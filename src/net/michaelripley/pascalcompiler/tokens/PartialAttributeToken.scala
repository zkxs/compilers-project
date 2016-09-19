package net.michaelripley.pascalcompiler.tokens

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
  
  /**
   * Duplicate this token, but using a different lexeme
   */
  def makeToken(lexeme: Lexeme) = {
    new AttributeToken(token, attribute, lexeme)
  }
  
  override def canEqual(other: Any): Boolean = {
    other.isInstanceOf[PartialAttributeToken]
  }
  
  override def equals(other: Any): Boolean = {
    other match {
      case other: PartialAttributeToken => {
        (other canEqual this) && ((other eq this) || (
            other.token == token
            && other.attribute == attribute
        ))
      }
      case _ => false
    }
  }
  
}
