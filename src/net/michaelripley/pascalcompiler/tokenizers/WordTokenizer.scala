package net.michaelripley.pascalcompiler.tokenizers

import net.michaelripley.pascalcompiler.tokens._

private object WordTokenizer {
  private val pattern = """[a-z][a-z0-9]*""".r
  private val identifierTokenName = "ID"
  private val IdentifierTooLongError
      = new PartialErrorToken("LEXERR", "Identifier too long")
  private val maxIdentifierLength = 10
}

import WordTokenizer._
import net.michaelripley.pascalcompiler.identifiers.SymbolTable
import net.michaelripley.pascalcompiler.ReservedStrings
import net.michaelripley.pascalcompiler.Lexeme
import net.michaelripley.pascalcompiler.LineFragment

/**
 * Tokenizes both identifiers and reserved words
 */
class WordTokenizer(
    private val reservedWords: ReservedStrings,
    private val symbolTable: SymbolTable) extends Tokenizer {
  
  def extractToken(line: LineFragment): Option[Token] = {
    
    pattern.findPrefixOf(line.contents) match {
      case Some(wordString) => { // check if the line starts with an identifier
        // it does! it's either a reserved word or an identifier.
        
        val lexeme = Lexeme(wordString, line.location)
        
        if (wordString.length() > maxIdentifierLength) {
          Some(IdentifierTooLongError.makeToken(lexeme))
        } else {
          val lowerCaseWordString = wordString.toLowerCase()
          
          // check if word is reserved
          reservedWords.get(lowerCaseWordString) match {
            // it is! return the token we got back
            case Some(token) => Some(token.makeToken(lexeme))
            
            // it is not, so it's an identifier
            case None => {
              
              // add identifier to table
              val identifier = symbolTable.registerSymbol(lowerCaseWordString)
              
              // create a new token for this id
              Some(new IdentifierToken(identifierTokenName, identifier, lexeme))
            }
          }
        }
      }
      case None => None
    }
  }
  
}
