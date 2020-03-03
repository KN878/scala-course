package nikita.kalinskiy

//As the set of real numbers ordered by '<' or '>' is totally ordered, hence the subsets of integers also conform to
//the total order
object OrderingInt extends Ordering[Int] {
  override def compare(a: Int, b: Int): Ordering.Result = {
    if (a > b) return Ordering.Greater
    if (a == b) return Ordering.Equal
    Ordering.Lesser
  }
}
