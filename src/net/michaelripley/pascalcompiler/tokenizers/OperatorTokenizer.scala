package net.michaelripley.pascalcompiler.tokenizers

import net.michaelripley.pascalcompiler.tokens.AttributeToken

class OperatorTokenizer extends Tokenizer {
  def extractToken(line: String, lineNumber: Int, columnOffset: Int): Option[AttributeToken] = {
    None
  }
}
