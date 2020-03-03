package nikita.kalinskiy

import org.scalatest.{FlatSpec, Matchers}

class OrderingStringSpec extends FlatSpec with Matchers {
  "Result of OrderingString compare('abc', 'xyz')" should "be equal Ordering.Lesser" in {
    val result = OrderingString.compare("abc", "xyz")
    result shouldEqual Ordering.Lesser
  }

  "Result of OrderingString compare('xyz', 'abc')" should "be equal Ordering.Greater" in {
    val result = OrderingString.compare("xyz", "abc")
    result shouldEqual Ordering.Greater
  }

  "Result of OrderingString compare('abc', 'abc')" should "be equal Ordering.Equal" in {
    val result = OrderingString.compare("abc", "abc")
    result shouldEqual Ordering.Equal
  }

  "Ordering.max(StringOrdering)(\"kek\", \"lol\", \"aaa\")" should "be equal 'lol'" in {
    val result = Ordering.max(OrderingString)("kek", "lol", "aaa")
    result shouldEqual "lol"
  }
}
