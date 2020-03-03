package nikita.kalinskiy

sealed trait Command
object Command {
  object Exit                                         extends Command
  final case class PrintHistory(id: Int)              extends Command
  final case class TransactionCommand(t: Transaction) extends Command
}
