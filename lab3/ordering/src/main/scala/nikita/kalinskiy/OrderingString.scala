package nikita.kalinskiy

// OrderingString uses lexicographical order to compare two objects.
// The lexicographical order on the Cartesian product of a family of totally ordered sets,
// indexed by a well ordered set, is itself a total order.
object OrderingString extends Ordering[String] {
  override def compare(a: String, b: String): Ordering.Result = {
    a compareTo b match {
      case x if x > 0 => Ordering.Greater
      case x if x < 0 => Ordering.Lesser
      case _          => Ordering.Equal
    }
  }
}
