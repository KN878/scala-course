package nikita.kalinskiy.syntax

import nikita.kalinskiy.Monad

package object monad {

  implicit class SyntaxMonad[A, F[+_]](value: F[A]) {
    def flatMap[B](f: A => F[B])(implicit monad: Monad[F]): F[B] = monad.flatMap(value)(f)
  }

}
