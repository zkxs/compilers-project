package net.michaelripley.pascalcompiler

import java.io.PrintWriter
import scala.collection.mutable.MutableList
import scala.io.Source
import scala.util.matching.Regex.Match
import net.michaelripley.pascalcompiler.identifiers.SymbolTable
import net.michaelripley.pascalcompiler.tokenizers._
import net.michaelripley.pascalcompiler.tokens._

object Lexer {
  def main(args: Array[String]): Unit = {
    val lexer = new Lexer(
        Source.fromFile("reservedwords.dat"),
        Source.fromFile("operators.dat"),
        Source.fromFile("punctuation.dat")
    )
    lexer.lex("test1_correct.pas")
    lexer.lex("test2_errors.pas")
  }
  
  // EOF token
  private val eofToken = new Token("EOF"){
    override def toString(): String = {
      s"                                   $tokenName"
    }
  }
  
  // tokenizes garbage
  private val garbageTokenizer = new CatchAllTokenizer()
  
  // tokenizes any number
  private val numberTokenizer = {
    
    val maxIntegerLength = 10
    val maxRealPartLength = 5
    val maxExponentLength = 2
    
    // tokenizes integers
    val integerTokenizer = {
      
      // successful token
      val integerToken = new PartialAttributeToken("INTEGER")
      
      // error tokens
      val integerErrorTooLong
          = new PartialErrorToken("LEXERR", "Integer too long")
      val integerErrorLeadingZero
          = new PartialErrorToken("LEXERR", "Integer has leading zeros")
      
      // tokenizer implementation
      new SimpleTokenizer("""\d+""".r, integerToken) {
        override def checkError(matchResult: Match, lexeme: Lexeme): 
            Option[AttributeToken] = {
          val intString = matchResult.matched
          if (intString.length() > maxIntegerLength) {
            // check if integer is too long
            Some(integerErrorTooLong.makeToken(lexeme))
          } else if (intString.length > 1 && intString.head == '0') {
            // check if integer has leading zeros
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
      val realErrorTooLong
          = new PartialErrorToken("LEXERR", "Real too long")
      val realErrorLeadingZero
          = new PartialErrorToken("LEXERR", "Real has leading zeros")
      val realErrorTrailingZero
          = new PartialErrorToken("LEXERR", "Real has trailing zeros")
      
      // tokenizer implementation
      new SimpleTokenizer("""(\d+)\.(\d+)""".r, realToken) {
        override def checkError(matchResult: Match, lexeme: Lexeme):
            Option[AttributeToken] = {
          val integerPart    = matchResult.group(1)
          val fractionalPart = matchResult.group(2)
          if (integerPart.length > maxRealPartLength
              || fractionalPart.length > maxRealPartLength) {
            // check if parts are too long
            Some(realErrorTooLong.makeToken(lexeme))
          } else if (integerPart.length > 1 && integerPart.head == '0') {
            // check for leading zeros
            Some(realErrorLeadingZero.makeToken(lexeme))
          } else if (fractionalPart.length > 1 
              && fractionalPart.charAt(fractionalPart.length - 1) == '0') {
            // check for trailing zeros
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
      val longRealErrorTooLong
          = new PartialErrorToken("LEXERR", "LongReal too long")
      val longRealErrorLeadingZero
          = new PartialErrorToken("LEXERR", "LongReal has leading zeros")
      val longRealErrorTrailingZero
          = new PartialErrorToken(
              "LEXERR", "LongReal fractional part has trailing zeros")
      val longRealErrorExponentZero
          = new PartialErrorToken("LEXERR", "LongReal exponent is zero")
      val longRealErrorExponentLeadingZero
          = new PartialErrorToken(
              "LEXERR", "LongReal exponent has leading zeros")
      
      // tokenizer implementation
      new SimpleTokenizer("""(\d+)\.(\d+)[Ee]([-+]?)(\d+)""".r, longRealToken) {
        override def checkError(matchResult: Match, lexeme: Lexeme):
            Option[AttributeToken] = {
          val integerPart    = matchResult.group(1)
          val fractionalPart = matchResult.group(2)
          //  sign           = matchResult.group(3) // currently unused
          val exponentPart   = matchResult.group(4)
          if (integerPart.length > maxRealPartLength
              || fractionalPart.length > maxRealPartLength
              || exponentPart.length > maxExponentLength) {
            // check if parts are too long
            Some(longRealErrorTooLong.makeToken(lexeme))
          } else if (integerPart.length > 1 && integerPart.head == '0') {
            // check for leading zeros
            Some(longRealErrorLeadingZero.makeToken(lexeme))
          } else if (fractionalPart.length > 1 
              && fractionalPart.tail == '0') {
            // check for trailing zeros
            Some(longRealErrorTrailingZero.makeToken(lexeme))
          } else if (exponentPart == "0") {
            Some(longRealErrorExponentZero.makeToken(lexeme))
          } else if (exponentPart.head == '0') {
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

// only Lexer._ is imported here. All other imports are above the object.
import Lexer._

class Lexer(
    reservedWordFile: Source,
    operatorsFile: Source,
    punctuationFile: Source) {
  
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
    
    // lump all the tokenizers together, in order of priority
    new CompoundTokenizer(
      wordTokenizer,
      numberTokenizer,
      operatorTokenizer,
      punctuationTokenizer,
      garbageTokenizer)
  }
  
  def lex(filename: String): List[Token] = {
    val sourceFile = Source.fromFile(filename)
    val listWriter = new PrintWriter(filename + ".listing")
    val tokenWriter = new PrintWriter(filename + ".tokens")
    
    tokenWriter.println(
        "Line No.    Lexeme                 Token-Type      Attribute")
    
    val tokens = MutableList[Token]()
    sourceFile.getLines().zipWithIndex.foreach {
      case (line, lineNumber) => { // extract fields from tuple
        tokens ++= lexLine(listWriter, tokenWriter, line, lineNumber)
      }
    }
    
    tokens += eofToken
    tokenWriter.println(eofToken)
    
    listWriter.close()
    tokenWriter.close()
    
    tokens.toList
  }
  
  private def lexLine(
      listWriter: PrintWriter,
      tokenWriter: PrintWriter,
      line: String,
      lineNumber: Int): List[Token] = {
    
    // First, output the line to the listing
    listWriter.println(f"${lineNumber + 1}%5d: $line")
    
    // Now, tokenize the line
    val tokens = tokenizeLine(line, lineNumber)
    
    tokens.foreach( token => {
      tokenWriter.println(token)
      
      token match {
        case error: ErrorToken => listWriter.println(error.errorString())
        case _ =>
      }
      
    })
    
    tokens
  }
  
  /**
   * Tokenize line, starting from beginning
   */
  private def tokenizeLine(line: String, lineNumber: Int): List[Token] = {
    tokenizeLine(LineFragment(line, LineLocation(lineNumber, 0)), List.empty)
  }
  
  import scala.annotation.tailrec
  
  /**
   * Tokenize line, starting from given location
   */
  @tailrec
  private def tokenizeLine(line: LineFragment, priorTokens: List[Token]):
      List[Token] = {
    
    val spacelessLine = line.removeLeadingSpace
    
    if (spacelessLine.isEmpty()) {
      priorTokens
    } else {
      
      val token = anythingTokenizer.extractToken(spacelessLine).get
      val length = token match {
        case token: AttributeToken  => token.lexeme.size()
        case token: IdentifierToken => token.lexeme.size()
      }
      
      tokenizeLine(spacelessLine.offset(length), priorTokens :+ token)
    }
  }
  
}
