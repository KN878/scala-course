package nikita.kalinskiy

sealed trait Transaction
object Transaction {
  final case class CreateAccount(currency: Currency)                                  extends Transaction
  final case class DepositMoney(accountId: Int, currency: Currency, amount: Int)      extends Transaction
  final case class WithdrawMoney(accountId: Int, currency: Currency, amount: Int)     extends Transaction
  final case class TransferMoney(from: Int, to: Int, currency: Currency, amount: Int) extends Transaction
}
