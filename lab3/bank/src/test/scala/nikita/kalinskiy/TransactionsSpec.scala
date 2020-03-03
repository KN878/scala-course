package nikita.kalinskiy

import nikita.kalinskiy.Currency.{RUB, USD}
import org.scalatest.{FlatSpec, Matchers}

class TransactionsSpec extends FlatSpec with Matchers {
  "Transactions" should "not be applied to the nonexistent account" in {
    val initialState = BankState(Map(2 -> BankAccount(2, RUB, 0)))
    val depositError =
      initialState.applyTransaction(Transaction.DepositMoney(0, RUB, 10)).left.getOrElse(TransactionError(""))
    depositError.message shouldEqual "No account with ID 0"

    val withdrawError =
      initialState.applyTransaction(Transaction.WithdrawMoney(0, RUB, 10)).left.getOrElse(TransactionError(""))
    withdrawError.message shouldEqual "No account with ID 0"

    val transferErrorFrom =
      initialState.applyTransaction(Transaction.TransferMoney(0, 2, RUB, 10)).left.getOrElse(TransactionError(""))
    transferErrorFrom.message shouldEqual "No account with ID 0"

    val transferErrorTo =
      initialState.applyTransaction(Transaction.TransferMoney(2, 0, RUB, 10)).left.getOrElse(TransactionError(""))
    transferErrorTo.message shouldEqual "No account with ID 0"

  }

  "Create account transaction" should "add new account to bank state" in {
    val initialState = BankState()
    val finalState   = initialState.applyTransaction(Transaction.CreateAccount(RUB)).getOrElse(initialState)
    finalState.accounts.size shouldEqual initialState.accounts.size + 1
    finalState.lastTransaction shouldEqual "New account in RUB, ID 0"
  }

  "Deposit money transaction" should "add money to the account with matching currency" in {
    val initialState = BankState(Map(0 -> BankAccount(0, USD, 0)))
    val finalState   = initialState.applyTransaction(Transaction.DepositMoney(0, USD, 10)).getOrElse(initialState)
    finalState.accounts(0).amount shouldEqual initialState.accounts(0).amount + 10
    finalState.lastTransaction shouldEqual "Deposited 10 USD to account 0, new balance: 10 USD"
  }

  "Deposit money transaction with wrong currency" should "return TransactionError" in {
    val initialState = BankState(Map(0 -> BankAccount(0, USD, 0)))
    val error        = initialState.applyTransaction(Transaction.DepositMoney(0, RUB, 10)).left.getOrElse(TransactionError(""))
    error.message shouldEqual "You can not deposit RUB, account is in USD"
  }

  "Withdraw money transaction" should "reduce amount of  money on the account with matching currency" in {
    val initialState = BankState(Map(0 -> BankAccount(0, USD, 10)))
    val finalState   = initialState.applyTransaction(Transaction.WithdrawMoney(0, USD, 10)).getOrElse(initialState)
    finalState.accounts(0).amount shouldEqual initialState.accounts(0).amount - 10
    finalState.lastTransaction shouldEqual "Withdrew 10 USD from account 0, new balance: 0 USD"
  }

  "Withdraw money transaction with wrong currency" should "return TransactionError" in {
    val initialState = BankState(Map(0 -> BankAccount(0, USD, 0)))
    val error =
      initialState.applyTransaction(Transaction.WithdrawMoney(0, RUB, 10)).left.getOrElse(TransactionError(""))
    error.message shouldEqual "You can not withdraw RUB, account is in USD"
  }

  "Withdrawal" should "not be available if there is not enough money on account" in {
    val initialState = BankState(Map(0 -> BankAccount(0, USD, 0)))
    val error =
      initialState.applyTransaction(Transaction.WithdrawMoney(0, USD, 10)).left.getOrElse(TransactionError(""))
    error.message shouldEqual "Not enough money on your account: 0 USD"
  }

  "Transfer of money" should "withdraw money from 'from' account and deposit it on 'to' account" in {
    val initialState = BankState(Map(0 -> BankAccount(0, USD, 10), 1 -> BankAccount(1, USD, 0)))
    val finalState   = initialState.applyTransaction(Transaction.TransferMoney(0, 1, USD, 10)).getOrElse(initialState)
    finalState.accounts(0).amount shouldEqual initialState.accounts(0).amount - 10
    finalState.accounts(1).amount shouldEqual initialState.accounts(1).amount + 10
    finalState.lastTransaction shouldEqual "Transferred 10 USD to account 1, new account 0 balance: 0 USD"
  }

  "Transfer of money" should "not be available if accounts is in different currencies" in {
    val initialState = BankState(Map(0 -> BankAccount(0, USD, 10), 1 -> BankAccount(1, RUB, 0)))
    val finalState =
      initialState.applyTransaction(Transaction.TransferMoney(0, 1, USD, 10)).left.getOrElse(TransactionError(""))
    finalState.message shouldEqual "You can not transfer USD, another party's account is in RUB"
  }

  "Transfer of money" should "not be available if 'to' account has not enough money" in {
    val initialState = BankState(Map(0 -> BankAccount(0, USD, 0), 1 -> BankAccount(1, USD, 0)))
    val finalState =
      initialState.applyTransaction(Transaction.TransferMoney(0, 1, USD, 10)).left.getOrElse(TransactionError(""))
    finalState.message shouldEqual "Not enough money on your account: 0 USD"
  }
}
