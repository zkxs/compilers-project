package net.michaelripley.pascalcompiler.tokens

class PartialErrorToken(token: String, attribute: Option[String]) extends PartialAttributeToken(token, attribute) {
  
  override def makeToken(lexeme: Lexeme) = {
    new ErrorToken(token, attribute, lexeme)
  }
  
  override def canEqual(other: Any): Boolean = {
    other.isInstanceOf[PartialErrorToken]
  }
  
  override def equals(other: Any): Boolean = {
    other match {
      case other: PartialErrorToken => {
        (other canEqual this) && ((other eq this) || (
            super.equals(other)
        ))
      }
      case _ => false
    }
  }
  
}
