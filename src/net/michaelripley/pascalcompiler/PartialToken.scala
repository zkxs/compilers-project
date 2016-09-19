package net.michaelripley.pascalcompiler

/**
 * A token it its purest form. It only contains the token name and attribute
 */

class PartialToken(val token: String, val attribute: Option[String]) {
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
   * Duplicate this token but using a different lexeme
   */
  def makeToken(lexeme: Lexeme) = {
    new Token(token, attribute, lexeme)
  }
}