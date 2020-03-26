package nikita.kalinskiy

trait Functor[F[+_]] {
  def map[A, B](a: F[A])(f: A => B): F[B]
}

object Functor {
  def apply[T, H[+_]](implicit instance: Functor[H]): Functor[H] = instance

  implicit def mapFunctor[E]: Functor[Map[E, +*]] = new Functor[Map[E, +*]] {
    override def map[A, B](a: Map[E, A])(f: A => B): Map[E, B] = for {(k, v) <- a} yield k -> f(v)
  }
}
