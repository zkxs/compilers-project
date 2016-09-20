package net.michaelripley.pascalcompiler

import scala.io.Source
import net.michaelripley.pascalcompiler.tokens.PartialAttributeToken

object ReservedWords {
  
  private val pattern = """^([^\s]+)\s+([^\s]+)\s*([^\s]*)\s*$""".r
  
  private def load(reservedWordSource: Source): Map[String, PartialAttributeToken] = {
    reservedWordSource.getLines().map {
      line => line match {
        case pattern(lexeme, token, attribute) => (lexeme, new PartialAttributeToken(token, attribute))
        case _ => throw new IllegalArgumentException(s"""Invalid line "$line" in $reservedWordSource""")
      }
    }.toMap
  }
}

import ReservedWords._

class ReservedWords(reservedWordSource: Source) {
   private val reservedWordMap = load(reservedWordSource)
   
   def get(lexeme: String) = {
     reservedWordMap.get(lexeme)
   }
}
