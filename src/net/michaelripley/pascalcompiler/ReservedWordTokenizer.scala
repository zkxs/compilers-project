package net.michaelripley.pascalcompiler

import ReservedWordTokenizer.{reservedWords, reservedWordMap}

class ReservedWordTokenizer(tokenName: String, attribute: String) extends Tokenizer {
  
  // valid reserved word characters
  val validChars = ('a' to 'z')
  
  def extractTokenImpl(lineNumber: Int, offset: Int, line: String): Option[Token] = {
    
    val lowerCaseLine = line.toLowerCase()
    
    val possibleReservedWords = reservedWords
      .filter(lowerCaseLine.startsWith)
      .filter(rword => {
        if (lowerCaseLine.size == rword.size) {
          // this means word ends the line
          true
        } else {
          val nextChar = lowerCaseLine.charAt(rword.size)
          
          // if the next char is valid, something is wrong and this isn't a reserved word after all
          !validChars.contains(nextChar)
        }
      })
      .toList
    
    if (possibleReservedWords.isEmpty) {
      // this block is executed when we do not find a matching reserved word
      None
    } else if (possibleReservedWords.size == 1) {
      // this block is executed when we find a reserved word in the line
      
      val rword = possibleReservedWords(0)                // we found one match. get it.
      val lexeme = new Lexeme(rword, lineNumber, offset)  // now create the lexeme
      val partialToken = reservedWordMap(rword)           // grab the partial token from the table
      val token = partialToken.makeToken(lexeme)          // add the lexeme to the token
      Some(token)                                         // return it
    } else { // implies more than one match
      throw new IllegalArgumentException("Ambiguous reserved word? Shouldn't be possible.")
    }
  }
}

import scala.io.Source

object ReservedWordTokenizer {
  
  private val pattern = """^([^\s]+)\s+([^\s]+)\s*([^\s]*)\s*$""".r
  private val filename = "reservedwords.dat"
  val reservedWordMap = load()
  val reservedWords = reservedWordMap.keySet
  
  def load(): Map[String, PartialToken] = {
    Source.fromFile(filename).getLines().map {
      line => line match {
        case pattern(lexeme, token, attribute) => (lexeme, new PartialToken(token, attribute))
        case _ => throw new IllegalArgumentException(s"""Invalid line "$line" in $filename""")
      }
    }.toMap
  }
}
