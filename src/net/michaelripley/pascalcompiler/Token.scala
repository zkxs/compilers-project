package net.michaelripley.pascalcompiler

class Token(val lexeme: String, val token: String, val attribute: Option[String]) {
  
  def this(lexeme: String, token: String, attributeString: String) = {
    this(lexeme, token, 
      if (attributeString.isEmpty()) {
        None
      } else {
        Option(attributeString)
      }
    )
  }
  
  /**
   * Duplicate this token but using a different lexeme
   */
  def duplicate(lexeme: String) = {
    new Token(lexeme, token, attribute)
  }
  
  override def equals(o: Any): Boolean = {
    o match {
      case o: ReservedWord => o.lexeme equalsIgnoreCase this.lexeme
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