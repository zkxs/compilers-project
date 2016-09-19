package net.michaelripley.pascalcompiler

class Operators(tokenName: String, attribute: String) extends PartialToken(tokenName, attribute) {
  
}

import scala.io.Source

object Operators {
  
  private val pattern = """^([^\s]+)\s+([^\s]+)\s*([^\s]*)\s*$""".r
  private val filename = "operators.dat"
  
  def load() = {
    Source.fromFile(filename).getLines().map {
      line => line match {
        case pattern(lexeme, token, attribute) => new Operators(token, attribute)
        case _ => throw new IllegalArgumentException(s"""Invalid line "$line" in $filename""")
      }
    }
  }
}
