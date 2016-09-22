package net.michaelripley.pascalcompiler.tokenizers

import net.michaelripley.pascalcompiler.tokens.LineLocation

final case class Lexeme(val lexeme: String, val lineLocation: LineLocation) {
  
  def size() = {
    lexeme.size
  }
  
}
