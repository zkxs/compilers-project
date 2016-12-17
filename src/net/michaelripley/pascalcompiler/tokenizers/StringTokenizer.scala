package net.michaelripley.pascalcompiler.tokenizers

private object StringTokenizer {
  
  /**
   * Get the longest string in an iterable. Returns None if it is empty or
   * if there is a tie for longest.
   */
  private def getLongest(list: Iterable[String]): Option[String] = {
    
    if (list.isEmpty) {
      return None
    }
    
    var currentElem:String = null
    var currentLength = -1
    var countWithLength = 0
    
    list.foreach(
      elem => {
        val elemLength = elem.length()
        
        if (elemLength > currentLength) {
          currentElem = elem
          currentLength = elemLength
          countWithLength = 1
        } else if (elemLength == currentLength) {
          countWithLength += 1
        }
      }
    )
    
    /*  because the list is nonempty and the initial value of
     *  currentLength = -1, it is guaranteed that our three variables
     *  are valid, eg. currentElem is not null and countWithLength < 0
     */
    
    if (countWithLength == 1) {
      return Some(currentElem)
    } else {
      return None
    }
  }
  
}

import net.michaelripley.pascalcompiler.tokens._
import net.michaelripley.pascalcompiler.lexer.ReservedStrings
import StringTokenizer._
import net.michaelripley.pascalcompiler.lexer.Lexeme
import net.michaelripley.pascalcompiler.lexer.LineFragment

class StringTokenizer(private val reservedStrings: ReservedStrings)
    extends Tokenizer {
  
  private val reservedStringKeys = reservedStrings.getStrings()
  
  def extractToken(line: LineFragment): Option[AttributeToken] = {
    
    // get all reserved strings that prefix line
    val matchingStrings = reservedStringKeys.filter(
      rString => line.contents.startsWith(rString)
    )
    
    // get the longest string, if there is one
    val longestString = getLongest(matchingStrings)
    
    longestString match {
      case Some(string) => {
        // if there WAS a longest string, tokenize and return it
        val lexeme = Lexeme(string, line.location)
        Some(reservedStrings(string).makeToken(lexeme))
      }
      // if there was no longest string, then we couldn't get a token
      case None => None
    }
  }
  
}
