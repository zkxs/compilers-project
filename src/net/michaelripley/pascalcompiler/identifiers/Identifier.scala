package net.michaelripley.pascalcompiler.identifiers

// number is unique to this specific token and file
class Identifier(val name: String, val number: Int) {
  
  def canEqual(other: Any): Boolean = {
    other.isInstanceOf[Identifier]
  }
  
  override def equals(other: Any): Boolean = {
    other match {
      case other: Identifier => {
        (other canEqual this) && ((other eq this) || (
            other.number == number
        ))
      }
      case _ => false
    }
  }
  
  override def hashCode(): Int = {
    number.hashCode()
  }
}
