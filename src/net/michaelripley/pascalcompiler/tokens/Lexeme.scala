package net.michaelripley.pascalcompiler.tokens

final case class Lexeme(val lexeme: String, val lineLocation: LineLocation) {
  
  def size() = {
    lexeme.size
  }
  
}
