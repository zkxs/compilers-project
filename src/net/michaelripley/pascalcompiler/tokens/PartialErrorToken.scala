package net.michaelripley.pascalcompiler.tokens

import net.michaelripley.Util
import net.michaelripley.pascalcompiler.lexer.Lexeme

class PartialErrorToken(
    tokenName: String,
    attribute: Option[String]
) extends PartialAttributeToken(tokenName, attribute) {
  
  def this(token: String, attributeString: String) = {
    this(token, 
      if (attributeString.isEmpty()) {
        None
      } else {
        Option(attributeString)
      }
    )
  }
  
  override def makeToken(lexeme: Lexeme): ErrorToken = {
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
  
  override def hashCode(): Int = {
    Util.hash(tokenName, attribute)
  }
  
}
