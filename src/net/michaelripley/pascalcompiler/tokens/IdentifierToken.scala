package net.michaelripley.pascalcompiler.tokens

import net.michaelripley.pascalcompiler.identifiers.Identifier

class IdentifierToken(tokenName: String, val identifier: Identifier) extends Token(tokenName) {
  
}
