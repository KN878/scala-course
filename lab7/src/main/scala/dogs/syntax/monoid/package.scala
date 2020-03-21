package dogs.syntax

import dogs.{Monoid, Semigroup}

package object monoid {

  implicit class SyntaxSemigroupMonoid[T](val left: T) extends AnyVal {
    def |+|(right: T)(implicit S: Semigroup[T]): T = S.combine(left, right)
  }

  implicit class SyntaxIterable[T](val inner: Iterable[T]) extends AnyVal {
    def reduceMonoid(implicit M: Monoid[T]): T = inner.foldSemigroup(M.unit)

    def foldSemigroup(start: T)(implicit S: Semigroup[T]): T = inner.fold(start){ (acc, next) => S.combine(acc, next) }

    def foldLeftSemigroup(start: T)(implicit S: Semigroup[T]): T = inner.foldLeft(start){ (acc, next) => S.combine(acc, next) }

    def foldRightSemigroup(start: T)(implicit S: Semigroup[T]): T = inner.foldRight(start){ (acc, next) => S.combine(acc, next) }
  }

}
