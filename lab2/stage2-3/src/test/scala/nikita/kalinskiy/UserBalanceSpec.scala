package nikita.kalinskiy

import org.scalatest.{FlatSpec, Matchers}

class UserBalanceSpec extends FlatSpec with Matchers {
  "UserBalance(1,2,3) + UserBalance(2,3,4)" should "be equal to UserBalance(3,5,7)" in {
    val ub1 = UserBalance(1, 2, 3)
    val ub2 = UserBalance(2, 3, 4)
    ub1 + ub2 shouldEqual UserBalance(3, 5, 7)
  }

  "UserBalance(1,2,3) - UserBalance(2,3,4)" should "be equal to UserBalance(-1,-1,-1)" in {
    val ub1 = UserBalance(1, 2, 3)
    val ub2 = UserBalance(2, 3, 4)
    ub1 - ub2 shouldEqual UserBalance(-1, -1, -1)
  }

  "UserBalance(1,2,3) unary_-" should "be equal to UserBalance(-1,-2,-3)" in {
    val ub1 = UserBalance(1, 2, 3)
    ub1.unary_-() shouldEqual UserBalance(-1, -2, -3)
  }
}
