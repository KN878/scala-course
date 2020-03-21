package dogs.syntax.option

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class SyntaxOptionSpec extends AnyFlatSpec with Matchers{
  it should "return Some[a.type]" in {
    3.some shouldBe Some(3)
    3L.some shouldBe Some(3L)
    3.3F.some shouldBe Some(3.3F)
    List(1,2,3).some shouldBe Some(List(1,2,3))
  }
}
