package nikita.kalinskiy

import com.typesafe.config.ConfigFactory

object Settings {
  private val config = ConfigFactory.load()

  val customers: Int = config.getInt("khinkalnaya.customers")
  val chefs: Int     = config.getInt("khinkalnaya.chefs")
  val seed: Long     = config.getLong("khinkalnaya.random.seed")
  val beefTimeRange: (Double, Double) =
    (config.getInt("khinkalnaya.cooking-time.beefStart"), config.getInt("khinkalnaya.cooking-time.beefEnd"))
  val muttonTimeRange: (Double, Double) =
    (config.getInt("khinkalnaya.cooking-time.muttonStart"), config.getInt("khinkalnaya.cooking-time.muttonEnd"))
  val cheeseAndMushroomsTimeRange: (Double, Double) =
    (
      config.getInt("khinkalnaya.cooking-time.cheeseAndMushroomsStart"),
      config.getInt("khinkalnaya.cooking-time.cheeseAndMushroomsEnd")
    )
  val decidingTimeRange: (Double, Double) =
    (
      config.getInt("khinkalnaya.random.customer.decidingTimeStart"),
      config.getInt("khinkalnaya.random.customer.decidingTimeEnd")
    )
  val eatingTimeRange: (Double, Double) =
    (
      config.getInt("khinkalnaya.random.customer.eatingTimeStart"),
      config.getInt("khinkalnaya.random.customer.eatingTimeEnd")
    )
}
