package nikita.kalinskiy

final case class BankAccount(id: Int, currency: Currency, amount: Int, history: List[String] = List("Creation"))
