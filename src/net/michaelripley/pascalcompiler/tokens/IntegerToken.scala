package net.michaelripley.pascalcompiler.tokens

import net.michaelripley.Util
import net.michaelripley.pascalcompiler.lexer.Lexeme

class IntegerToken(
    lexeme: Lexeme,
    val value: Int) extends AttributeToken("INTEGER", None, lexeme) {
  
  override def canEqual(other: Any): Boolean = {
    other.isInstanceOf[IntegerToken]
  }
  
  override def equals(other: Any): Boolean = {
    other match {
      case other: IntegerToken => {
        (other canEqual this) && ((other eq this) || (
            other.value == value
        ))
      }
      case _ => false
    }
  }
  
  override def hashCode(): Int = {
    Util.hash(tokenName, attribute, lexeme)
  }
  
}
