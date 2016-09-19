package net.michaelripley.pascalcompiler.tokens

class ErrorToken(token: String, attribute: Option[String], lexeme: Lexeme) extends AttributeToken(token, attribute, lexeme) {
  
  override def canEqual(other: Any): Boolean = {
    other.isInstanceOf[ErrorToken]
  }
  
  override def equals(other: Any): Boolean = {
    other match {
      case other: ErrorToken => {
        (other canEqual this) && ((other eq this) || (
            super.equals(other)
        ))
      }
      case _ => false
    }
  }
}
