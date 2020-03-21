package dogs.syntax.either

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class EitherSyntaxSpec extends AnyFlatSpec with Matchers{
  "left" should "return Either[a.type, Nothing]" in {
    3.left shouldBe Left(3)
    3L.left shouldBe Left(3L)
    3.3F.left shouldBe Left(3.3F)
    List(12,3,4).left shouldBe Left(List(12,3,4))
  }

  "right" should "return Either[Nothing, a.type]" in {
    3.right shouldBe Right(3)
    3L.right shouldBe Right(3L)
    3.3F.right shouldBe Right(3.3F)
    List(12,3,4).right shouldBe Right(List(12,3,4))
  }
}
