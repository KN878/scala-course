package nikita.kalinskiy

object Combinator {
  def sequence[F[+_] : Monad, T](v: List[F[T]]): F[List[T]] = traverse(v) { fa => fa }

  def traverse[F[+_], A, B](as: List[A])(f: A => F[B])(implicit M: Monad[F]): F[List[B]] = {
    as.foldRight(M.pure(List[B]())) { (a, mbs) => map2(f(a), mbs)(_ :: _) }
  }

  def map2[F[+_], A, B, C](ma: F[A], mb: F[B])(f: (A, B) => C)(implicit M: Monad[F]): F[C] = {
    M.flatMap(ma) {
      (a: A) => M.map(mb) { (b: B) => f(a, b) }
    }
  }
}