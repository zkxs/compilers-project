package net.michaelripley.pascalcompiler.tokens

final class Lexeme(val lexeme: String, val lineNumber: Int, val column: Int) {
  
  def size() = {
    lexeme.size
  }
  
  override def equals(other: Any): Boolean = {
    other match {
      case other: Lexeme => {
        (other eq this) || (
            other.lexeme == lexeme
            && other.lineNumber == lineNumber
            && other.column == column
        )
      }
      case _ => false
    }
  }
  
}
