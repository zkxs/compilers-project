package net.michaelripley.pascalcompiler

/*
 * Contains lexeme information in addition to the token information
 */

class Token(token: String, attribute: Option[String], val lexeme: Lexeme) extends PartialToken(token, attribute) {
  
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
   * Duplicate this token but using a different lexeme
   */
  def duplicate(lexeme: Lexeme) = {
    new Token(token, attribute, lexeme)
  }
  
  override def equals(o: Any): Boolean = {
    o match {
      case o: ReservedWord => (o.token equalsIgnoreCase this.token) && (o.attribute == this.attribute)
      case _ => false
    }
  }
  
  override def hashCode(): Int = {
    lexeme.hashCode()
  }
  
  override def toString: String = {
    attribute match {
      case Some(attributeString) => s"($lexeme, $token, $attributeString)"
      case None => s"($lexeme, $token)"
    }
  }
}