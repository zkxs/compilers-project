package net.michaelripley.pascalcompiler.tokens

import net.michaelripley.Util
import net.michaelripley.pascalcompiler.identifiers.Identifier
import net.michaelripley.pascalcompiler.Lexeme

class IdentifierToken(tokenName: String, val identifier: Identifier, val lexeme: Lexeme) extends Token(tokenName) {
  
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
  
  override def toString: String = {
    f"${lexeme.location.lineNumber}%4d        ${lexeme.lexeme}%-15s        $tokenName%-8s        loc${identifier.number}"
  }
  
}
