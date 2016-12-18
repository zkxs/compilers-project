package net.michaelripley.pascalcompiler.tokens

import net.michaelripley.Util
import net.michaelripley.pascalcompiler.lexer.Lexeme

class AttributeToken(
    tokenName: String,
    attribute: Option[String],
    val lexeme: Lexeme) extends PartialAttributeToken(tokenName, attribute) {
  
  def this(token: String, attributeString: String, lexeme: Lexeme) = {
    this(token, 
      if (attributeString.isEmpty()) {
        None
      } else {
        Option(attributeString)
      },
      lexeme
    )
  }
  
  /**
   * @return Size of the lexeme
   */
  def size() = {
    lexeme.size
  }
  
  def isNumeric(): Boolean = {
    tokenName == "INTEGER" || tokenName == "REAL" ||  tokenName == "LONGREAL"
  }
  
  override def canEqual(other: Any): Boolean = {
    other.isInstanceOf[AttributeToken]
  }
  
  override def equals(other: Any): Boolean = {
    other match {
      case other: AttributeToken => {
        (other canEqual this) && ((other eq this) || (
            other.tokenName == tokenName
            && other.attribute == attribute
        ))
      }
      case _ => false
    }
  }
  
  override def hashCode(): Int = {
    Util.hash(tokenName, attribute, lexeme)
  }
  
  override def toString: String = {
    attribute match {
      case Some(attributeString) =>
        f"${lexeme.location.lineNumber}%4d        ${lexeme.lexeme}%-15s        $tokenName%-10s        $attributeString"
      case None =>
        f"${lexeme.location.lineNumber}%4d        ${lexeme.lexeme}%-15s        $tokenName%-10s        NULL"
    }
  }
}
