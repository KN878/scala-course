package nikita.kalinskiy

import org.scalatest.{FlatSpec, Matchers}

class RationalSpec extends FlatSpec with Matchers {
  "==" should "be correct for 2/1 and 4/2" in {
    val isEqual = Rational(2, 1) == Rational(4, 2)
    isEqual shouldBe true
  }

  "2/3 * 5/3" should "be equal to 10/9" in {
    val result = Rational(2, 3) * Rational(5, 3)
    result == Rational(10, 9) shouldBe true
  }

  "-1/2 + 2/2" should "be equal to 1/2" in {
    val result = Rational(-1, 2) + Rational(2, 2)
    result == Rational(1, 2) shouldBe true
  }
}
