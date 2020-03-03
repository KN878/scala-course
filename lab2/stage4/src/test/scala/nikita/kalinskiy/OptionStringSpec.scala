package nikita.kalinskiy

import org.scalatest.{FlatSpec, Matchers}

class OptionStringSpec extends FlatSpec with Matchers {
  "SomeString(\"abcd\").filter(value => value.contains(\"a\"))" should "be SomeString(\"abcd\")" in {
    val optionString: OptionString = SomeString("abcd")
    val result = optionString.filter(value => value.contains("a"))
    result shouldEqual SomeString("abcd")
  }

  "SomeString(\"abcd\").filter(value.contains(\"e\"))" should "be NoneString" in {
    val optionString: OptionString = SomeString("abcd")
    val result = optionString.filter(value => value.contains("e"))
    result shouldEqual NoneString
  }

  "SomeString(\"abcd\").map(value => value+\"e\")" should "be SomeString(\"abcde\")" in {
    val optionString: OptionString = SomeString("abcd")
    val result = optionString.map(value => value + "e")
    result shouldEqual SomeString("abcde")
  }

  "NoneString,(value => value+\"e\")" should "be NoneString" in {
    val optionString: OptionString = NoneString
    val result = optionString.map(value => value + "e")
    result shouldEqual NoneString
  }

  "SomeString(\"abcd\").getOrElse(\"none\")" should "be \"abcd\"" in {
    val optionString: OptionString = SomeString("abcd")
    val result = optionString.getOrElse("none")
    result shouldEqual "abcd"
  }

  "NoneString.getOrElse(\"none\")" should "be \"none\"" in {
    val optionString: OptionString = NoneString
    val result = optionString.getOrElse("none")
    result shouldEqual "none"
  }

  "SomeString(\"abcd\").flatMap(value => SomeString(value + \"e\"))" should "be \"abcde\"" in {
    val optionString: OptionString = SomeString("abcd")
    val result = optionString.flatMap(value => SomeString(value + "e"))
    result shouldEqual SomeString("abcde")
  }

  "NoneString.flatMap(value => SomeString(value + \"e\"))" should "be NoneString" in {
    val optionString: OptionString = NoneString
    val result = optionString.flatMap(value => SomeString(value + "e"))
    result shouldEqual NoneString
  }
}
