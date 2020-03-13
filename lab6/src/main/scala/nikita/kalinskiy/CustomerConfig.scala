package nikita.kalinskiy

import com.typesafe.config.ConfigFactory

class CustomerConfig {
  private val config = ConfigFactory.load()

  private val decidingTimeRange: (Double, Double) =
    (
      config.getInt("khinkalnaya.random.customer.decidingTimeStart"),
      config.getInt("khinkalnaya.random.customer.decidingTimeEnd")
    )
  private val eatingTimeRange: (Double, Double) =
    (
      config.getInt("khinkalnaya.random.customer.eatingTimeStart"),
      config.getInt("khinkalnaya.random.customer.eatingTimeEnd")
    )

  def getDecidingTime: (Double, Double) = decidingTimeRange
  def getEatingTime: (Double, Double)   = eatingTimeRange
}

object CustomerConfig {
  def apply(): CustomerConfig = new CustomerConfig()
}
