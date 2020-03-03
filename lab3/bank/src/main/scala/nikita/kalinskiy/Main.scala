package nikita.kalinskiy

import nikita.kalinskiy.Currency.{RUB, USD}
import nikita.kalinskiy.Transaction.{CreateAccount, DepositMoney, TransferMoney, WithdrawMoney}

import scala.annotation.tailrec
import scala.io.StdIn

object Main extends App {
  def parseOpenAccountCommand(currencyStr: String): Option[Command] = {
    parseCurrencyFromString(currencyStr) match {
      case Right(currency) => Some(Command.TransactionCommand(CreateAccount(currency)))
      case Left(ParseError(message)) =>
        println(message)
        None
    }
  }

  def parseDepositMoneyCommand(accountId: String, currencyStr: String, amount: String): Option[Command] = {
    parseCurrencyFromString(currencyStr) match {
      case Right(currency) => Some(Command.TransactionCommand(DepositMoney(accountId.toInt, currency, amount.toInt)))
      case Left(ParseError(message)) =>
        println(message)
        None
    }
  }

  def parseWithdrawMoneyCommand(accountId: String, currencyStr: String, amount: String): Option[Command] = {
    parseCurrencyFromString(currencyStr) match {
      case Right(currency) => Some(Command.TransactionCommand(WithdrawMoney(accountId.toInt, currency, amount.toInt)))
      case Left(ParseError(message)) =>
        println(message)
        None
    }
  }

  def parseTransferMoneyCommand(from: String, to: String, currencyStr: String, amount: String): Option[Command] = {
    parseCurrencyFromString(currencyStr) match {
      case Right(currency) =>
        Some(Command.TransactionCommand(TransferMoney(from.toInt, to.toInt, currency, amount.toInt)))
      case Left(ParseError(message)) =>
        println(message)
        None
    }
  }

  def parseCurrencyFromString(currency: String): Either[ParseError, Currency] = {
    currency match {
      case "RUB" => Right(RUB)
      case "USD" => Right(USD)
      case _     => Left(ParseError(s"Unsupported currency $currency"))
    }

  }

  def parseCommand(input: String): Either[ParseError, Command] = {
    val parsed = input match {
      case "exit"                                             => Some(Command.Exit)
      case s"open account in $currencyStr"                    => parseOpenAccountCommand(currencyStr)
      case s"deposit $amount $currency to account $accountId" => parseDepositMoneyCommand(accountId, currency, amount)
      case s"withdraw $amount $currency from account $accountId" =>
        parseWithdrawMoneyCommand(accountId, currency, amount)
      case s"transfer $amount $currency from account $from to account $to" =>
        parseTransferMoneyCommand(from, to, currency, amount)
      case s"history of account $id" => id.toIntOption.map(Command.PrintHistory)
      case _                         => None
    }
    parsed match {
      case Some(cmd) => Right(cmd)
      case _         => Left(ParseError(s"Unknown input: $input"))
    }
  }

  def printAccountHistory(state: BankState, accountId: Int): Unit = {
    if (!state.accounts.contains(accountId)) {
      println(s"No account with ID $accountId")
      return
    }

    val accountHistory = state.accounts(accountId).history
    val currency       = state.accounts(accountId).currency
    println(s"Operations of account $accountId (${currency.toString}):")
    for (operation <- accountHistory) {
      println(s"- $operation")
    }
  }

  @tailrec def mainLoop(state: BankState): Unit = {
    print("> ")
    val input = StdIn.readLine()
    val newState = parseCommand(input) match {
      case Left(ParseError(message)) =>
        println(message)
        state
      case Right(Command.Exit) =>
        return
      case Right(Command.PrintHistory(accountId)) =>
        printAccountHistory(state, accountId)
        state
      case Right(Command.TransactionCommand(transaction)) =>
        state.applyTransaction(transaction) match {
          case Left(TransactionError(message)) =>
            println(message)
            state
          case Right(value) =>
            println(value.lastTransaction)
            value
        }
    }
    mainLoop(newState)
  }
  mainLoop(BankState())
}
