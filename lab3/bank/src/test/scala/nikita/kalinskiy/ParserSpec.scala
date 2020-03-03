package nikita.kalinskiy

import nikita.kalinskiy.Currency.{RUB, USD}
import nikita.kalinskiy.Transaction.{CreateAccount, DepositMoney, TransferMoney, WithdrawMoney}
import org.scalatest.{FlatSpec, Matchers}

class ParserSpec extends FlatSpec with Matchers {
  "'Parse currency from input string'" should "work only with 'RUB'/'USD'" in {
    Main.parseCurrencyFromString("RUB") shouldEqual Right(RUB)
    Main.parseCurrencyFromString("USD") shouldEqual Right(USD)
    Main.parseCurrencyFromString("EUR") shouldEqual Left(ParseError(s"Unsupported currency EUR"))
  }

  "Parsing of 'exit' command" should "return Right(Command.Exit)" in {
    Main.parseCommand("exit") shouldEqual Right(Command.Exit)
  }

  "Parsing of 'open account in RUB/USD' command" should "work with supported currencies only" in {
    Main.parseCommand("open account in RUB") shouldEqual Right(Command.TransactionCommand(CreateAccount(RUB)))
    Main.parseCommand("open account in USD") shouldEqual Right(Command.TransactionCommand(CreateAccount(USD)))
    Main.parseCommand("open account in EUR") shouldEqual Left(ParseError(s"Unknown input: open account in EUR"))
  }

  "Parsing of 'deposit' command" should "work with supported currencies only" in {
    Main.parseCommand("deposit 10 RUB to account 0") shouldEqual Right(
      Command.TransactionCommand(DepositMoney(0, RUB, 10))
    )
    Main.parseCommand("deposit 10 USD to account 0") shouldEqual Right(
      Command.TransactionCommand(DepositMoney(0, USD, 10))
    )
    Main.parseCommand("deposit 10 EUR to account 0") shouldEqual Left(
      ParseError(s"Unknown input: deposit 10 EUR to account 0")
    )
  }

  "Parsing of 'withdraw' command" should "work with supported currencies only" in {
    Main.parseCommand("withdraw 10 RUB from account 0") shouldEqual Right(
      Command.TransactionCommand(WithdrawMoney(0, RUB, 10))
    )
    Main.parseCommand("withdraw 10 USD from account 0") shouldEqual Right(
      Command.TransactionCommand(WithdrawMoney(0, USD, 10))
    )
    Main.parseCommand("withdraw 10 EUR from account 0") shouldEqual Left(
      ParseError(s"Unknown input: withdraw 10 EUR from account 0")
    )
  }

  "Parsing of 'transfer' command" should "work with supported currencies only" in {
    Main.parseCommand("transfer 10 RUB from account 1 to account 0") shouldEqual Right(
      Command.TransactionCommand(TransferMoney(1, 0, RUB, 10))
    )
    Main.parseCommand("transfer 10 USD from account 1 to account 0") shouldEqual Right(
      Command.TransactionCommand(TransferMoney(1, 0, USD, 10))
    )
    Main.parseCommand("transfer 10 EUR from account 1 to account 0") shouldEqual Left(
      ParseError(s"Unknown input: transfer 10 EUR from account 1 to account 0")
    )
  }

  "Parsing of 'history' command" should "return Right(Command.PrintHistory)" in {
    Main.parseCommand("history of account 0") shouldEqual Right(Command.PrintHistory(0))
  }
}
