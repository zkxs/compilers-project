package net.michaelripley.pascalcompiler.tokenizers

import net.michaelripley.pascalcompiler.tokens.AttributeToken

trait Tokenizer {
  
  /**
   * Extract a token. Will not have leading space.
   */
  def extractToken(line: String, lineNumber: Int, columnOffset: Int): Option[AttributeToken];
}
