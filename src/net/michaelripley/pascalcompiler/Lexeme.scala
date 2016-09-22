package net.michaelripley.pascalcompiler

final case class Lexeme(lexeme: String, location: LineLocation) {
  
  def size() = {
    lexeme.size
  }
  
}
