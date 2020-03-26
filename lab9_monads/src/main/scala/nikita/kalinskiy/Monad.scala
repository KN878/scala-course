package nikita.kalinskiy

import scala.concurrent.{ExecutionContext, Future}

case class Id[+A](value: A)

case class Wrapper[I, +O](f: I => Future[O])

trait Monad[F[+_]] extends Functor[F] {
  implicit def functorFromMonad[T, H[+_]](implicit monad: Monad[H]): Functor[H] = monad

  def pure[T](v: T): F[T]

  def flatMap[A, B](a: F[A])(f: A => F[B]): F[B]

  def flatten[T](v: F[F[T]]): F[T] = flatMap(v)(identity[F[T]])

  def map[A, B](a: F[A])(f: A => B): F[B] = flatMap(a)((i: A) => pure(f(i)))
}

object Monad {
  def apply[T, F[+_]](implicit instance: Monad[F]): Monad[F] = instance

  implicit object OptionMonad extends Monad[Option] {
    override def pure[T](v: T): Option[T] = Option(v)

    override def flatMap[A, B](a: Option[A])(f: A => Option[B]): Option[B] = a flatMap f
  }

  implicit object VectorMonad extends Monad[Vector] {
    override def pure[T](v: T): Vector[T] = Vector(v)

    override def flatMap[A, B](a: Vector[A])(f: A => Vector[B]): Vector[B] = a flatMap f
  }

  implicit object ListMonad extends Monad[List] {
    override def pure[T](v: T): List[T] = List(v)

    override def flatMap[A, B](a: List[A])(f: A => List[B]): List[B] = a flatMap f
  }


  implicit object IdMonad extends Monad[Id] {
    override def pure[T](v: T): Id[T] = Id(v)

    override def flatMap[A, B](a: Id[A])(f: A => Id[B]): Id[B] = f(a.value)
  }

  implicit def FutureMonad(implicit ec: ExecutionContext): Monad[Future] = new Monad[Future] {
    override def pure[T](v: T): Future[T] = Future(v)

    override def flatMap[A, B](a: Future[A])(f: A => Future[B]): Future[B] = a flatMap f
  }

  implicit def EitherMonad[E]: Monad[Either[E, +*]] = new Monad[Either[E, +*]] {
    override def pure[T](v: T): Either[E, T] = Right(v)

    override def flatMap[A, B](a: Either[E, A])(f: A => Either[E, B]): Either[E, B] = a match {
      case Right(value) => f(value)
      case Left(value) => Left(value)
    }
  }

  implicit def UnaryFunctionMonad[E]: Monad[(E) => +*] = new Monad[(E) => +*] {
    override def pure[T](v: T): (E) => T = _ => v

    override def flatMap[A, B](a: (E) => A)(f: A => ((E) => B)): (E) => B = v => f(a(v))(v)
  }

  implicit def WrapperMonad[I](implicit ec: ExecutionContext): Monad[Wrapper[I, +*]] = new Monad[Wrapper[I, +*]] {
    override def pure[T](v: T): Wrapper[I, T] = Wrapper((_: I) => Future(v))

    override def flatMap[A, B](a: Wrapper[I, A])(f: A => Wrapper[I, B]): Wrapper[I, B] =
      Wrapper((v: I) => a.f(v).flatMap(res => f(res).f(v)))
  }
}