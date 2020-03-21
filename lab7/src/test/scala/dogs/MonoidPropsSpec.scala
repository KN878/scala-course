package dogs

object MonoidIntPropsSpec extends CommutativeMonoidProperties[Int]("commutativeMonoidInt") {
  override def instance: Monoid[Int] = Monoid.intMonoid
}
