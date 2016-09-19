package net.michaelripley.pascalcompiler.tokens

import java.util.Objects

class AttributeToken(tokenName: String, attribute: Option[String], val lexeme: Lexeme) extends PartialAttributeToken(tokenName, attribute) {
  
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
    Objects.hash(tokenName, attribute, lexeme)
  }
  
  override def toString: String = {
    attribute match {
      case Some(attributeString) => s"($lexeme, $tokenName, $attributeString)"
      case None => s"($lexeme, $tokenName)"
    }
  }
}
