package nikita.kalinskiy

//We compare lists by their length, expressed as natural numbers.
// And the natural numbers comprise the smallest totally ordered set with no upper bound
object OrderingList extends Ordering[List[Any]] {
  override def compare(a: List[Any], b: List[Any]): Ordering.Result = {
    if (a.length > b.length) return Ordering.Greater
    if (a.length == b.length) return Ordering.Equal
    Ordering.Lesser
  }
}
