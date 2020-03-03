package nikita.kalinskiy

import nikita.kalinskiy.Transaction.{CreateAccount, DepositMoney, WithdrawMoney}

final case class BankState(accounts: Map[Int, BankAccount] = Map(), lastTransaction: String = "") {
  def createAccount(currency: Currency): Either[TransactionError, BankState] = {
    val newId           = if (accounts.isEmpty) 0 else accounts.size
    val lastTransaction = s"New account in ${currency.toString}, ID $newId"
    Right(copy(accounts + (newId -> BankAccount(newId, currency, 0)), lastTransaction))
  }

  def depositMoney(accountId: Int, currency: Currency, amount: Int): Either[TransactionError, BankState] = {
    if (currency != accounts(accountId).currency)
      return Left(
        TransactionError(
          s"You can not deposit ${currency.toString}, account is in ${accounts(accountId).currency.toString}"
        )
      )

    val currentAccount = accounts(accountId)
    val newAccount = BankAccount(
      accountId,
      currency,
      currentAccount.amount + amount,
      currentAccount.history :+ s"Deposition of $amount ${currency.toString}"
    )
    val lastTransaction =
      s"Deposited $amount ${currency.toString} to account $accountId, new balance: ${newAccount.amount} ${currency.toString}"
    Right(copy(accounts + (accountId -> newAccount), lastTransaction))
  }

  def withdrawMoney(accountId: Int, currency: Currency, amount: Int): Either[TransactionError, BankState] = {
    if (currency != accounts(accountId).currency) {
      return Left(
        TransactionError(
          s"You can not withdraw ${currency.toString}, account is in ${accounts(accountId).currency.toString}"
        )
      )
    }

    val currentAccount = accounts(accountId)
    if (currentAccount.amount - amount < 0) {
      Left(TransactionError(s"Not enough money on your account: ${currentAccount.amount} ${currency.toString}"))
    } else {
      val newAccount = BankAccount(
        accountId,
        currency,
        currentAccount.amount - amount,
        currentAccount.history :+ s"Withdrawal of $amount ${currency.toString}"
      )
      val lastTransaction =
        s"Withdrew $amount ${currency.toString} from account $accountId, new balance: ${newAccount.amount} ${currency.toString}"
      Right(copy(accounts + (accountId -> newAccount), lastTransaction))
    }
  }

  def transferMoney(from: Int, to: Int, currency: Currency, amount: Int): Either[TransactionError, BankState] = {
    val fromAccount = accounts(from)
    val toAccount   = accounts(to)

    if (currency != fromAccount.currency)
      return Left(
        TransactionError(s"You can not transfer ${currency.toString}, account is in ${fromAccount.currency.toString}")
      )
    if (currency != toAccount.currency)
      return Left(
        TransactionError(
          s"You can not transfer ${currency.toString}, another party's account is in ${toAccount.currency.toString}"
        )
      )

    if (fromAccount.amount - amount < 0) {
      Left(
        TransactionError(s"Not enough money on your account: ${fromAccount.amount} ${fromAccount.currency.toString}")
      )
    } else {
      val newFromAccount = BankAccount(
        from,
        currency,
        fromAccount.amount - amount,
        fromAccount.history :+ s"Transfer of $amount ${fromAccount.currency.toString} to account $to"
      )
      val newToAccount = BankAccount(
        to,
        currency,
        toAccount.amount + amount,
        toAccount.history :+ s"Transfer of $amount ${fromAccount.currency.toString} from account $from"
      )
      val lastTransaction =
        s"Transferred $amount ${currency.toString} to account $to, new account $from balance: ${newFromAccount.amount} ${currency.toString}"
      Right(copy(accounts + (from -> newFromAccount, to -> newToAccount), lastTransaction))
    }

  }

  def applyTransaction(transaction: Transaction): Either[TransactionError, BankState] = {
    transaction match {
      case CreateAccount(currency) => createAccount(currency)
      case DepositMoney(accountId, currency, amount) =>
        if (!accounts.contains(accountId)) return Left(TransactionError(s"No account with ID $accountId"))
        depositMoney(accountId, currency, amount)
      case WithdrawMoney(accountId, currency, amount) =>
        if (!accounts.contains(accountId)) return Left(TransactionError(s"No account with ID $accountId"))
        withdrawMoney(accountId, currency, amount)
      case Transaction.TransferMoney(from, to, currency, amount) =>
        if (!accounts.contains(from)) return Left(TransactionError(s"No account with ID $from"))
        if (!accounts.contains(to)) return Left(TransactionError(s"No account with ID $to"))
        transferMoney(from, to, currency, amount)
    }
  }
}
