package nikita.kalinskiy

import com.typesafe.config.ConfigFactory

class CafeConfig {
  private val config = ConfigFactory.load()

  private val customers: Int = config.getInt("khinkalnaya.customers")
  private val chefs: Int     = config.getInt("khinkalnaya.chefs")
  private val seed: Long     = config.getLong("khinkalnaya.random.seed")

  def getCustomers: Int = customers
  def getChefs: Int     = chefs
  def getSeed: Long     = seed
}

object CafeConfig {
  def apply(): CafeConfig = new CafeConfig()
}
