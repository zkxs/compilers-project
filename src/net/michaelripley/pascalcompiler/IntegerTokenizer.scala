package net.michaelripley.pascalcompiler

object IntegerTokenizer {
  val pattern = """\d+""".r
  val numberToken = new PartialToken("INTEGER", None)
}

import IntegerTokenizer._

class IntegerTokenizer extends Tokenizer {
  def extractTokenImpl(lineNumber: Int, offset: Int, line: String): Option[Token] = {
    pattern.findFirstIn(line) match {
      case Some(numberString) => {
        val lexeme = new Lexeme(numberString, lineNumber, offset)
        Some(numberToken.makeToken(lexeme))
      }
      case None => None
    }
  }
}