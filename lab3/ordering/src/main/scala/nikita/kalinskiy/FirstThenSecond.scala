package nikita.kalinskiy

class FirstThenSecond[A, B](val first: Ordering[A], val second: Ordering[B]) extends Ordering[(A, B)] {
  override def compare(a: (A, B), b: (A, B)): Ordering.Result = {
    val firstCompare = first.compare(a._1, b._1)
    if (firstCompare == Ordering.Equal) {
      return second.compare(a._2, b._2)
    }
    firstCompare
  }
}
