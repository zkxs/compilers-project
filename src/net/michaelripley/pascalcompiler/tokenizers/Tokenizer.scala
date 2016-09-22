package net.michaelripley.pascalcompiler.tokenizers

import net.michaelripley.pascalcompiler.tokens._
import net.michaelripley.pascalcompiler.LineFragment

trait Tokenizer {
  
  /**
   * Extract a token. Will not have leading space, and will not be empty.
   */
  def extractToken(line: LineFragment): Option[Token];
}
