package net.michaelripley.pascalcompiler.tokenizers

import scala.util.matching.Regex
import scala.util.matching.Regex.Match
import net.michaelripley.pascalcompiler.tokens._
import net.michaelripley.pascalcompiler.lexer.Lexeme
import net.michaelripley.pascalcompiler.lexer.LineFragment

/**
 * Simple fill-in-the-blanks Tokenizer implementation that should work for most
 * use cases
 * @param pattern Pattern that matches the lexeme for this type of token
 * @param tokenCreator Method to create token if match had no errors
 *        (see {@link #checkError})
 */
class SimpleTokenizer(val pattern: Regex, val tokenCreator:
    (Match, Lexeme) => AttributeToken) extends Tokenizer {
  
  /**
   * Alternate constructor that is useful for lexemes that always tokenize
   * to the same exact token.
   * @param pattern Pattern that matches the lexeme for this type of token
   * @param partialToken Partial token to add the lexeme to
   */
  def this(pattern: Regex, partialToken: PartialAttributeToken) = {
    this(pattern, (m, l) => partialToken.makeToken(l))
  }
    
  final def extractToken(line: LineFragment): Option[AttributeToken] = {
    pattern.findPrefixMatchOf(line.contents) match {
      case Some(matchResult) => {
        // there was a match; create the lexeme
        val lexeme = Lexeme(matchResult.matched, line.location)
        // check for lexical errors in the match (e.g. number too long)
        checkError(matchResult, lexeme) match {
          // There was an error. Return its token.
          case Some(errorToken) => Some(errorToken)
          case None => { // There was no error.
            // create the token for the match
            Some(tokenCreator(matchResult, lexeme)) 
          }
        }
      }
      case None => None // There was no match :(
    }
  }
  
  /**
   * Check a match to see if it is valid. By default, all matches are valid.
   * @return A token representing the error, or None if there was no error.
   */
  def checkError(matchResult: Match, lexeme: Lexeme): Option[AttributeToken] = {
    None
  }
}
