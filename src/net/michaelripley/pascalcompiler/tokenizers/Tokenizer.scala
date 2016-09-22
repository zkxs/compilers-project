package net.michaelripley.pascalcompiler.tokenizers

import net.michaelripley.pascalcompiler.tokens.Token

trait Tokenizer {
  
  /**
   * Extract a token. Will not have leading space, and will not be empty.
   */
  def extractToken(line: String, lineNumber: Int, columnOffset: Int): Option[Token];
}
