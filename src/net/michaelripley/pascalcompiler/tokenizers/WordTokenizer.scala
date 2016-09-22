package net.michaelripley.pascalcompiler.tokenizers

object WordTokenizer {
  val pattern = """[a-z][a-z0-9]*""".r
  val identifierTokenName = "ID"
}

import WordTokenizer._
import net.michaelripley.pascalcompiler.identifiers.SymbolTable
import net.michaelripley.pascalcompiler.tokens._
import net.michaelripley.pascalcompiler.ReservedStrings

/**
 * Tokenizes both identifiers and reserved words
 */
class WordTokenizer(private val reservedWords: ReservedStrings, private val symbolTable: SymbolTable) extends Tokenizer {
  
  def extractToken(line: String, lineLocation: LineLocation): Option[Token] = {
    
    pattern.findFirstIn(line) match {
      case Some(wordString) => { // check if the line starts with an identifier
        // it does! it's either a reserved word or an identifier.
        
        val lexeme = Lexeme(wordString, lineLocation)
        val lowerCaseWordString = wordString.toLowerCase()
        
        reservedWords.get(lowerCaseWordString) match { // check if word is reserved
          case Some(token) => Some(token.makeToken(lexeme)) // it is! return the token we got back
          case None => { // it is not, so it's an identifier
            
            // add identifier to table
            val identifier = symbolTable.registerSymbol(lowerCaseWordString)
            
            // create a new token for this id
            Some(new IdentifierToken(identifierTokenName, identifier))
          }
        }
      }
      case None => None
    }
  }
  
}
