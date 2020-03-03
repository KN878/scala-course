package nikita.kalinskiy

import java.time.LocalDateTime

sealed abstract class Metrics(timestamp: LocalDateTime, symbol: String) {
  def toVector: Vector[String]
}

final case class InstantMetrics(
    timestamp: LocalDateTime,
    symbol: String,
    midPoint: Double,
    vwapAsk: Double,
    vwapBid: Double,
    vwapMidPoint: Double
) extends Metrics(timestamp, symbol) {
  override def toVector: Vector[String] =
    Vector(timestamp.toString, symbol, midPoint.toString, vwapAsk.toString, vwapBid.toString, vwapMidPoint.toString)
}

final case class SmaMetrics(
    timestampStart: LocalDateTime,
    timestampEnd: LocalDateTime,
    symbol: String,
    sma: Double,
) extends Metrics(timestampStart, symbol) {
  override def toVector: Vector[String] = Vector(
    timestampStart.toString,
    timestampEnd.toString,
    symbol,
    sma.toString,
  )
}

final case class EmaMetrics(timestamp: LocalDateTime, symbol: String, ema: Double) extends Metrics(timestamp, symbol) {
  override def toVector: Vector[String] = Vector(
    timestamp.toString,
    symbol,
    ema.toString,
  )
}

object Metrics {
  def vwap(prices: Vector[(Double, Int)]): Double = {
    val (volumedPrice, totalVolume) =
      prices.reduceLeft((sum: (Double, Int), price: (Double, Int)) => (sum._1 + price._1 * price._2, sum._2 + price._2))
    volumedPrice / totalVolume
  }

  def vwapMidSeq(entries: Seq[Entry]): Seq[Double] = {
    for { entry <- entries } yield { (Metrics.vwap(entry.bids) + Metrics.vwap(entry.asks)) / 2 }
  }

  def vwapMid(entry: Entry): Double = (vwap(entry.asks) + vwap(entry.bids)) / 2
}

object InstantMetrics {

  /**
    * Calculates Midpoint, VWAP for Asks, VWAP for Bids, VWAPs midpoint
    */
  def metrics(bids: Vector[(Double, Int)], asks: Vector[(Double, Int)]): List[Double] = {
    val vwapBids = Metrics.vwap(bids)
    val vwapAsks = Metrics.vwap(asks)
    List((bids.last._1 + asks.head._1) / 2, vwapAsks, vwapBids, (vwapAsks + vwapBids) / 2)
  }

  val header: Vector[String] = Vector("timestamp", "symbol", "mid_point", "vmap_asks", "vwap_bids", "vwap_mid_point")

  val path: String = "instant-metrics"
}

object EmaMetrics {
  val path: String = "ema"

  val header: Vector[String] =
    Vector("timestamp_start", "timestamp_end", "symbol", "ema")
}

object SmaMetrics {
  def sma(entries: Seq[Entry]): Double = {
    val vmapsMid = Metrics.vwapMidSeq(entries)
    vmapsMid.sum / vmapsMid.size
  }

  val path: String = "sma"

  val header: Vector[String] =
    Vector("timestamp_start", "timestamp_end", "symbol", "sma")

}
