package net.michaelripley.pascalcompiler

object WordTokenizer {
  val pattern = """[a-z][a-z0-9]*""".r
  val wordToken = new PartialToken("FIXME", None)
}

import WordTokenizer._

/**
 * Tokenizes both identifers and reserved words
 */
class WordTokenizer extends Tokenizer {
  def extractToken(line: String, lineNumber: Int, columnOffset: Int): Option[Token] = {
    
    pattern.findFirstIn(line) match {
      case Some(wordString) => {
        val lexeme = new Lexeme(wordString, lineNumber, columnOffset)
        Some(wordToken.makeToken(lexeme))
      }
      case None => None
    }
  }
}