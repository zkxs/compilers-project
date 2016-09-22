package net.michaelripley.pascalcompiler

final case class Lexeme(lexeme: String, lineLocation: LineLocation) {
  
  def size() = {
    lexeme.size
  }
  
}
