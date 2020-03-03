package nikita.kalinskiy

import org.scalatest.{FlatSpec, Matchers}

class OrderingListSpec extends FlatSpec with Matchers {
  "Result of OrderingList[Int] compare(List(1,2,3), List(5,4))" should "be equal Ordering.Greater" in {
    val result = OrderingList.compare(List(1, 2, 3), List(5, 4))
    result shouldEqual Ordering.Greater
  }

  "Result of OrderingList[Int] compare(List(-1,1), List(5,4,2))" should "be equal Ordering.Lesser" in {
    val result = OrderingList.compare(List(-1, 1), List(5, 4, 2))
    result shouldEqual Ordering.Lesser
  }

  "Result of OrderingList[Int] compare(List(1,2), List(5,4))" should "be equal Ordering.Equal" in {
    val result = OrderingList.compare(List(1, 2), List(5, 4))
    result shouldEqual Ordering.Equal
  }

  "Ordering.max[List[Int]](OrderingList)(List(1), List(2,3), List(4))" should "be equal to List(2,3)" in {
    val result = Ordering.max[List[Int]](OrderingList)(List(1), List(2, 3), List(4))
    result shouldEqual List(2, 3)
  }
}
