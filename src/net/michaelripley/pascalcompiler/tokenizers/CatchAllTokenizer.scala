package net.michaelripley.pascalcompiler.tokenizers

import net.michaelripley.pascalcompiler.tokens._

private object CatchAllTokenizer {
  private val garbageError = new PartialErrorToken("LEXERR", "Unknown character")
}

import CatchAllTokenizer._

class CatchAllTokenizer extends Tokenizer {
  def extractToken(line: String, lineNumber: Int, columnOffset: Int): Option[AttributeToken] = {
    
    // get a single character worth of garbage
    val garbage = line.head.toString()
    
    // make the lexeme
    val lexeme = new Lexeme(garbage, lineNumber, columnOffset)
    
    // make and return the token
    Some(garbageError.makeToken(lexeme))
  }
}
