package net.michaelripley.pascalcompiler.tokens

class PartialErrorToken(tokenName: String, attribute: Option[String]) extends PartialAttributeToken(tokenName, attribute) {
  
  override def makeToken(lexeme: Lexeme) = {
    new ErrorToken(tokenName, attribute, lexeme)
  }
  
  override def canEqual(other: Any): Boolean = {
    other.isInstanceOf[PartialErrorToken]
  }
  
  override def equals(other: Any): Boolean = {
    other match {
      case other: PartialErrorToken => {
        (other canEqual this) && ((other eq this) || (
            other.tokenName == tokenName
            && other.attribute == attribute
        ))
      }
      case _ => false
    }
  }
  
}
