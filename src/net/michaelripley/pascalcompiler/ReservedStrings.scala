package net.michaelripley.pascalcompiler

import scala.io.Source
import net.michaelripley.pascalcompiler.tokens.PartialAttributeToken

object ReservedStrings {
  
  private val pattern = """^([^\s]+)\s+([^\s]+)\s*([^\s]*)\s*$""".r
  
  private def load(reservedWordSource: Source): Map[String, PartialAttributeToken] = {
    val map = reservedWordSource.getLines().map {
      line => line match {
        case pattern(lexeme, token, attribute) => (lexeme, new PartialAttributeToken(token, attribute))
        case _ => throw new IllegalArgumentException(s"""Invalid line "$line" in $reservedWordSource""")
      }
    }.toMap
    
    reservedWordSource.close()
    
    map
  }
}

import ReservedStrings._

class ReservedStrings(reservedWordSource: Source) {
   private val reservedWordMap = load(reservedWordSource)
   
   /**
    * Get the token associated with a specific lexeme,
    * throwing an exception if it cannot be found
    */
   def apply(lexeme: String) = {
     get(lexeme).get
   }
   
   /**
    * Optionally get the token associated with a specific lexeme
    */
   def get(lexeme: String) = {
     reservedWordMap.get(lexeme)
   }
   
   /**
    * Get a set of all strings in this ReservedStrings
    */
   def getStrings() = {
     reservedWordMap.keySet
   }
}
