package nikita.kalinskiy

sealed trait Currency
object Currency {
  object USD extends Currency {
    override def toString: String = "USD"
  }
  object RUB extends Currency {
    override def toString: String = "RUB"
  }
}
