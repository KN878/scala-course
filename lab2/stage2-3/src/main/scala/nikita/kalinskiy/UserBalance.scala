package nikita.kalinskiy

case class UserBalance(usd: Double, eur: Double, pound: Double) {
  def +(ub: UserBalance): UserBalance = UserBalance(this.usd + ub.usd, this.eur + ub.eur, this.pound + ub.pound)

  def -(ub: UserBalance): UserBalance = UserBalance(this.usd - ub.usd, this.eur - ub.eur, this.pound - ub.pound)

  def unary_-(): UserBalance = UserBalance(-usd, -eur, -pound)
}

object UserBalance {
  def apply(usd: Double, eur: Double, pound: Double) = new UserBalance(usd, eur, pound)

  def unapply(ub: UserBalance): Some[(Double, Double, Double)] = Some(ub.usd, ub.eur, ub.eur)
}