package nikita.kalinskiy

class SecondThenFirst[A, B](val first: Ordering[A], val second: Ordering[B]) extends Ordering[(A, B)] {
  override def compare(a: (A, B), b: (A, B)): Ordering.Result = {
    val secondCompare = second.compare(a._2, b._2)
    if (secondCompare == Ordering.Equal) {
      return first.compare(a._1, b._1)
    }
    secondCompare
  }
}
