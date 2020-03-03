package nikita.kalinskiy

import java.nio.file.Paths
import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.alpakka.csv.scaladsl._
import akka.stream.scaladsl._

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.Duration

object Main extends App {

  import StreamStages._

  implicit val actors: ActorSystem                = ActorSystem()
  implicit val executionContext: ExecutionContext = actors.dispatcher

  val source: Source[Entry, Any] = FileIO
    .fromPath(Paths.get("snapshots.csv"))
    .via(CsvParsing.lineScanner())
    .map(i => Entry.fromLine(i.map(_.utf8String)))
    .collect { case Some(v) => v }

  val routes: Route = concat(
    (path(InstantMetrics.path) & parameter("symbol".as[String].?) & parameter("c".as[Int] ? 1000000)) { (symbol, c) =>
      get {
        val instantMetricsStream = source
          .via(filterBySymbol(symbol))
          .via(filterByVolume(c))
          .via(instantMetrics)
          .map(_.toVector)
          .prepend(Source.single(InstantMetrics.header))
          .via(formatter)
        complete(HttpEntity(ContentTypes.`text/plain(UTF-8)`, instantMetricsStream))
      }
    },
    pathPrefix("continuous-metrics") {
      concat(
        (path(SmaMetrics.path) & parameter("symbol".as[String].?) & parameter("n".as[Int] ? 50)) { (symbol, n) =>
          get {
            val smaMetricsStream = source
              .via(filterBySymbol(symbol))
              .sliding(n)
              .via(smaMetrics)
              .map(_.toVector)
              .prepend(Source.single(SmaMetrics.header))
              .via(formatter)
            complete(HttpEntity(ContentTypes.`text/plain(UTF-8)`, smaMetricsStream))
          }
        },
        (path(EmaMetrics.path) & parameter("symbol".as[String].?) & parameter("n".as[Int] ? 50)) {
          (symbol, n) =>
            get {
              val emaMetricsStream = source
                .via(filterBySymbol(symbol))
                .sliding(n)
                .map(entries => {
                  val emas = entries.scanLeft((0.0, 0)) { (res, entry) =>
                    val n     = res._2 + 1
                    val alpha = 2.0 / (n + 1)
                    val vwap  = Metrics.vwapMid(entry)
                    val ema   = alpha * vwap + (1 - alpha) * res._1
                    (ema, n)
                  }
                  (
                    entries.head.timestamp.toString,
                    entries.last.timestamp.toString,
                    entries.head.symbol,
                    emas.last._1.toString
                  )
                })
                .map(ema => Vector(ema._1, ema._2, ema._3, ema._4))
                .prepend(Source.single(EmaMetrics.header))
                .via(formatter)
              complete(HttpEntity(ContentTypes.`text/plain(UTF-8)`, emaMetricsStream))
            }
        }
      )
    }
  )

  for {
    binding <- Http().bindAndHandle(routes, "localhost", 8080)
    _ = sys.addShutdownHook {
      for {
        _ <- binding.terminate(Duration(5, TimeUnit.SECONDS))
        _ <- actors.terminate()
      } yield ()
    }
  } yield ()
}
