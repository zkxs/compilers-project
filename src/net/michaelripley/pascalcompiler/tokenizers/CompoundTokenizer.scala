package net.michaelripley.pascalcompiler.tokenizers

import net.michaelripley.pascalcompiler.tokens._
import net.michaelripley.pascalcompiler.LineFragment

/**
 * A tokenizer made of several tokenizers stuck together.
 * The first tokenizer to succeed will be used.
 */
class CompoundTokenizer(val tokenizers: Tokenizer*) extends Tokenizer {
  
  def extractToken(line: LineFragment): Option[Token] = {
    val iter = tokenizers.iterator
    
    while (iter.hasNext) {
      val tokenizer = iter.next()
      val result = tokenizer.extractToken(line)
      if (result.isDefined) {
        return result
      }
    }
    
    return None
  }
  
}
