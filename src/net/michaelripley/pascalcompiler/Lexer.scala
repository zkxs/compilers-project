package net.michaelripley.pascalcompiler

import scala.io.Source
import scala.util.matching.Regex.Match
import net.michaelripley.pascalcompiler.identifiers.SymbolTable
import net.michaelripley.pascalcompiler.tokenizers._
import net.michaelripley.pascalcompiler.tokens._

object Lexer {
  def main(args: Array[String]): Unit = {
    val lexer = new Lexer(
        Source.fromFile("test.pas"),
        Source.fromFile("reservedwords.dat"),
        Source.fromFile("operators.dat"),
        Source.fromFile("punctuation.dat")
    )
    lexer.lex()
  }
  
  // tokenizes any number
  private val numberTokenizer = {
    
    // tokenizes integers
    val integerTokenizer = {
      
      // successful token
      val integerToken = new PartialAttributeToken("INTEGER")
      
      // error tokens
      val integerErrorTooLong     = new PartialErrorToken("LEXERR", "Integer too long")
      val integerErrorLeadingZero = new PartialErrorToken("LEXERR", "Integer has leading zeros")
      
      // tokenizer implementation
      new SimpleTokenizer("""\d+""".r, integerToken) {
        override def checkError(matchResult: Match, lexeme: Lexeme): Option[AttributeToken] = {
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
    
    // tokenizes short reals
    val realTokenizer = {
      
      // successful token
      val realToken = new PartialAttributeToken("REAL")
      
      // error tokens
      val realErrorTooLong      = new PartialErrorToken("LEXERR", "Real too long")
      val realErrorLeadingZero  = new PartialErrorToken("LEXERR", "Real has leading zeros")
      val realErrorTrailingZero = new PartialErrorToken("LEXERR", "Real has trailing zeros")
      
      // tokenizer implementation
      new SimpleTokenizer("""(\d+)\.(\d+)""".r, realToken) {
        override def checkError(matchResult: Match, lexeme: Lexeme): Option[AttributeToken] = {
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
    }
    
    // tokenizes long reals
    val longRealTokenizer = {
      
      // successful token
      val longRealToken = new PartialAttributeToken("LONGREAL")
      
      // error tokens
      val longRealErrorTooLong             = new PartialErrorToken("LEXERR", "LongReal too long")
      val longRealErrorLeadingZero         = new PartialErrorToken("LEXERR", "LongReal has leading zeros")
      val longRealErrorTrailingZero        = new PartialErrorToken("LEXERR", "LongReal fractional part has trailing zeros")
      val longRealErrorExponentZero        = new PartialErrorToken("LEXERR", "LongReal exponent is zero")
      val longRealErrorExponentLeadingZero = new PartialErrorToken("LEXERR", "LongReal exponent has leading zeros")
      
      // tokenizer implementation
      new SimpleTokenizer("""(\d+)\.(\d+)[Ee]([-+]?)(\d+)""".r, longRealToken) {
        override def checkError(matchResult: Match, lexeme: Lexeme): Option[AttributeToken] = {
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
    }
    
    // compound the three number-type tokenizers into one
    new CompoundTokenizer(longRealTokenizer, realTokenizer, integerTokenizer)
  }
  
} // end of object block

import Lexer._

class Lexer(val sourceFile: Source, reservedWordFile: Source, operatorsFile: Source, punctuationFile: Source) {
  
  // create new symbol table
  private val symbolTable = new SymbolTable()
  
  // create tokenizer that can tokenize anything
  private val anythingTokenizer = {
    // load words in from files
    val reservedWords = new ReservedStrings(reservedWordFile)
    val operators = new ReservedStrings(operatorsFile)
    val punctuation = new ReservedStrings(punctuationFile)
    
    // create tokenizers for each type of token
    val wordTokenizer = new WordTokenizer(reservedWords, symbolTable)
    val operatorTokenizer = new StringTokenizer(operators)
    val punctuationTokenizer = new StringTokenizer(punctuation)
    // numberTokenizer is a value of the Lexer object
    
    // lump all the tokenizers together, in order of priority
    new CompoundTokenizer(
      wordTokenizer,
      numberTokenizer,
      operatorTokenizer,
      punctuationTokenizer)
  }
  
  /* TODO: write to files
   * 1. listing file
   * 2. token file
   * 
   * TODO: save tokens to list for future use
   */
  
  def lex(): Unit = {
    sourceFile.getLines().zipWithIndex.foreach {
      case (line, index) => { // extract fields from tuple
        lexLine(line, index)
      }
    }
  }
  
  private def lexLine(line: String, index: Int) = {
    println(f"${index + 1}%5d: $line")
  }
}
