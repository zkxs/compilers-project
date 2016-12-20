package net.michaelripley.pascalcompiler.parser

private object Parser {
  val q = '"'
}

import scala.annotation.tailrec
import java.io.PrintWriter
import net.michaelripley.pascalcompiler.tokens._
import net.michaelripley.pascalcompiler.identifiers._
import Type._
import Parser._

class Parser(
    tokens: List[Token],
    lines: Array[String],
    listWriter: PrintWriter,
    idMan: IdentifierManager) {
  
  // easy tokens to match
  private val PROGRAM = new AttributeToken("PROGRAM")
  private val PROCEDURE = new AttributeToken("PROCEDURE")
  private val BEGIN = new AttributeToken("BEGIN")
  private val CALL = new AttributeToken("CALL")
  private val VAR = new AttributeToken("VAR")
  private val INTEGER = new AttributeToken("TYPENAME", "INTEGER")
  private val REAL = new AttributeToken("TYPENAME", "REAL")
  private val ARRAY = new AttributeToken("ARRAY")
  private val OF = new AttributeToken("OF")
  private val PAREN_OPEN = new AttributeToken("PAREN", "OPEN")
  private val PAREN_CLOSE = new AttributeToken("PAREN", "CLOSE")
  private val SQUAREBRACKET_OPEN = new AttributeToken("SQUAREBRACKET", "OPEN")
  private val SQUAREBRACKET_CLOSE = new AttributeToken("SQUAREBRACKET", "CLOSE")
  private val COMMA = new AttributeToken("COMMA")
  private val COLON = new AttributeToken("COLON")
  private val SEMICOLON = new AttributeToken("SEMICOLON")
  private val FULLSTOP = new AttributeToken("FULLSTOP")
  private val ARRAYRANGE = new AttributeToken("ARRAYRANGE")
  private val PLUS = new AttributeToken("ADDOP", "ADD")
  private val MINUS = new AttributeToken("ADDOP", "SUBTRACT")
  private val ASSIGNOP = new AttributeToken("ASSIGNOP")
  private val NOT = new AttributeToken("NOT")
  private val IF = new AttributeToken("IF")
  private val THEN = new AttributeToken("THEN")
  private val ELSE = new AttributeToken("ELSE")
  private val DO = new AttributeToken("DO")
  private val WHILE = new AttributeToken("WHILE")
  private val END = new AttributeToken("END")
  private val EOF = new AttributeToken("EOF")
  private val EOL = new Token("EOL") // fake shenanigans to make printing work
  
  // harder tokens to match (they can be different literals)
  
  private type TokenMatcher = (Token => (Boolean, String))
  private type SyncSet = (Set[Token], Set[TokenMatcher])
  
  private val ID: TokenMatcher = (t: Token) => {
    (t match {
      case i: IdentifierToken => true
      case _ => false
    }, "ID")
  }
  
  private val NUM: TokenMatcher = (t: Token) => {
    (t match {
      case _: IntegerToken => true
      case a: AttributeToken
        if (a.tokenName == "REAL" || a.tokenName == "LONGREAL") => true
      case _ => false
    }, "NUM")
  }
  
  private val RELOP: TokenMatcher = (t: Token) => {
    (t match {
      case a: AttributeToken if a.tokenName == "RELOP" => true
      case _ => false
    }, "RELOP")
  }
  
  private val ADDOP: TokenMatcher = (t: Token) => {
    (t match {
      case a: AttributeToken if a.tokenName == "ADDOP" => true
      case _ => false
    }, "ADDOP")
  }
  
  private val MULOP: TokenMatcher = (t: Token) => {
    (t match {
      case a: AttributeToken if a.tokenName == "MULOP" => true
      case _ => false
    }, "MULOP")
  }
  
  private val tokenIterator = tokens.iterator
  private var currentToken: Token = _
  private var lineNumber = 0
  
  @tailrec
  private def nextToken(): Unit = {
    currentToken = tokenIterator.next()
    
    if (currentToken == EOL) {
      lineNumber += 1
      if (lineNumber < lines.size) {
        printLine(lineNumber)
      }
      nextToken()
    }
  }
  
  private def printLine(lineNumber: Int) = {
    listWriter.println(f"${lineNumber + 1}%5d: ${lines(lineNumber)}")
  }
		  
  def parse() = {
    printLine(0)
    nextToken()
    program()
    matchToken(EOF, (Set.empty[Token], Set.empty[TokenMatcher]))
  }
  
  private def matchToken(m: TokenMatcher, sync: SyncSet): Option[Token] = {
    val (matched, name) = m(currentToken)
    if (matched) {
      if (currentToken != EOF) {
        val toReturn = currentToken
        nextToken()
        Some(toReturn)
      } else {
        /* Typically, you would exit the parser after reading an expected EOF,
         * however the only case in which this happens in this program is at
         * the end of parse(), so simply returning is acceptable behavior.
         */
        Some(currentToken)
      }
    } else {
      syntaxError(name, sync)
      None
    }
  }
  
  private def matchToken(t: Token, sync: SyncSet): Option[Token] = {
    matchToken(curr => (t == curr,
      t match {
        case at: AttributeToken => {
          at.attribute match {
            case Some(attr) => s"${at.tokenName}_$attr"
            case _ => at.tokenName
          }
        }
        case _ => t.tokenName
      }
    ), sync)
  }
  
  private def isCurrentToken(m: TokenMatcher): Boolean = {
    m(currentToken)._1
  }
  
  private def isCurrentToken(t: Token): Boolean = {
    t == currentToken
  }
  
  private def isCurrentToken(toks: Token*): Boolean = {
    toks.foreach( t => {
      if (isCurrentToken(t)) {
        return true
      }
    })
    return false
  }
  
  private def isCurrentTokenInSync(sync: SyncSet): Boolean = {
    val (sync1, sync2) = sync
    
    currentToken == EOF || sync1.contains(currentToken) ||
        sync2.find(_(currentToken)._1).isDefined
  }
  
  /**
   * Perform generic error reporting and recovery
   * @param message Error message
   * @param sync1 Sync set
   * @param sync2 Extra sync set of TokenMatchers
   */
  private def error(message: String, sync: SyncSet): Unit = {
    
    listWriter.println(message)
    
    while (!isCurrentTokenInSync(sync)) {
      nextToken()
      currentToken match {
        case et: ErrorToken => listWriter.println(et.fullErrorString())
        case _ =>
      }
    }
  }
  
  /**
   * Perform generic error reporting and recovery
   * @param message Error message
   * @param sync Sync set
   */
  private def error(message: String, sync: Set[Token]): Unit = {
    error(message, (sync, Set.empty[TokenMatcher]))
  }
  
  private def syntaxError(expectedTokens: String, sync: SyncSet): Unit = {
    
    val lexeme = currentToken match {
      case at: AttributeToken  => at.lexeme
      case id: IdentifierToken => id.lexeme
    }
    
    val space = " " * (lexeme.location.columnOffset + 7)
    
    val errorString = currentToken match {
      case et: ErrorToken => {
        et.errorString()
      }
      case _ => {
        s"^ SYNERR: expected one of: $expectedTokens but got $q${lexeme.lexeme}$q"
      }
    }
    
    error(space + errorString, sync)
  }
  
  private def semanticError(message: String, sync: SyncSet): Unit = {
    val lexeme = currentToken match {
      case at: AttributeToken  => at.lexeme
      case id: IdentifierToken => id.lexeme
    }
    
    val space = " " * (lexeme.location.columnOffset + 7)
    
    error(space + "^ SEMERR: " + message, sync)
  }
  
  /**
   * Optionally process an IdentifierError
   * @return true if there was no error
   */
  private def semanticError(
      err: Option[IdentifierError], sync: SyncSet): Boolean = {
    
    err.fold(true)(e => {
      semanticError(e.message, sync)
      false
    })
  }
  
  /**
   * @return true if all options exist
   */
  private def exists(things: Option[Any]*): Boolean = {
    things.forall(_.isDefined)
  }
  
  private def extractId(tok: Option[Token]): Option[Identifier] = {
    tok match {
      case Some(it: IdentifierToken) => Some(it.identifier)
      case _ => None
    }
  }
  
  /* *************************************************************************
   *      BEGINNING OF RECURSIVE DESCENT PARSER COOKIE-CUTTER FUNCTIONS
   * *************************************************************************/
  
  import idMan._ // bring the functions in idMan into this scope
  
  private def program(): Unit = {
    val sync = (Set.empty[Token], Set.empty[TokenMatcher])
    
    if (isCurrentToken(PROGRAM)) {
      matchToken(PROGRAM, sync)
      val optId = extractId(matchToken(ID, sync))
      matchToken(PAREN_OPEN, sync)
      val optParams = identifierList()
      
      val success = if (exists(optId, optParams)) {
        semanticError(addProgram(optId.get, optParams.get), sync)
      } else {
        false
      }
      
      matchToken(PAREN_CLOSE, sync)
      matchToken(SEMICOLON, sync)
      programPrime()
      
      if (success) {
        semanticError(pop(), sync)
      }
    } else {
      syntaxError("PROGRAM", sync)
    }
  }
  
  private def programPrime(): Unit = {
     val sync = (Set.empty[Token], Set.empty[TokenMatcher])
    
    if (isCurrentToken(PROCEDURE)) {
      subprogramDeclarations()
      compoundStatement()
      matchToken(FULLSTOP, sync)
    } else if (isCurrentToken(BEGIN)) {
      compoundStatement()
      matchToken(FULLSTOP, sync)
    } else if (isCurrentToken(VAR)) {
      declarations()
      programPrime()
    } else {
      syntaxError("PROCEDURE, BEGIN, VAR", sync)
    }
  }
  
  private def identifierList(): Option[List[Type]] = {
    val sync = (Set[Token](PAREN_CLOSE), Set.empty[TokenMatcher])
    
    if (isCurrentToken(ID)) {
      matchToken(ID, sync)
      identifierListTail().map(T_ProgramParam() +: _)
    } else {
      syntaxError("ID", sync)
      None
    }
  }
  
  private def identifierListTail(): Option[List[Type]] = {
    val sync = (Set[Token](PAREN_CLOSE), Set.empty[TokenMatcher])
    
    if (isCurrentToken(PAREN_CLOSE)) {
      Some(List.empty)
    } else if (isCurrentToken(COMMA)) {
      matchToken(COMMA, sync)
      matchToken(ID, sync)
      identifierListTail().map(T_ProgramParam() +: _)
    } else {
      syntaxError("')', ','", sync)
      None
    }
  }
  
  private def declarations(): Unit = {
    val sync = (Set[Token](PROCEDURE, BEGIN), Set.empty[TokenMatcher])
    
    if (isCurrentToken(VAR)) {
      matchToken(VAR, sync)
      matchToken(ID, sync)
      matchToken(COLON, sync)
      anyType()
      matchToken(SEMICOLON, sync)
      optionalDeclarations()
    } else {
      syntaxError("VAR", sync)
    }
  }
  
  private def optionalDeclarations(): Unit = {
    val sync = (Set[Token](PROCEDURE, BEGIN), Set.empty[TokenMatcher])
    
    if (isCurrentToken(PROCEDURE, BEGIN)) {
      ()
    } else if (isCurrentToken(VAR)) {
      declarations()
    } else {
      syntaxError("PROCEDURE, BEGIN, VAR", sync)
    }
  }
  
  // could not name this function type, as type is a reserved word in Scala
  private def anyType(): Option[Type] = {
    val sync = (Set[Token](SEMICOLON, PAREN_CLOSE), Set.empty[TokenMatcher])
    
    if (isCurrentToken(INTEGER, REAL)) {
      standardType()
    } else if (isCurrentToken(ARRAY)) {
      matchToken(ARRAY, sync)
      matchToken(SQUAREBRACKET_OPEN, sync)
      val optNum1 = matchToken(NUM, sync) match {
        case Some(it: IntegerToken) => Some(it.value)
        case _ => {
          semanticError("array bound must be an integer", sync);
          None
        }
      }
      matchToken(ARRAYRANGE, sync)
      val optNum2 = matchToken(NUM, sync) match {
        case Some(it: IntegerToken) => Some(it.value)
        case _ => {
          semanticError("array bound must be an integer", sync);
          None
        }
      }
      matchToken(SQUAREBRACKET_CLOSE, sync)
      matchToken(OF, sync)
      val optType = standardType() match {
        case Some(t: ArrayableType) => Some(t)
        case _ => {
          semanticError("arrays can only contain integers or reals", sync)
          None
        }
      }
      
      if (exists(optNum1, optNum2, optType)) {
        Some(T_Array(optNum2.get - optNum1.get, optType.get))
      } else {
        None // indicative of errors we've already complained about
      }
      
    } else {
      syntaxError("INTEGER, REAL, ARRAY", sync)
      None
    }
  }
  
  private def standardType(): Option[Type] = {
    val sync = (Set[Token](SEMICOLON, PAREN_CLOSE), Set.empty[TokenMatcher])
    
    if (isCurrentToken(INTEGER)) {
      matchToken(INTEGER, sync)
      Some(T_Integer())
    } else if (isCurrentToken(REAL)) {
      matchToken(REAL, sync)
      Some(T_Real())
    } else {
      syntaxError("INTEGER, REAL", sync)
      None
    }
  }
  
  private def subprogramDeclarations(): Unit = {
    val sync = (Set[Token](BEGIN), Set.empty[TokenMatcher])
    
    if (isCurrentToken(PROCEDURE)) {
      subprogramDeclaration()
      matchToken(SEMICOLON, sync)
      optionalSubprogramDeclarations()
    } else {
      syntaxError("PROCEDURE", sync)
    }
  }
  
  private def optionalSubprogramDeclarations(): Unit = {
    val sync = (Set[Token](BEGIN), Set.empty[TokenMatcher])
    
    if (isCurrentToken(PROCEDURE)) {
      subprogramDeclarations()
    } else if (isCurrentToken(BEGIN)) {
      ()
    } else {
      syntaxError("PROCEDURE, BEGIN", sync)
    }
  }
  
  private def subprogramDeclaration(): Unit = {
    val sync = (Set[Token](SEMICOLON), Set.empty[TokenMatcher])
    
    if (isCurrentToken(PROCEDURE)) {
      val success = subprogramHead()
      subprogramDeclarationPrime()
      
      if (success) {
        semanticError(pop(), sync)
      }
    } else {
      syntaxError("PROCEDURE", sync)
    }
  }
  
  private def subprogramDeclarationPrime(): Unit = {
    val sync = (Set[Token](SEMICOLON), Set.empty[TokenMatcher])
    
    if (isCurrentToken(PROCEDURE)) {
      subprogramDeclarations()
      compoundStatement()
    } else if (isCurrentToken(BEGIN)) {
      compoundStatement()
    } else if (isCurrentToken(VAR)) {
      declarations()
      subprogramDeclarationPrime()
    } else {
      syntaxError("PROCEDURE, BEGIN, VAR", sync)
    }
  }
  
  // returns True if there was no error
  private def subprogramHead(): Boolean = {
    val sync = (Set[Token](VAR, PROCEDURE, BEGIN), Set.empty[TokenMatcher])
    
    if (isCurrentToken(PROCEDURE)) {
      matchToken(PROCEDURE, sync)
      val optId = extractId(matchToken(ID, sync))
      val optParams = subprogramHeadPrime()
      
      if (exists(optId, optParams)) {
        semanticError(addProcedure(optId.get, optParams.get), sync)
      } else {
        false
      }
      
    } else {
      syntaxError("PROCEDURE", sync)
      false
    }
  }
  
  private def subprogramHeadPrime(): Option[List[Type]] = {
    val sync = (Set[Token](VAR, PROCEDURE, BEGIN), Set.empty[TokenMatcher])
    
    if (isCurrentToken(PAREN_OPEN)) {
      val optParams = arguments()
      matchToken(SEMICOLON, sync)
      optParams
    } else if (isCurrentToken(SEMICOLON)) {
      matchToken(SEMICOLON, sync)
      Some(List.empty)
    } else {
      syntaxError("'(', ';'", sync)
      None
    }
  }
  
  private def arguments(): Option[List[Type]] = {
    val sync = (Set[Token](SEMICOLON), Set.empty[TokenMatcher])
    
    if (isCurrentToken(PAREN_OPEN)) {
      matchToken(PAREN_OPEN, sync)
      val optParams = parameterList()
      matchToken(PAREN_CLOSE, sync)
      optParams
    } else {
      syntaxError("'('", sync)
      None
    }
  }
  
  private def parameterList(): Option[List[Type]] = {
    val sync = (Set[Token](PAREN_CLOSE), Set.empty[TokenMatcher])
    
    if (isCurrentToken(ID)) {
      matchToken(ID, sync)
      matchToken(COLON, sync)
      val optType = anyType()
      val optParams = parameterListTail()
      
      if (exists(optType, optParams)) {
        Some(optType.get +: optParams.get)
      } else {
        None // indicative of errors we've already complained about
      }
    } else {
      syntaxError("ID", sync)
      None
    }
  }
  
  private def parameterListTail(): Option[List[Type]] = {
    val sync = (Set[Token](PAREN_CLOSE), Set.empty[TokenMatcher])
    
    if (isCurrentToken(PAREN_CLOSE)) {
      Some(List.empty)
    } else if (isCurrentToken(SEMICOLON)) {
      matchToken(SEMICOLON, sync)
      matchToken(ID, sync)
      matchToken(COLON, sync)
      val optType = anyType()
      val optParams = parameterListTail()
      
      if (exists(optType, optParams)) {
        Some(optType.get +: optParams.get)
      } else {
        None // indicative of errors we've already complained about
      }
    } else {
      syntaxError("')', ';'", sync)
      None
    }
  }
  
  private def compoundStatement(): Unit = {
    val sync = (Set[Token](FULLSTOP, SEMICOLON, CALL, BEGIN, IF, WHILE),
        Set[TokenMatcher](ID))
    
    if (isCurrentToken(BEGIN)) {
      matchToken(BEGIN, sync)
      compoundStatmentTail()
    } else {
      syntaxError("BEGIN", sync)
    }
  }
  
  private def compoundStatmentTail(): Unit = {
    val sync = (Set[Token](FULLSTOP, SEMICOLON, CALL, BEGIN, IF, WHILE),
        Set[TokenMatcher](ID))
    
    if (isCurrentToken(BEGIN, CALL, IF, WHILE) || isCurrentToken(ID)) {
      statementList()
      matchToken(END, sync)
    } else if (isCurrentToken(END)) {
      matchToken(END, sync)
    } else {
      syntaxError("BEGIN, CALL, ID, IF, WHILE, END", sync)
    }
  }
  
  private def statementList(): Unit = {
    val sync = (Set[Token](END), Set.empty[TokenMatcher])
    
    if (isCurrentToken(BEGIN, CALL, IF, WHILE) || isCurrentToken(ID)) {
      statement()
      statementListTail()
    } else {
      syntaxError("BEGIN, CALL, ID, IF, WHILE", sync)
    }
  }
  
  private def statementListTail(): Unit = {
    val sync = (Set[Token](END), Set.empty[TokenMatcher])
    
    if (isCurrentToken(SEMICOLON)) {
      matchToken(SEMICOLON, sync)
      statement()
      statementListTail()
    } else if (isCurrentToken(END)) {
      ()
    } else {
      syntaxError("';', END", sync)
    }
  }
  
  private def statement(): Unit = {
    val sync = (Set[Token](SEMICOLON, END, ELSE), Set.empty[TokenMatcher])
    
    if (isCurrentToken(BEGIN)) {
      compoundStatement()
    } else if (isCurrentToken(CALL)) {
      procedureStatement()
    } else if (isCurrentToken(ID)) {
      variable()
      matchToken(ASSIGNOP, sync)
      expression()
      //TODO: confirm that types are equal
    } else if (isCurrentToken(IF)) {
      matchToken(IF, sync)
      expression() //TODO: confirm type is boolean
      matchToken(THEN, sync)
      statement()
      optionalElse()
    } else if (isCurrentToken(WHILE)) {
      matchToken(WHILE, sync)
      expression() //TODO: confirm type is boolean
      matchToken(DO, sync)
      statement()
    } else {
      syntaxError("BEGIN, CALL, ID, IF, WHILE", sync)
    }
  }
  
  private def optionalElse(): Unit = {
    val sync = (Set[Token](SEMICOLON, END, ELSE), Set.empty[TokenMatcher])
    
    if (isCurrentToken(SEMICOLON, END)) {
      ()
    } else if (isCurrentToken(ELSE)) {
      matchToken(ELSE, sync)
      statement()
    } else {
      syntaxError("';', END, ELSE", sync)
    }
  }
  
  private def variable(): Option[Type] = {
    val sync = (Set[Token](ASSIGNOP), Set.empty[TokenMatcher])
    
    if (isCurrentToken(ID)) {
      val optId = extractId(matchToken(ID, sync))
      val isArrayVar = arrayVariable()
      
      if (exists(optId, isArrayVar)) {
        
        getVariable(optId.get).fold( error => { // if failure
          semanticError(error.message, sync)
          None
        }, id => { // if success
          if (isArrayVar.get) {
            // it was an array assignment
            id.idType match {
              case T_Array(_, innerType) => Some(innerType)
              case _ => {
                // this array assignment was not to an array!
                semanticError(s"array assignment to non-array $q$id$q", sync)
                None
              }
            }
          } else {
            // it was not an array assignment
            Some(id.idType)
          }
        })
      } else {
        None
      }
      
    } else {
      syntaxError("ID", sync)
      None
    }
  }
  
  // returns true if this is an arrayvar, false otherwise
  private def arrayVariable(): Option[Boolean] = {
    val sync = (Set[Token](ASSIGNOP), Set.empty[TokenMatcher])
    
    if (isCurrentToken(SQUAREBRACKET_OPEN)) {
      matchToken(SQUAREBRACKET_OPEN, sync)
      expression()
      matchToken(SQUAREBRACKET_CLOSE, sync)
      Some(true)
    } else if (isCurrentToken(ASSIGNOP)) {
      Some(false)
    } else {
      syntaxError("SQUAREBRACKET_OPEN, ASSIGNOP", sync)
      None
    }
  }
  
  private def procedureStatement(): Unit = {
    val sync = (Set[Token](SEMICOLON, END, ELSE), Set.empty[TokenMatcher])
    
    if (isCurrentToken(CALL)) {
      matchToken(CALL, sync)
      matchToken(ID, sync)
      optionalExpressionList()
    } else {
      syntaxError("CALL", sync)
    }
  }
  
  private def optionalExpressionList(): Unit = {
    val sync = (Set[Token](SEMICOLON, END, ELSE), Set.empty[TokenMatcher])
    
    if (isCurrentToken(PAREN_OPEN)) {
      matchToken(PAREN_OPEN, sync)
      expressionList()
      matchToken(PAREN_CLOSE, sync)
    } else if (isCurrentToken(SEMICOLON)) {
      Unit
    } else {
      syntaxError("'(', ';'", sync)
    }
  }
  
  private def expressionList(): Unit = {
    val sync = (Set[Token](PAREN_CLOSE), Set.empty[TokenMatcher])
    
    if (isCurrentToken(ID) || isCurrentToken(NUM) 
        || isCurrentToken(PAREN_OPEN, PLUS, MINUS, NOT)) {
      
      expression()
      expressionListTail()
    } else {
      syntaxError("ID, NUM, '(', '+', '-', NOT", sync)
    }
  }
  
  private def expressionListTail(): Unit = {
    val sync = (Set[Token](PAREN_CLOSE), Set.empty[TokenMatcher])
    
    if (isCurrentToken(PAREN_CLOSE)) {
      Unit
    } else if (isCurrentToken(COMMA)) {
      matchToken(COMMA, sync)
      expression()
      expressionListTail()
    } else {
      syntaxError("')', ','", sync)
    }
  }
  
  private def expression(): Unit = {
    val sync = (Set[Token](SEMICOLON, END, ELSE, THEN, DO, SQUAREBRACKET_CLOSE,
        PAREN_CLOSE, COMMA),
      Set.empty[TokenMatcher])
    
    if (isCurrentToken(ID) || isCurrentToken(NUM) 
        || isCurrentToken(PAREN_OPEN, PLUS, MINUS, NOT)) {
      
      simpleExpression()
      optionalRelop()
    } else {
      syntaxError("ID, NUM, '(', '+', '-', NOT", sync)
    }
  }
  
  private def optionalRelop(): Unit = {
    val sync = (Set[Token](SEMICOLON, END, ELSE, THEN, DO, SQUAREBRACKET_CLOSE,
        PAREN_CLOSE, COMMA),
      Set.empty[TokenMatcher])
    
    if (isCurrentToken(PAREN_CLOSE, SQUAREBRACKET_CLOSE, COMMA, SEMICOLON,
        THEN, ELSE, DO, END)) {
      
      Unit
    } else if (isCurrentToken(RELOP)) {
      matchToken(RELOP, sync)
      simpleExpression()
    } else {
      syntaxError("')', ']', ',', ';', THEN, ELSE, DO, END, RELOP", sync)
    }
  }
  
  private def simpleExpression(): Unit = {
    val sync = (Set[Token](SEMICOLON, END, ELSE, THEN, DO, SQUAREBRACKET_CLOSE,
        PAREN_CLOSE, COMMA),
      Set[TokenMatcher](RELOP))
    
    if (isCurrentToken(ID) || isCurrentToken(NUM) 
        || isCurrentToken(PAREN_OPEN, NOT)) {
      
      term()
      optionalAddop()
    } else if (isCurrentToken(PLUS, MINUS)) {
      sign()
      term()
      optionalAddop()
    } else {
      syntaxError("ID, NUM, '(', '+', '-', NOT", sync)
    }
  }
  
  private def optionalAddop(): Unit = {
    val sync = (Set[Token](SEMICOLON, END, ELSE, THEN, DO, SQUAREBRACKET_CLOSE,
        PAREN_CLOSE, COMMA),
      Set[TokenMatcher](RELOP))
    
    if (isCurrentToken(PAREN_CLOSE, SQUAREBRACKET_CLOSE, COMMA, SEMICOLON,
        THEN, ELSE, DO, END) || isCurrentToken(RELOP)) {
      
      Unit
    } else if (isCurrentToken(ADDOP)) {
      matchToken(ADDOP, sync)
      term()
      optionalAddop()
    } else {
      syntaxError("')', ']', ',', ';', THEN, ELSE, DO, END, RELOP, ADDOP", sync)
    }
  }
  
  private def term(): Unit = {
    val sync = (Set[Token](SEMICOLON, END, ELSE, THEN, DO, SQUAREBRACKET_CLOSE,
        PAREN_CLOSE, COMMA),
      Set[TokenMatcher](RELOP, ADDOP))
    
    if (isCurrentToken(ID) || isCurrentToken(NUM) 
        || isCurrentToken(PAREN_OPEN, NOT)) {
      
      factor()
      optionalMulop()
    } else {
      syntaxError("ID, NUM, '(', NOT", sync)
    }
  }
  
  private def optionalMulop(): Unit = {
    val sync = (Set[Token](SEMICOLON, END, ELSE, THEN, DO, SQUAREBRACKET_CLOSE,
        PAREN_CLOSE, COMMA),
      Set[TokenMatcher](RELOP, ADDOP))
    
    if (isCurrentToken(PAREN_CLOSE, SQUAREBRACKET_CLOSE, COMMA, SEMICOLON,
        THEN, ELSE, DO, END)
        || isCurrentToken(RELOP) || isCurrentToken(ADDOP)) {
      
      Unit
    } else if (isCurrentToken(MULOP)) {
      matchToken(MULOP, sync)
      factor()
      optionalMulop()
    } else {
      syntaxError(
          "')', ']', ',', ';', THEN, ELSE, DO, END, RELOP, ADDOP, MULOP", sync)
    }
  }
  
  private def factor(): Unit = {
    val sync = (Set[Token](SEMICOLON, END, ELSE, THEN, DO, SQUAREBRACKET_CLOSE,
        PAREN_CLOSE, COMMA),
      Set[TokenMatcher](RELOP, ADDOP, MULOP))
    
    if (isCurrentToken(ID)) {
      matchToken(ID, sync)
      arrayExpression()
    } else if (isCurrentToken(NUM)) {
      matchToken(NUM, sync)
    } else if (isCurrentToken(PAREN_OPEN)) {
      matchToken(PAREN_OPEN, sync)
      expression()
      matchToken(PAREN_CLOSE, sync)
    } else if (isCurrentToken(NOT)) {
      matchToken(NOT, sync)
      factor()
    } else {
      syntaxError("ID, NUM, '(', NOT", sync)
    }
  }
  
  private def arrayExpression(): Unit = {
    val sync = (Set[Token](SEMICOLON, END, ELSE, THEN, DO, SQUAREBRACKET_CLOSE,
        PAREN_CLOSE, COMMA),
      Set[TokenMatcher](RELOP, ADDOP, MULOP))
    
    if (isCurrentToken(PAREN_CLOSE, SQUAREBRACKET_CLOSE, COMMA, SEMICOLON,
        THEN, ELSE, DO, END)
        || isCurrentToken(RELOP) || isCurrentToken(ADDOP)
        || isCurrentToken(MULOP)) {
      Unit
    } else if (isCurrentToken(SQUAREBRACKET_OPEN)) {
      matchToken(SQUAREBRACKET_OPEN, sync)
      expression()
      matchToken(SQUAREBRACKET_CLOSE, sync)
    } else {
      syntaxError(
          "')', '[', ']', ',', ';', THEN, ELSE, DO, END, RELOP, ADDOP, MULOP",
          sync)
    }
  }
  
  private def sign(): Unit = {
    val sync = (Set[Token](PAREN_OPEN, NOT), Set[TokenMatcher](ID, NUM))
    
    if (isCurrentToken(PLUS)) {
      matchToken(PLUS, sync)
    } else if (isCurrentToken(MINUS)) {
      matchToken(MINUS, sync)
    } else {
      syntaxError("'+', '-'", sync)
    }
  }
  
}
