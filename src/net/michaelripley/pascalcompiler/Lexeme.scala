package net.michaelripley.pascalcompiler

final case class Lexeme(val lexeme: String, val lineLocation: LineLocation) {
  
  def size() = {
    lexeme.size
  }
  
}
