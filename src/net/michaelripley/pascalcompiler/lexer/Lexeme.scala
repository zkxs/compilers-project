package net.michaelripley.pascalcompiler.lexer

final case class Lexeme(lexeme: String, location: LineLocation) {
  
  def size() = {
    lexeme.size
  }
  
}
