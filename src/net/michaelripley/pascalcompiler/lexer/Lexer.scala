package net.michaelripley.pascalcompiler.lexer

import java.io.FileNotFoundException
import java.io.PrintWriter
import scala.collection.mutable.MutableList
import scala.io.Source
import scala.util.matching.Regex.Match
import scala.annotation.tailrec
import net.michaelripley.pascalcompiler.identifiers.SymbolTable
import net.michaelripley.pascalcompiler.tokenizers._
import net.michaelripley.pascalcompiler.tokens._
import net.michaelripley.pascalcompiler.lexer._

// only Lexer._ is imported here. All other imports are above the object.
import Lexer._

object Lexer {
  def main(args: Array[String]): Unit = {
    
    val lexer = new Lexer(
        resource("/reservedwords.dat"),
        resource("/operators.dat"),
        resource("/punctuation.dat")
    )
    
    args.foreach(lexer.lex)
  }
  
  private def resource(path: String): Source = {
    Source.fromInputStream(getClass.getResourceAsStream(path))
  }
  
  // EOF token
  private val eofToken = new Token("EOF"){
    override def toString(): String = {
      f"                                   $tokenName%-10s        NULL"
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
              && fractionalPart.last == '0') {
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
              && fractionalPart.last == '0') {
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
import net.michaelripley.pascalcompiler.lexer.LineLocation
import net.michaelripley.pascalcompiler.lexer.ReservedStrings

class Lexer(
    reservedWordFile: Source,
    operatorsFile: Source,
    punctuationFile: Source) {
  
  // load words in from files
  val reservedWords = new ReservedStrings(reservedWordFile)
  val operators = new ReservedStrings(operatorsFile)
  val punctuation = new ReservedStrings(punctuationFile)
  
  // create tokenizer for each type of token
  val operatorTokenizer = new StringTokenizer(operators)
  val punctuationTokenizer = new StringTokenizer(punctuation)
  
  // create tokenizer that can tokenize anything
  private def getSuperTokenizer(symbolTable: SymbolTable) = {
    // create tokenizer for identifiers/reserved words
    val wordTokenizer = new WordTokenizer(reservedWords, symbolTable)
    
    // lump all the tokenizers together, in order of priority
    new CompoundTokenizer(
      wordTokenizer,
      numberTokenizer,
      operatorTokenizer,
      punctuationTokenizer,
      garbageTokenizer)
  }
  
  def lex(filename: String): List[Token] = {
    val symbolTable = new SymbolTable()
    val superTokenizer = getSuperTokenizer(symbolTable)
    
    
    val sourceFile:Source = try {
      Source.fromFile(filename)
    } catch {
      case e: FileNotFoundException => {
        println(s"${e.getClass.getSimpleName}: ${e.getMessage}")
        System.exit(1)
        null
      }
    }
    
    
    val listWriter = new PrintWriter(filename + ".listing")
    val tokenWriter = new PrintWriter(filename + ".tokens")
    
    tokenWriter.println(
        "Line No.    Lexeme                 Token-Type        Attribute")
    
    val tokens = MutableList[Token]()
    sourceFile.getLines().zipWithIndex.foreach {
      case (line, lineNumber) => { // extract fields from tuple
        tokens ++= lexLine(
            superTokenizer,
            listWriter,
            tokenWriter,
            line,
            lineNumber
        )
      }
    }
    
    tokens += eofToken
    tokenWriter.println(eofToken)
    
    listWriter.close()
    tokenWriter.close()
    
    tokens.toList
  }
  
  private def lexLine(
      superTokenizer: Tokenizer,
      listWriter: PrintWriter,
      tokenWriter: PrintWriter,
      line: String,
      lineNumber: Int): List[Token] = {
    
    // First, output the line to the listing
    listWriter.println(f"${lineNumber + 1}%5d: $line")
    
    // Now, tokenize the line
    val tokens = tokenizeLine(superTokenizer, line, lineNumber)
    
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
  private def tokenizeLine(
      superTokenizer: Tokenizer,
      line: String,
      lineNumber: Int): List[Token] = {
    
    tokenizeLine(superTokenizer,
        LineFragment(line, LineLocation(lineNumber, 0)),
        List.empty)
  }
  
  import scala.annotation.tailrec
  
  /**
   * Tokenize line, starting from given location
   */
  @tailrec
  private def tokenizeLine(
      superTokenizer: Tokenizer,
      line: LineFragment,
      priorTokens: List[Token]): List[Token] = {
    
    val spacelessLine = line.removeLeadingSpace
    
    if (spacelessLine.isEmpty()) {
      priorTokens
    } else {
      
      val token = superTokenizer.extractToken(spacelessLine).get
      val length = token match {
        case token: AttributeToken  => token.lexeme.size()
        case token: IdentifierToken => token.lexeme.size()
      }
      
      tokenizeLine(
          superTokenizer,
          spacelessLine.offset(length),
          priorTokens :+ token
      )
    }
  }
  
}
