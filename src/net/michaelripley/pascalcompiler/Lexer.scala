package net.michaelripley.pascalcompiler

import scala.io.Source
import scala.util.matching.Regex.Match

object Lexer {
  def main(args: Array[String]): Unit = {
    val lexer = new Lexer(
        Source.fromFile("test.pas"),
        Source.fromFile("reservedwords.dat")
    )
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
  
  val realToken = new PartialToken("REAL", None)
  val realErrorTooLong = new PartialToken("LEXERR", Some("Real too long"))
  val realErrorLeadingZero = new PartialToken("LEXERR", Some("Real has leading zeros"))
  val realErrorTrailingZero = new PartialToken("LEXERR", Some("Real has trailing zeros"))
  val realTokenizer = new SimpleTokenizer("""(\d+)\.(\d+)""".r, realToken) {
    override def checkError(matchResult: Match, lexeme: Lexeme): Option[Token] = {
      val integerPart    = matchResult.group(1)
      val fractionalPart = matchResult.group(2)
      if (integerPart.length > 5 || fractionalPart.length > 5) { // check if parts are too long
        Some(realErrorTooLong.makeToken(lexeme))
      } else if (integerPart.charAt(0) == '0') { // check for leading zeros
        Some(realErrorLeadingZero.makeToken(lexeme))
      } else if (fractionalPart.length > 1 
          && fractionalPart.charAt(fractionalPart.length - 1) == '0') { // check for trailing zeros
        Some(realErrorTrailingZero.makeToken(lexeme))
      } else {
        None
      }
    }
  }
  
  val longRealToken = new PartialToken("LONGREAL", None)
  val longRealErrorTooLong = new PartialToken("LEXERR", Some("LongReal too long"))
  val longRealErrorLeadingZero = new PartialToken("LEXERR", Some("LongReal has leading zeros"))
  val longRealErrorTrailingZero = new PartialToken("LEXERR", Some("LongReal fractional part has trailing zeros"))
  val longRealErrorExponentZero = new PartialToken("LEXERR", Some("LongReal exponent is zero"))
  val longRealErrorExponentLeadingZero = new PartialToken("LEXERR", Some("LongReal exponent has leading zeros"))
  
  val longRealTokenizer = new SimpleTokenizer("""(\d+)\.(\d+)[Ee]([-+]?)(\d+)""".r, longRealToken) {
    override def checkError(matchResult: Match, lexeme: Lexeme): Option[Token] = {
      val integerPart    = matchResult.group(1)
      val fractionalPart = matchResult.group(2)
      //  sign           = matchResult.group(3) // currently unused
      val exponentPart   = matchResult.group(4)
      if (integerPart.length > 5
          || fractionalPart.length > 5
          || exponentPart.length > 2) { // check if parts are too long
        Some(longRealErrorTooLong.makeToken(lexeme))
      } else if (integerPart.charAt(0) == '0') { // check for leading zeros
        Some(longRealErrorLeadingZero.makeToken(lexeme))
      } else if (fractionalPart.length > 1 
          && fractionalPart.charAt(fractionalPart.length - 1) == '0') { // check for trailing zeros
        Some(longRealErrorTrailingZero.makeToken(lexeme))
      } else if (exponentPart == "0") {
        Some(longRealErrorExponentZero.makeToken(lexeme))
      } else if (exponentPart.charAt(0) == '0') {
        Some(longRealErrorExponentLeadingZero.makeToken(lexeme))
      } else {
        None
      }
    }
  }
  
  
} // end of object block

class Lexer(val sourceFile: Source, val reservedWordFile: Source) {
  def lex(): Unit = {
    sourceFile.getLines().zipWithIndex.foreach {
      case (line, index) => {
        println(f"${index + 1}%5d: $line")
      }
    }
  }
}
