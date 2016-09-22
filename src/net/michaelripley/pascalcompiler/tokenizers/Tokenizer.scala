package net.michaelripley.pascalcompiler.tokenizers

import net.michaelripley.pascalcompiler.tokens._

trait Tokenizer {
  
  /**
   * Extract a token. Will not have leading space, and will not be empty.
   */
  def extractToken(line: String, lineLocation: LineLocation): Option[Token];
}
