package net.michaelripley.pascalcompiler.tokens

import net.michaelripley.Util
import net.michaelripley.pascalcompiler.identifiers.Identifier
import net.michaelripley.pascalcompiler.lexer.Lexeme

class IdentifierToken(
    val identifier: Identifier,
    val lexeme: Lexeme) extends Token("ID") {
  
  override def canEqual(other: Any): Boolean = {
    other.isInstanceOf[IdentifierToken]
  }
  
  override def equals(other: Any): Boolean = {
    other match {
      case other: IdentifierToken => {
        (other canEqual this) && ((other eq this) || (
            other.tokenName == tokenName
            && other.identifier == identifier
            && other.lexeme == lexeme
        ))
      }
      case _ => false
    }
  }
  
  override def hashCode(): Int = {
    Util.hash(tokenName, identifier, lexeme)
  }
  
  /**
   * Only returns the first part of the string. The "attribute" must be appended
   * separately
   */
  override def toString: String = {
    f"${lexeme.location.lineNumber + 1}%4d        ${lexeme.lexeme}%-19s    $tokenName%-14s    "
  }
  
}
