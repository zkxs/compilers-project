package net.michaelripley.pascalcompiler

class Operator(tokenName: String, attribute: String) extends PartialToken(tokenName, attribute) {
  
}

import scala.io.Source

object Operator {
  
  private val pattern = """^([^\s]+)\s+([^\s]+)\s*([^\s]*)\s*$""".r
  private val filename = "operators.dat"
  
  def load() = {
    Source.fromFile(filename).getLines().map {
      line => line match {
        case pattern(lexeme, token, attribute) => new Operator(token, attribute)
        case _ => throw new IllegalArgumentException(s"""Invalid line "$line" in $filename""")
      }
    }
  }
}
