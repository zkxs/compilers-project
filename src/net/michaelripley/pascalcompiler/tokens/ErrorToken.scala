package net.michaelripley.pascalcompiler.tokens

import net.michaelripley.Util
import net.michaelripley.pascalcompiler.Lexeme

class ErrorToken(tokenName: String, attribute: Option[String], lexeme: Lexeme) extends AttributeToken(tokenName, attribute, lexeme) {
  
  override def canEqual(other: Any): Boolean = {
    other.isInstanceOf[ErrorToken]
  }
  
  override def equals(other: Any): Boolean = {
    other match {
      case other: ErrorToken => {
        (other canEqual this) && ((other eq this) || (
            other.tokenName == tokenName
            && other.attribute == attribute
            && other.lexeme == lexeme
        ))
      }
      case _ => false
    }
  }
  
  override def hashCode(): Int = {
    Util.hash(tokenName, attribute, lexeme)
  }
}
