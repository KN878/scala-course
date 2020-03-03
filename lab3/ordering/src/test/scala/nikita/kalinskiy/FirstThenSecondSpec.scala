package nikita.kalinskiy

import org.scalatest.{FlatSpec, Matchers}

class FirstThenSecondSpec extends FlatSpec with Matchers {
  "Ordering compare " should "work" in {
    new FirstThenSecond(OrderingInt, OrderingInt).compare((1, 1), (1, 2)) shouldEqual Ordering.Lesser
    new FirstThenSecond(OrderingInt, OrderingInt).compare((1, 1), (0, 1)) shouldEqual Ordering.Greater
    new FirstThenSecond(OrderingInt, OrderingInt).compare((1, 3), (1, 3)) shouldEqual Ordering.Equal

    new FirstThenSecond(OrderingList, OrderingList)
      .compare((List(1, 1), List(1, 2)), (List(0, 1), List(1, 3, 4))) shouldEqual Ordering.Lesser
    new FirstThenSecond(OrderingList, OrderingList)
      .compare((List(1, 1), List(1, 2, 2, 2)), (List(1, 1), List(1, 3, 4))) shouldEqual Ordering.Greater
    new FirstThenSecond(OrderingList, OrderingList)
      .compare((List(1, 1), List(1, 2)), (List(1, 1), List(1, 3))) shouldEqual Ordering.Equal

    new FirstThenSecond(OrderingString, OrderingString)
      .compare(("abc", "def"), ("acd", "def")) shouldEqual Ordering.Lesser
    new FirstThenSecond(OrderingString, OrderingString)
      .compare(("qwer", "def"), ("acd", "def")) shouldEqual Ordering.Greater
    new FirstThenSecond(OrderingString, OrderingString)
      .compare(("abc", "def"), ("abc", "def")) shouldEqual Ordering.Equal

    new FirstThenSecond(OrderingString, OrderingString)
      .compare(("scala", "rulit"), ("java", "soset")) shouldEqual Ordering.Greater
  }

  "Ordering max " should "work" in {
    Ordering.max(new FirstThenSecond(OrderingInt, OrderingInt))((1, 1), (1, 2)) shouldEqual (1, 2)
    Ordering.max(new FirstThenSecond(OrderingInt, OrderingInt))((1, 1), (0, 1)) shouldEqual (1, 1)
    Ordering.max(new FirstThenSecond(OrderingInt, OrderingInt))((1, 3), (1, 3)) shouldEqual (1, 3)

    Ordering.max(new FirstThenSecond(OrderingList, OrderingList))((List(1, 1), List(1, 2)), (List(0, 1), List(1, 3, 4))) shouldEqual (List(
      0,
      1
    ), List(1, 3, 4))

    Ordering.max(new FirstThenSecond(OrderingString, OrderingString))(("abc", "def"), ("acd", "def")) shouldEqual ("acd", "def")
    Ordering.max(new FirstThenSecond(OrderingString, OrderingString))(("qwer", "def"), ("acd", "def")) shouldEqual ("qwer", "def")
    Ordering.max(new FirstThenSecond(OrderingString, OrderingString))(("abc", "def"), ("abc", "def")) shouldEqual ("abc", "def")

    Ordering.max(new FirstThenSecond(OrderingString, OrderingString))(("scala", "rulit"), ("java", "soset")) shouldEqual ("scala", "rulit")
  }
}
