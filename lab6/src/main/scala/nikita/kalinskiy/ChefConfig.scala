package nikita.kalinskiy

import com.typesafe.config.ConfigFactory

class ChefConfig {
  private val config = ConfigFactory.load()

  private val beefTimeRange: (Double, Double) =
    (config.getInt("khinkalnaya.cooking-time.beefStart"), config.getInt("khinkalnaya.cooking-time.beefEnd"))
  private val muttonTimeRange: (Double, Double) =
    (config.getInt("khinkalnaya.cooking-time.muttonStart"), config.getInt("khinkalnaya.cooking-time.muttonEnd"))
  private val cheeseAndMushroomsTimeRange: (Double, Double) =
    (
      config.getInt("khinkalnaya.cooking-time.cheeseAndMushroomsStart"),
      config.getInt("khinkalnaya.cooking-time.cheeseAndMushroomsEnd")
    )

  def getBeefTime: (Double, Double)               = beefTimeRange
  def getMuttonTime: (Double, Double)             = muttonTimeRange
  def getCheeseAndMushroomsTime: (Double, Double) = cheeseAndMushroomsTimeRange
}

object ChefConfig {
  def apply(): ChefConfig = new ChefConfig()
}
