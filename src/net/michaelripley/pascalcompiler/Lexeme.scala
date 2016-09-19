package net.michaelripley.pascalcompiler

class Lexeme(val lexeme: String, val lineNumber: Int, val column: Int) {
  def size() = {
    lexeme.size
  }
}