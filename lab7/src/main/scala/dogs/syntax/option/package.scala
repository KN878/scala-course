package dogs.syntax

package object option {
  implicit class SyntaxOption[T](val a: T) {
    def some: Option[T] = Some(a)
  }
}
