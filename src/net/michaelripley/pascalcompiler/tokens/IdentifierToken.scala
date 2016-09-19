package net.michaelripley.pascalcompiler.tokens

import net.michaelripley.pascalcompiler.identifiers.Identifier

class IdentifierToken(tokenName: String, val identifier: Identifier) extends Token(tokenName) {
  
  override def canEqual(other: Any): Boolean = {
    other.isInstanceOf[IdentifierToken]
  }
  
  override def equals(other: Any): Boolean = {
    other match {
      case other: IdentifierToken => {
        (other canEqual this) && ((other eq this) || (
            other.tokenName == tokenName
            && other.identifier == identifier
        ))
      }
      case _ => false
    }
  }
  
}
