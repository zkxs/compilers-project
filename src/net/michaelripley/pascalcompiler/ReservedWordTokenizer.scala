package net.michaelripley.pascalcompiler

class ReservedWord(tokenName: String, attribute: String) extends PartialToken(tokenName, attribute) {
  
}

import scala.io.Source

object ReservedWord {
  
  private val pattern = """^([^\s]+)\s+([^\s]+)\s*([^\s]*)\s*$""".r
  private val filename = "reservedwords.dat"
  
  def load() = {
    Source.fromFile(filename).getLines().map {
      line => line match {
        case pattern(lexeme, token, attribute) => new ReservedWord(token, attribute)
        case _ => throw new IllegalArgumentException(s"""Invalid line "$line" in $filename""")
      }
    }
  }
}
