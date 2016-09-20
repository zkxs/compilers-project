package net.michaelripley.pascalcompiler.tokenizers

object WordTokenizer {
  val pattern = """[a-z][a-z0-9]*""".r
  val identifierTokenName = "ID"
}

import WordTokenizer._
import net.michaelripley.pascalcompiler.tokens._
import net.michaelripley.pascalcompiler.ReservedWords

/**
 * Tokenizes both identifers and reserved words
 */
class WordTokenizer(private val reservedWords: ReservedWords) extends Tokenizer {
  def extractToken(line: String, lineNumber: Int, columnOffset: Int): Option[AttributeToken] = {
    
    pattern.findFirstIn(line) match {
      case Some(wordString) => { // check if the line starts with an identifier
        // it does! it's either a reserved word or an identifier.
        val lexeme = new Lexeme(wordString, lineNumber, columnOffset)
        
        reservedWords.get(wordString.toLowerCase()) match { // check if word is reserved
          case Some(token) => Some(token.makeToken(lexeme)) // it is! return the token we got back
          case None => { // it is not, so it's an identifier
            //FIXME: add to id table
            
            // get id number from id table
            Some(new AttributeToken(identifierTokenName, "FIXME", lexeme)) // create a new token for this id
          }
        }
      }
      case None => None
    }
  }
}
