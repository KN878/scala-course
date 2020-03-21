package dogs
import dogs.syntax.monoid._


/**
 * Semigroups and monoids are implemented for sum operation
 */

trait Semigroup[T] {
  def combine(a: T, b: T): T
}

object Semigroup {
  def apply[T](implicit instance: Semigroup[T]): Semigroup[T] = instance

  implicit val intSemigroup: Semigroup[Int] = (a: Int, b: Int) => a + b

  implicit val longSemigroup: Semigroup[Long] = (a: Long, b: Long) => a + b

  implicit val floatSemigroup: Semigroup[Float] = (a: Float, b: Float) => a + b

  implicit val doubleSemigroup: Semigroup[Double] = (a: Double, b: Double) => a + b

  implicit def listSemigroup[T]: Semigroup[List[T]] = (a: List[T], b: List[T]) => a ++ b

  implicit def mapSemigroup[K, V: Semigroup]: Semigroup[Map[K, V]] = (a: Map[K, V], b: Map[K, V]) => a ++ b

  implicit def equalitySemigroup[T]: Semigroup[Equality[T]] = (a: Equality[T], b: Equality[T]) =>
    (left: T, right: T) => a.equal(left, right) && b.equal(left, right)
}

trait Monoid[T] extends Semigroup[T] {
  def unit: T
}

object Monoid {
  def apply[T](implicit instance: Monoid[T]): Monoid[T] = instance

  implicit def mapMonoid[K, V: Semigroup]: Monoid[Map[K, V]] = new Monoid[Map[K, V]] {
    override def unit: Map[K, V] = Map.empty[K, V]

    override def combine(a: Map[K, V], b: Map[K, V]): Map[K, V] = a ++ b
  }

  implicit def equalityMonoid[T]: Monoid[Equality[T]] = new Monoid[Equality[T]] {
    override def unit: Equality[T] = (_, _) => true

    override def combine(a: Equality[T], b: Equality[T]): Equality[T] = (left: T, right: T) =>
      a.equal(left, right) && b.equal(left, right)
  }

  implicit val intMonoid: Monoid[Int] = new Monoid[Int] {
    override def unit: Int = 0

    override def combine(a: Int, b: Int): Int = a + b
  }

  implicit val longMonoid: Monoid[Long] = new Monoid[Long] {
    override def unit: Long = 0

    override def combine(a: Long, b: Long): Long = a + b
  }

  implicit val floatMonoid: Monoid[Float] = new Monoid[Float] {
    override def unit: Float = 0

    override def combine(a: Float, b: Float): Float = a + b
  }

  implicit val doubleMonoid: Monoid[Double] = new Monoid[Double] {
    override def unit: Double = 0

    override def combine(a: Double, b: Double): Double = a + b
  }

  implicit def listMonoid[T]: Monoid[List[T]] = new Monoid[List[T]] {
    override def unit: List[T] = List[T]()

    override def combine(a: List[T], b: List[T]): List[T] = a ++ b
  }
}

trait CommutativeSemigroup[T] extends Semigroup[T]

object CommutativeSemigroup {
  def apply[T](implicit instance: CommutativeSemigroup[T]): CommutativeSemigroup[T] = instance

  implicit val intSemigroup: CommutativeSemigroup[Int] = (a: Int, b: Int) => a + b

  implicit val longSemigroup: CommutativeSemigroup[Long] = (a: Long, b: Long) => a + b

  implicit val floatSemigroup: CommutativeSemigroup[Float] = (a: Float, b: Float) => a + b

  implicit val doubleSemigroup: CommutativeSemigroup[Double] = (a: Double, b: Double) => a + b

  implicit def listSemigroup[T]: CommutativeSemigroup[List[T]] = (a: List[T], b: List[T]) => a ++ b

  implicit def mapSemigroup[K, V: CommutativeSemigroup]: CommutativeSemigroup[Map[K, V]] = (a: Map[K, V], b: Map[K, V]) => a ++ b

  implicit def equalitySemigroup[T]: CommutativeSemigroup[Equality[T]] = (a: Equality[T], b: Equality[T]) =>
    (left: T, right: T) => a.equal(left, right) && b.equal(left, right)
}

trait CommutativeMonoid[T] extends CommutativeSemigroup[T] with Monoid[T]

object CommutativeMonoid {
  def apply[T](implicit instance: CommutativeMonoid[T]): CommutativeMonoid[T] = instance

  implicit def mapCommutativeMonoid[K, V: CommutativeSemigroup]: CommutativeMonoid[Map[K, V]] = new CommutativeMonoid[Map[K, V]] {
    override def unit: Map[K, V] = Map.empty[K, V]

    override def combine(a: Map[K, V], b: Map[K, V]): Map[K, V] = {
      val aWithoutB = a.filterNot { case (k, _) => b.contains(k) }
      val bWithoutA = b.filterNot { case (k, _) => a.contains(k) }
      val aAndB = a.filter { case (k, _) => b.contains(k) }
      aWithoutB ++ bWithoutA ++ aAndB.map { case (k, v) => (k, v |+| b(k)) }
    }
  }

  implicit val intMonoid: Monoid[Int] = new Monoid[Int] {
    override def unit: Int = 0

    override def combine(a: Int, b: Int): Int = a + b
  }

  implicit val longMonoid: Monoid[Long] = new Monoid[Long] {
    override def unit: Long = 0

    override def combine(a: Long, b: Long): Long = a + b
  }

  implicit val floatMonoid: Monoid[Float] = new Monoid[Float] {
    override def unit: Float = 0

    override def combine(a: Float, b: Float): Float = a + b
  }

  implicit val doubleMonoid: Monoid[Double] = new Monoid[Double] {
    override def unit: Double = 0

    override def combine(a: Double, b: Double): Double = a + b
  }
}
