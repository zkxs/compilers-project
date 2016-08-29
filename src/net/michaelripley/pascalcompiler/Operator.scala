package net.michaelripley.pascalcompiler

class Operator(lexeme: String, tokenName: String, attribute: String) extends Token(lexeme, tokenName, attribute) {
  
}

import scala.io.Source

object Operator {
  
  private val pattern = """^([^\s]+)\s+([^\s]+)\s*([^\s]*)\s*$""".r
  private val filename = "operators.dat"
  
  def load() = {
    Source.fromFile(filename).getLines().map {
      line => line match {
        case pattern(lexeme, token, attribute) => new ReservedWord(lexeme, token, attribute)
        case _ => throw new IllegalArgumentException(s"""Invalid line "$line" in $filename""")
      }
    }
  }
}
