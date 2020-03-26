package nikita.kalinskiy.syntax

import nikita.kalinskiy.Functor

package object functor {

  implicit class SyntaxFunctor[A, F[+_]](value: F[A]) {
    def map[B](f: A => B)(implicit functor: Functor[F]): F[B] = functor.map(value)(f)
  }

}
