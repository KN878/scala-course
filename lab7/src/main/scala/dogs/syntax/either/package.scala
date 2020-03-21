package dogs.syntax

package object either {

  implicit class SyntaxEither[T](val a: T) {
    def right: Either[Nothing, T] = Right(a)

    def left: Either[T, Nothing] = Left(a)
  }

}
