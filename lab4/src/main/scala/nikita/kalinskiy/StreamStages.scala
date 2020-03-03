package nikita.kalinskiy

import akka.stream.alpakka.csv.scaladsl.CsvFormatting
import akka.stream.scaladsl.Flow
import akka.util.ByteString

object StreamStages {
  // No need to test this – not implemented by us
  val formatter: Flow[Vector[String], ByteString, Any] = CsvFormatting.format()

  def filterBySymbol(symbol: Option[String]): Flow[Entry, Entry, Any] = symbol match {
    case None         => Flow[Entry]
    case Some(symbol) => Flow[Entry].filter(_.symbol == symbol)
  }

  def filterByVolume(c: Int): Flow[Entry, Entry, Any] =
    Flow[Entry].filter(_.bids.map(_._2).sum <= c).filter(_.asks.map(_._2).sum <= c)

  val instantMetrics: Flow[Entry, Metrics, Any] = Flow[Entry].map { entry =>
    val metrics = InstantMetrics.metrics(entry.bids, entry.asks)
    InstantMetrics(entry.timestamp, entry.symbol, metrics.head, metrics(1), metrics(2), metrics.last)
  }

  val smaMetrics: Flow[Seq[Entry], Metrics, Any] = Flow[Seq[Entry]].map { entries =>
    SmaMetrics(entries.head.timestamp, entries.last.timestamp, entries.head.symbol, SmaMetrics.sma(entries))
  }
}
