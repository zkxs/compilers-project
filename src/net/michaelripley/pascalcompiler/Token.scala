package net.michaelripley.pascalcompiler

class Token(val lexeme: String, val token: String, attributeString: String) {
  val attribute = {
    if (attributeString.isEmpty()) {
      None
    } else {
      Option(attributeString)
    }
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