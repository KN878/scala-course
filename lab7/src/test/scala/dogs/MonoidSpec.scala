package dogs

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class MonoidSpec extends AnyFlatSpec with Matchers {
  "Semigroup" should "combine elements properly" in {
    Semigroup[Int].combine(3, 2) shouldEqual 5
    Semigroup[Long].combine(3L, 50L) shouldEqual 53L
    Semigroup[Float].combine(3.3f, -1f) shouldEqual 2.3f
    Semigroup[Double].combine(0.5, 0.6) shouldEqual 1.1
    Semigroup[List[Int]].combine(List(1, 2, 3), List(4, 5, 6)) shouldEqual List(1, 2, 3, 4, 5, 6)
    Semigroup[Map[Int, Int]].combine(Map(1 -> 2, 3 -> 4), Map(1 -> 2, 5 -> 6)) shouldEqual Map(1 -> 2, 3 -> 4, 5 -> 6)
  }

  "Monoid's combine with unit" should "return the same value as non-unit element" in {
    Monoid[Int].combine(1, Monoid[Int].unit) shouldEqual 1
    Monoid[Long].combine(2L, Monoid[Long].unit) shouldEqual 2L
    Monoid[Float].combine(3.3F, Monoid[Float].unit) shouldEqual 3.3F
    Monoid[Double].combine(0.1, Monoid[Double].unit) shouldEqual 0.1
    Monoid[List[Int]].combine(List(1,2,3), Monoid[List[Int]].unit) shouldEqual List(1,2,3)
    Monoid[Map[Int, Int]].combine(Map(1->2, 3->4), Monoid[Map[Int, Int]].unit) shouldEqual Map(1->2, 3->4)
  }

  "CommutativeMonoid's combine of two maps" should "work properly" in {
    CommutativeMonoid[Map[Int, Int]].combine(Map(1->2, 2->3), Map(1->3)) shouldEqual Map(1->5, 2->3)
  }


}
