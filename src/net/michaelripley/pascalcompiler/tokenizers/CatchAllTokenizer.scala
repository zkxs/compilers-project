package net.michaelripley.pascalcompiler.tokenizers

// Only tokens._ is imported here. All other imports are below the object.
import net.michaelripley.pascalcompiler.tokens._

private object CatchAllTokenizer {
  private val garbageError = new PartialErrorToken("LEXERR", "Unknown character")
}

import CatchAllTokenizer._
import net.michaelripley.pascalcompiler.Lexeme
import net.michaelripley.pascalcompiler.LineLocation

class CatchAllTokenizer extends Tokenizer {
  def extractToken(line: String, lineLocation: LineLocation): Option[AttributeToken] = {
    
    // get a single character worth of garbage
    val garbage = line.head.toString()
    
    // make the lexeme
    val lexeme = Lexeme(garbage, lineLocation)
    
    // make and return the token
    Some(garbageError.makeToken(lexeme))
  }
}
