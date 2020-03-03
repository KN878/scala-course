package nikita.kalinskiy

object OptionUtils {
  def sequence[T](list: List[Option[T]]): Option[List[T]] = {
    val nonNoneList = list.collect {
      case Some(v) => v
    }
    if (nonNoneList.size == list.size) Some(nonNoneList) else None
  }
}
