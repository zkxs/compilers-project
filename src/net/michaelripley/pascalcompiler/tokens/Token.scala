package net.michaelripley.pascalcompiler.tokens

/**
 * Superclass of all tokens. It only contains the token name.
 */
abstract class Token(val tokenName: String) {
  
  def canEqual(other: Any): Boolean = {
    other.isInstanceOf[Token]
  }
  
  override def equals(other: Any): Boolean = {
    other match {
      case other: Token => {
        (other canEqual this) && ((other eq this) || (
            other.tokenName == tokenName
        ))
      }
      case _ => false
    }
  }
  
  override def hashCode(): Int = {
    tokenName.hashCode()
  }
  
}
