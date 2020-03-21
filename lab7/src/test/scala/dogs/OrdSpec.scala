package dogs

object OrdIntSpec extends OrderProperties[Int]("intOrd") {
  override def instance: Ord[Int] = Ord[Int]
}

object OrdLongSpec extends OrderProperties[Long]("longOrd") {
  override def instance: Ord[Long] = Ord[Long]
}

object OrdStringSpec extends EqualityProperties[String]("stringOrd") {
  override def instance: Equality[String] = Ord[String]
}
