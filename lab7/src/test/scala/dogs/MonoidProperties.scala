package dogs

import org.scalacheck.Prop._
import org.scalacheck.{Arbitrary, Properties}

abstract class SemigroupProperties[T: Arbitrary](name: String) extends Properties(name) {
  def instance: Semigroup[T]

  property("semigroup is associative") = forAll { (a: T, b: T, c: T) =>
    instance.combine(a, instance.combine(b, c)) == instance.combine(instance.combine(a, b), c)
  }
}

abstract class MonoidProperties[T: Arbitrary](name: String) extends SemigroupProperties(name) {
  def instance: Monoid[T]

  property("identity is held") = forAll { a: T => instance.combine(a, instance.unit) == a }
}

abstract class CommutativeMonoidProperties[T: Arbitrary](name: String) extends MonoidProperties(name) {
  property("commutativity law is held") = forAll { (a: T, b: T) => instance.combine(a, b) == instance.combine(b, a) }
}
