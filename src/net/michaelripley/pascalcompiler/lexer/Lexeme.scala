package net.michaelripley.pascalcompiler.lexer

import net.michaelripley.pascalcompiler.lexer.LineLocation

final case class Lexeme(lexeme: String, location: LineLocation) {
  
  def size() = {
    lexeme.size
  }
  
}
