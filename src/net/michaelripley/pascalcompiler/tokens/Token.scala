package net.michaelripley.pascalcompiler.tokens

/**
 * Superclass of all tokens. It only contains the token name.
 */
abstract class Token(val token: String) {
  
  def canEqual(other: Any): Boolean = {
    other.isInstanceOf[Token]
  }
  
  override def equals(other: Any): Boolean = {
    other match {
      case other: Token => {
        (other canEqual this) && ((other eq this) || (
            other.token == token
        ))
      }
      case _ => false
    }
  }
  
}
