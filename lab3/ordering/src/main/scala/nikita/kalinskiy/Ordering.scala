package nikita.kalinskiy

/**
  * Represent a total order between values of type T.
  * All implementations should conform to laws of total orders:
  * - reflexivity
  * a == a
  * - totality
  * a <= b or b <= a
  * - antisymmetry
  * a <= b and b <= a implies a == b
  * - transitivity
  * a < b and b < c implies a < c, same for == and >
  *
  */
trait Ordering[-T] {
  // Returns Ordering.Greater if a > b
  // Returns Ordering.Equal if a == b
  // Returns Ordering.Lesser if a < b
  def compare(a: T, b: T): Ordering.Result
}

object Ordering {
  sealed trait Result
  object Greater extends Result
  object Equal   extends Result
  object Lesser  extends Result

  def max[T](ordering: Ordering[T])(first: T, other: T*): T = {
    var max = first
    for (el <- other) {
      if (ordering.compare(max, el) == Ordering.Lesser) {
        max = el
      }
    }
    max
  }
}
