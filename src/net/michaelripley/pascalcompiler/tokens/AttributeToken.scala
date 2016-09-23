package net.michaelripley.pascalcompiler.tokens

import net.michaelripley.Util
import net.michaelripley.pascalcompiler.Lexeme

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
  
  override def canEqual(other: Any): Boolean = {
    other.isInstanceOf[AttributeToken]
  }
  
  override def equals(other: Any): Boolean = {
    other match {
      case other: AttributeToken => {
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
  
  override def toString: String = {
    attribute match {
      case Some(attributeString) =>
        f"${lexeme.location.lineNumber}%4d        ${lexeme.lexeme}%-15s        $tokenName%-8s        $attributeString"
      case None =>
        f"${lexeme.location.lineNumber}%4d        ${lexeme.lexeme}%-15s        $tokenName%-8s        NULL"
    }
  }
}
