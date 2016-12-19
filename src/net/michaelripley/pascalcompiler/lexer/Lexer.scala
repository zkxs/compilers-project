package net.michaelripley.pascalcompiler.lexer

import java.io.FileNotFoundException
import java.io.PrintWriter
import scala.collection.mutable.MutableList
import scala.io.Source
import scala.util.matching.Regex.Match
import scala.annotation.tailrec
import net.michaelripley.pascalcompiler.tokenizers._
import net.michaelripley.pascalcompiler.tokens._
import net.michaelripley.pascalcompiler.lexer._
import net.michaelripley.pascalcompiler.parser.Parser
import net.michaelripley.pascalcompiler.identifiers.IdentifierManager

// only Lexer._ is imported here. All other imports are above the object.
import Lexer._

object Lexer {
  
  // EOL token (a fake thing we make to insert EOL's into token list)
  private val eolToken = new Token("EOL"){
    override def toString(): String = ""
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
      
      // thing to create integer tokens
      val integerTokenCreator = (m: Match, l: Lexeme) => {
        val intVal = m.matched.toInt
        new IntegerToken(l, intVal)
      }
      
      // error tokens
      val integerErrorTooLong
          = new PartialErrorToken("LEXERR", "Integer too long")
      val integerErrorLeadingZero
          = new PartialErrorToken("LEXERR", "Integer has leading zeros")
      
      // tokenizer implementation
      new SimpleTokenizer("""\d+""".r, integerTokenCreator) {
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
  
  // create new instance of tokenizer that can tokenize anything
  private def getSuperTokenizer() = {
    // create tokenizer for identifiers/reserved words
    val wordTokenizer = new WordTokenizer(reservedWords) // <-- has state
    
    // lump all the tokenizers together, in order of priority
    new CompoundTokenizer(
      wordTokenizer,
      numberTokenizer,
      operatorTokenizer,
      punctuationTokenizer,
      garbageTokenizer)
  }
  
  def lex(filename: String): List[Token] = {

    val idManager = new IdentifierManager()
    
    // must be called per-lex because wordTokenizer has state
    val superTokenizer = getSuperTokenizer()
    
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
    
    val mutableTokens = MutableList[Token]()
    val lines = sourceFile.getLines().toArray
    lines.zipWithIndex.foreach {
      case (line, lineNumber) => { // extract fields from tuple
        mutableTokens ++= lexLine(
            superTokenizer,
            line,
            lineNumber
        )
      }
    }
    
    // EOF token
    val eofToken = new AttributeToken("EOF", None,
      Lexeme("EOF", LineLocation(lines.size, lines.last.length))){
        override def toString(): String = {
          f"                                   $tokenName%-10s        NULL"
      }
    }
    
    mutableTokens += eofToken
    val tokens = mutableTokens.toList
    // at this point, tokens is done being generated
    
    // print all tokens to token file
    tokens.foreach(token => token match {
      case t if t == eolToken => Unit
      case t: IdentifierToken => tokenWriter.println(t + s"loc${t.identifier.number}") //TODO: lookup actual location from symbol table, also make this line much shorter
      case t => tokenWriter.println(t)
    })
    
    tokenWriter.close()
    
    val parser = new Parser(tokens, lines, listWriter)
    parser.parse()
    
    listWriter.close()

    tokens.toList // TODO: return Unit
  }
  
  private def lexLine(
      superTokenizer: Tokenizer,
      line: String,
      lineNumber: Int): MutableList[Token] = {
    
    // Now, tokenize the line
    val tokens = tokenizeLine(superTokenizer, line, lineNumber)
    tokens += eolToken
    tokens
  }
  
  /**
   * Tokenize line, starting from beginning
   */
  private def tokenizeLine(
      superTokenizer: Tokenizer,
      line: String,
      lineNumber: Int): MutableList[Token] = {
    
    tokenizeLine(superTokenizer,
        LineFragment(line, LineLocation(lineNumber, 0)),
        MutableList.empty[Token])
  }
  
  /**
   * Tokenize line, starting from given location
   */
  @tailrec
  private def tokenizeLine(
      superTokenizer: Tokenizer,
      line: LineFragment,
      priorTokens: MutableList[Token]): MutableList[Token] = {
    
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
          priorTokens += token
      )
    }
  }
  
}
