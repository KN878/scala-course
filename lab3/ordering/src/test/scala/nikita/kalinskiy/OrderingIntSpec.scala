package nikita.kalinskiy

import org.scalatest.{FlatSpec, Matchers}

class OrderingIntSpec extends FlatSpec with Matchers {
  "Result of OrderingInt compare(3, 4)" should "be equal Ordering.Lesser" in {
    val result = OrderingInt.compare(3, 4)
    result shouldEqual Ordering.Lesser
  }

  "Result of OrderingInt compare(4, 3)" should "be equal Ordering.Greater" in {
    val result = OrderingInt.compare(4, 3)
    result shouldEqual Ordering.Greater
  }

  "Result of OrderingInt compare(3, 3)" should "be equal Ordering.Equal" in {
    val result = OrderingInt.compare(3, 3)
    result shouldEqual Ordering.Equal
  }

  "Ordering.max(OrderingInt)(5, 1, 2, 3)" should "be equal to 5" in {
    val result = Ordering.max(OrderingInt)(5, 1, 2, 3)
    result shouldEqual 5
  }
}
