package dogs.syntax.monoid

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import dogs._

class MonoidSyntaxSpec extends AnyFlatSpec with Matchers{
  "|+|" should "word for all possible semigroup instances" in {
    1 |+| 1 shouldEqual 2
    1L |+| 1L shouldEqual 2
    0.1F |+| 0.1F shouldEqual 0.2F
    0.5 |+| 0.6 shouldEqual 1.1
    List(1,2,3) |+| List(3,4,5) shouldEqual List(1,2,3,3,4,5)
    Map(1->2, 2->3) |+| Map(1->2, 3->4) shouldEqual Map(1->2, 2->3, 3->4)
  }

  "Monoid's reduce" should "work properly" in {
    List(1,2,3).reduceMonoid shouldEqual 6
    List(Map(1->2, 2->3), Map(1->3, 3->4)).reduceMonoid shouldEqual Map(1->3, 2->3, 3->4)
  }

  "Monoid's reduce" should "be safe to use" in {
    List.empty[Int].reduceMonoid shouldEqual 0
  }

  "Monoid's foldSemigroup" should "work properly" in {
    List(1,2,3).foldSemigroup(1) shouldEqual 7
    List(Map(1->2, 2->3), Map(1->3, 3->4)).foldSemigroup(Map(7->8)) shouldEqual Map(1->3, 2->3, 3->4, 7->8)
  }


  "Monoid's foldLeftSemigroup" should "work properly" in {
    List(1,2,3).foldLeftSemigroup(1) shouldEqual 7
    List(Map(1->2, 2->3), Map(1->3, 3->4)).foldLeftSemigroup(Map(7->8)) shouldEqual Map(1->3, 2->3, 3->4, 7->8)
  }


  "Monoid's foldRightSemigroup" should "work properly" in {
    List(1,2,3).foldRightSemigroup(1) shouldEqual 7
    List(Map(1->2, 2->3), Map(1->3, 3->4)).foldRightSemigroup(Map(7->8)) shouldEqual Map(1->3, 2->3, 3->4, 7->8)
  }
}