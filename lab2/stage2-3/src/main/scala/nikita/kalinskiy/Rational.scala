package nikita.kalinskiy

class Rational private (val numer: Int, val denom:Int) {
  def this(n: Int) = this(n, 1)
  def + (that: Rational): Rational = Rational(numer*that.denom + that.numer*denom, denom*that.denom)
  def + (i: Int): Rational = Rational(numer + i*denom, denom)
  def - (that: Rational): Rational = Rational(numer*that.denom - that.numer*denom, denom*that.denom)
  def - (i: Int): Rational = Rational(numer - i*denom, denom)
  def * (that: Rational): Rational = Rational(numer*that.numer, denom*that.denom)
  def * (i: Int): Rational = Rational(numer*i, denom)
  def / (that: Rational): Rational = Rational(numer*that.denom, denom*that.numer)
  def / (i: Int): Rational = Rational(numer, denom*i)
  def == (that:Rational): Boolean = numer == that.numer && denom == that.denom

  override def toString: String = numer + "/" + denom
}

object Rational {
  private def gcd(a:Int, b:Int): Int = if (b == 0) a else gcd(b, a%b)
  def apply(n:Int, d:Int):Rational = {
    require(d != 0)
    new Rational(n / gcd(n.abs, d.abs), d / gcd(n.abs, d.abs))
  }

  def unapply(r: Rational): Some[(Int, Int)] = Some(r.numer, r.denom)
}