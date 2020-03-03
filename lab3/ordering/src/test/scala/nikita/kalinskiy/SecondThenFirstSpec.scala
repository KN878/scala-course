package nikita.kalinskiy

import org.scalatest.{FlatSpec, Matchers}

class SecondThenFirstSpec extends FlatSpec with Matchers {
  "Ordering compare " should "work" in {
    new SecondThenFirst(OrderingInt, OrderingInt).compare((1, 1), (1, 2)) shouldEqual Ordering.Lesser
    new SecondThenFirst(OrderingInt, OrderingInt).compare((1, 1), (0, 1)) shouldEqual Ordering.Greater
    new SecondThenFirst(OrderingInt, OrderingInt).compare((1, 3), (1, 3)) shouldEqual Ordering.Equal

    new SecondThenFirst(OrderingList, OrderingList)
      .compare((List(1, 1), List(1, 2)), (List(0, 1), List(1, 3, 4))) shouldEqual Ordering.Lesser
    new SecondThenFirst(OrderingList, OrderingList)
      .compare((List(1, 1), List(1, 2, 3, 4)), (List(1, 1), List(1, 3, 4))) shouldEqual Ordering.Greater
    new SecondThenFirst(OrderingList, OrderingList)
      .compare((List(1, 1), List(1, 2)), (List(1, 1), List(1, 3))) shouldEqual Ordering.Equal

    new SecondThenFirst(OrderingString, OrderingString)
      .compare(("abc", "def"), ("acd", "def")) shouldEqual Ordering.Lesser
    new SecondThenFirst(OrderingString, OrderingString)
      .compare(("qwer", "def"), ("acd", "def")) shouldEqual Ordering.Greater
    new SecondThenFirst(OrderingString, OrderingString)
      .compare(("abc", "def"), ("abc", "def")) shouldEqual Ordering.Equal

    new SecondThenFirst(OrderingString, OrderingString)
      .compare(("soset", "java"), ("rulit", "scala")) shouldEqual Ordering.Lesser

  }

  "Ordering max " should "work" in {
    Ordering.max(new SecondThenFirst(OrderingInt, OrderingInt))((1, 1), (1, 2)) shouldEqual (1, 2)
    Ordering.max(new SecondThenFirst(OrderingInt, OrderingInt))((1, 1), (0, 1)) shouldEqual (1, 1)
    Ordering.max(new SecondThenFirst(OrderingInt, OrderingInt))((1, 3), (1, 3)) shouldEqual (1, 3)

    Ordering.max(new SecondThenFirst(OrderingList, OrderingList))((List(1, 1), List(1, 2)), (List(0, 1), List(1, 3, 4))) shouldEqual (List(
      0,
      1
    ), List(1, 3, 4))

    Ordering.max(new SecondThenFirst(OrderingString, OrderingString))(("abc", "def"), ("acd", "def")) shouldEqual ("acd", "def")
    Ordering.max(new SecondThenFirst(OrderingString, OrderingString))(("qwer", "def"), ("acd", "def")) shouldEqual ("qwer", "def")
    Ordering.max(new SecondThenFirst(OrderingString, OrderingString))(("abc", "def"), ("abc", "def")) shouldEqual ("abc", "def")

    Ordering.max(new SecondThenFirst(OrderingString, OrderingString))(("soset", "java"), ("rulit", "scala")) shouldEqual ("rulit", "scala")
  }
}
