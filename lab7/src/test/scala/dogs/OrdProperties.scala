package dogs

import dogs.syntax.ord._
import org.scalacheck.Prop._
import org.scalacheck.{Arbitrary, Properties}

abstract class EqualityProperties[T: Arbitrary](name: String) extends PartialEqualityProperties[T](name) {
  def instance: Equality[T]

  property("total equality is reflexive") = forAll { a: T => instance.equal(a, a) }
}

abstract class PartialEqualityProperties[T: Arbitrary](name: String) extends Properties(name) {
  def instance: PartialEquality[T]

  property("partial equality is symmetric") = forAll { (left: T, right: T) =>
    instance.equal(left, right) == instance.equal(right, left)
  }

  property("partial equality is transitive") = forAll { (a: T, b: T, c: T) =>
    !(instance.equal(a, b) && instance.equal(b, c)) || instance.equal(a, c)
  }
}

abstract class PartialOrderProperties[T: Arbitrary](name: String) extends PartialEqualityProperties[T](name) {
  implicit def instance: PartialOrd[T]

  property("partial order is reflexive") = forAll { a: T => a <= a }

  property("partial order is antisymmetric") = forAll { (a: T, b: T) =>
    !((a <= b) && (b <= a)) || instance.equal(a, b)
  }

  property("partial order is transitive") = forAll { (a: T, b: T, c: T) => !((a <= b) && (b <= c)) || (a <= c) }
}

abstract class PartialOrderWithEqualityProperties[T: Arbitrary](name: String) extends PartialOrderProperties[T](name) {
  implicit def instance: PartialOrd[T]

  property("partial order is reflexive over equality") = forAll { a: T => instance.equal(a, a) }
}

abstract class OrderProperties[T: Arbitrary](name: String) extends PartialOrderWithEqualityProperties[T](name) {
  implicit def instance: Ord[T]

  property("total order is always defined") = forAll { (a: T, b: T) => instance.partialCompare(a, b).isDefined }
}
