package net.michaelripley.pascalcompiler

import scala.io.Source
import scala.util.matching.Regex.Match

object Lexer {
  def main(args: Array[String]): Unit = {
    val lexer = new Lexer(Source.fromFile("test.pas"))
    lexer.lex()
  }
  
  val integerToken = new PartialToken("INTEGER", None)
  val integerErrorTooLong = new PartialToken("LEXERR", Some("Integer too long"))
  val integerErrorLeadingZero = new PartialToken("LEXERR", Some("Integer has leading zeros"))
  
  val integerTokenizer = new SimpleTokenizer("""\d+""".r, integerToken) {
    override def checkError(matchResult: Match, lexeme: Lexeme): Option[Token] = {
      val intString = matchResult.matched
      if (intString.length() > 10) {           // check if integer is too long
        Some(integerErrorTooLong.makeToken(lexeme))
      } else if (intString.charAt(0) == '0') { // check if integer has leading zeros
        Some(integerErrorLeadingZero.makeToken(lexeme))
      } else {
        None
      }
    }
  }
}

class Lexer(val source: Source) {
  def lex(): Unit = {
    source.getLines().zipWithIndex.foreach {
      case (line, index) => {
        println(f"${index + 1}%5d: $line")
      }
    }
  }
}
