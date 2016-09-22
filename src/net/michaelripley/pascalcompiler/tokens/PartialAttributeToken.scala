package net.michaelripley.pascalcompiler.tokens

import net.michaelripley.Util

/**
 * Only contains the token name and attribute. It is missing the lexeme.
 */
class PartialAttributeToken(tokenName: String, val attribute: Option[String]) extends Token(tokenName) {
  
  def this(token: String, attributeString: String) = {
    this(token, 
      if (attributeString.isEmpty()) {
        None
      } else {
        Option(attributeString)
      }
    )
  }
  
  def this(token: String) = {
    this(token, None)
  }
  
  /**
   * Duplicate this token, but using a different lexeme
   */
  def makeToken(lexeme: Lexeme) = {
    new AttributeToken(tokenName, attribute, lexeme)
  }
  
  override def canEqual(other: Any): Boolean = {
    other.isInstanceOf[PartialAttributeToken]
  }
  
  override def equals(other: Any): Boolean = {
    other match {
      case other: PartialAttributeToken => {
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
