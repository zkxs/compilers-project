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
    listingPrinter: ListingPrinter,
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
  
  private def nextToken(): Unit = {
    currentToken = tokenIterator.next()
  }
		  
  def parse() = {
    nextToken()
    program()
    matchToken(EOF, (Set.empty[Token], Set.empty[TokenMatcher]))
    listingPrinter.finishPrinting()
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
  
  private def gobbleTokens(sync: SyncSet): Unit = {
    while (!isCurrentTokenInSync(sync)) {
      nextToken()
      currentToken match {
        case et: ErrorToken => listingPrinter.printError(et)
        case _ =>
      }
    }
  }
  
  /**
   * Perform generic error reporting and recovery
   * @param message Error message
   * @param sync1 Sync set
   * @param sync2 Extra sync set of TokenMatchers
   */
  private def error(errorType: String, message: String, sync: SyncSet): Unit = {
    printError(currentToken, errorType, message)
    gobbleTokens(sync)
  }
  
  /**
   * Perform generic error reporting and recovery
   * @param message Error message
   * @param sync Sync set
   */
  private def error(errorType: String, message: String, sync: Set[Token]): Unit = {
    error(errorType, message, (sync, Set.empty[TokenMatcher]))
  }
  
  private def syntaxError(expectedTokens: String, sync: SyncSet): Unit = {
    
    val lexeme = currentToken match {
      case at: AttributeToken  => at.lexeme
      case id: IdentifierToken => id.lexeme
    }
    
    currentToken match {
      case et: ErrorToken => {
        error("LEXERR", et.errorString(), sync)
      }
      case _ => {
        error("SYNERR",
            s"expected one of: $expectedTokens but got $q${lexeme.lexeme}$q",
            sync)
      }
    }
    
    
  }
  
  private def printError(token: Token, errorType: String, message: String): Unit = {
    val lexeme = token match {
      case at: AttributeToken  => at.lexeme
      case id: IdentifierToken => id.lexeme
    }
    val line = lexeme.location.lineNumber
    val space = " " * (lexeme.location.columnOffset + 7)
    
    listingPrinter.printError(line, space + "^ " + errorType + ": " + message)
  }
  
  private def printSemanticError(token: Token, message: String): Unit = { 
    printError(token, "SEMERR", message)
  }
  
  private def semanticError(
      token: Token, message: String, sync: SyncSet): Unit = {
    printSemanticError(token, message)
    gobbleTokens(sync)
  }
  
  private def semanticErrorDeferred(
      token: Token, message: String, sync: SyncSet): () => Unit = {
        
    printSemanticError(token, message)
    () => gobbleTokens(sync)
  }
  
  private def semanticErrorDeferred(
      token: Token,
      err: Option[IdentifierError],
      sync: SyncSet): Option[() => Unit] = {
    
    err.fold[Option[() => Unit]](None)(e => {
      Some(semanticErrorDeferred(token, e.message, sync))
    })
  }
  
  /**
   * Optionally process an IdentifierError
   * @return true if there was no error
   */
  private def semanticError(
      token: Token, err: Option[IdentifierError], sync: SyncSet): Unit = {
    err.map(e => semanticError(token, e.message, sync))
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
  
  /**
   * Return false if assertion was bad
   */
  private def assertEquals(
      token: Token,
      a: Option[Any],
      b: Option[Any],
      msg: String,
      sync: SyncSet): Boolean = {
    if (a.isEmpty || b.isEmpty) {
      // we've already complained about this. Do nothing.
      true
    } else if (a != b) {
      semanticError(token, msg, sync)
      false
    } else {
      true
    }
  }
  
  private val numericTypes = List(T_Integer(), T_Real())
  
  /**
   * Return false if assertion was bad
   */
  private def assertNumeric(
      token: Token,
      someType: Option[Type],
      msg: String,
      sync: SyncSet): Boolean = {
    if (someType.isEmpty) {
      // we've already complained about this. Do nothing.
      true
    } else if (!numericTypes.contains(someType.get)) {
      semanticError(token, msg, sync)
      false
    } else {
      true
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
      val idToken = matchToken(ID, sync)
      val optId = extractId(idToken)
      matchToken(PAREN_OPEN, sync)
      val optParams = identifierList()
      
      val deferred = if (exists(optId, optParams)) {
        semanticErrorDeferred(idToken.get,
            addProgram(optId.get, optParams.get), sync)
      } else {
        None
      }
      
      matchToken(PAREN_CLOSE, sync)
      matchToken(SEMICOLON, sync)
      programPrime()
      
      if (deferred.isDefined) {
        deferred.get() // perform deferred gobbling
        semanticError(idToken.get, pop(), sync)
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
  
  private def identifierList(): Option[List[TypedIdentifier]] = {
    val sync = (Set[Token](PAREN_CLOSE), Set.empty[TokenMatcher])
    
    if (isCurrentToken(ID)) {
      val optId = extractId(matchToken(ID, sync))
      val optList = identifierListTail()
      
      if (exists(optId, optList)) {
        Some(TypedIdentifier(optId.get.name, T_ProgramParam()) +: optList.get)
      } else {
        None
      }
    } else {
      syntaxError("ID", sync)
      None
    }
  }
  
  private def identifierListTail(): Option[List[TypedIdentifier]] = {
    val sync = (Set[Token](PAREN_CLOSE), Set.empty[TokenMatcher])
    
    if (isCurrentToken(PAREN_CLOSE)) {
      Some(List.empty)
    } else if (isCurrentToken(COMMA)) {
      matchToken(COMMA, sync)
      val optId = extractId(matchToken(ID, sync))
      val optList = identifierListTail()
      
      if (exists(optId, optList)) {
        Some(TypedIdentifier(optId.get.name, T_ProgramParam()) +: optList.get)
      } else {
        None
      }
    } else {
      syntaxError("')', ','", sync)
      None
    }
  }
  
  private def declarations(): Unit = {
    val sync = (Set[Token](PROCEDURE, BEGIN), Set.empty[TokenMatcher])
    
    if (isCurrentToken(VAR)) {
      matchToken(VAR, sync)
      val idToken = matchToken(ID, sync)
      val optId = extractId(idToken)
      matchToken(COLON, sync)
      val optType = anyType()
      matchToken(SEMICOLON, sync)
      
      if (exists(optId, optType)) {
        semanticError(idToken.get, addVariable(optId.get, optType.get), sync)
      }
      
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
        case Some(token: Token) => {
          semanticError(token, "array bound must be an integer", sync);
          None
        }
        case _ => None
      }
      matchToken(ARRAYRANGE, sync)
      val optNum2 = matchToken(NUM, sync) match {
        case Some(it: IntegerToken) => Some(it.value)
        case Some(token:Token) => {
          semanticError(token, "array bound must be an integer", sync);
          None
        }
        case _ => None
      }
      matchToken(SQUAREBRACKET_CLOSE, sync)
      matchToken(OF, sync)
      
      val savedToken = currentToken
      val optType = standardType() match {
        case Some(t: ArrayableType) => Some(t)
        case _ => {
          semanticError(savedToken,
              "arrays can only contain integers or reals", sync)
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
      val savedToken = currentToken
      val success = subprogramHead()
      subprogramDeclarationPrime()
      
      if (success) {
        semanticError(savedToken, pop(), sync)
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
      val idToken = matchToken(ID, sync)
      val optId = extractId(idToken)
      val optParams = subprogramHeadPrime()
      
      val deferred = if (exists(optId, optParams)) {
        semanticErrorDeferred(
            idToken.get, addProcedure(optId.get, optParams.get), sync)
      } else {
        None
      }
      
      deferred.map(f => f())
      deferred.isEmpty
      
    } else {
      syntaxError("PROCEDURE", sync)
      false
    }
  }
  
  private def subprogramHeadPrime(): Option[List[TypedIdentifier]] = {
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
  
  private def arguments(): Option[List[TypedIdentifier]] = {
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
  
  private def parameterList(): Option[List[TypedIdentifier]] = {
    val sync = (Set[Token](PAREN_CLOSE), Set.empty[TokenMatcher])
    
    if (isCurrentToken(ID)) {
      val optId = extractId(matchToken(ID, sync))
      matchToken(COLON, sync)
      val optType = anyType()
      val optParams = parameterListTail()
      
      if (exists(optId, optType, optParams)) {
        Some(TypedIdentifier(optId.get.name, optType.get) +: optParams.get)
      } else {
        None // indicative of errors we've already complained about
      }
    } else {
      syntaxError("ID", sync)
      None
    }
  }
  
  private def parameterListTail(): Option[List[TypedIdentifier]] = {
    val sync = (Set[Token](PAREN_CLOSE), Set.empty[TokenMatcher])
    
    if (isCurrentToken(PAREN_CLOSE)) {
      Some(List.empty)
    } else if (isCurrentToken(SEMICOLON)) {
      matchToken(SEMICOLON, sync)
      val optId = extractId(matchToken(ID, sync))
      matchToken(COLON, sync)
      val optType = anyType()
      val optParams = parameterListTail()
      
      if (exists(optId, optType, optParams)) {
        Some(TypedIdentifier(optId.get.name, optType.get) +: optParams.get)
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
      val varType = variable()
      val assignOp = matchToken(ASSIGNOP, sync)
      val exprType = expression()
      
      assignOp.map(tok => assertEquals(
          tok, varType, exprType, "types must match in an assignment", sync))
      
    } else if (isCurrentToken(IF)) {
      matchToken(IF, sync)
      val exprStart = currentToken
      val exprType = expression()
      matchToken(THEN, sync)
      statement()
      optionalElse()
      
      assertEquals(exprStart, exprType, Some(T_Boolean()),
          "IF conditons must be boolean expressions", sync)
    } else if (isCurrentToken(WHILE)) {
      matchToken(WHILE, sync)
      val exprStart = currentToken
      val exprType = expression()
      matchToken(DO, sync)
      statement()
      
      assertEquals(exprStart, exprType, Some(T_Boolean()),
          "WHILE conditons must be boolean expressions", sync)
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
      val idToken = matchToken(ID, sync)
      val optId = extractId(idToken)
      val isArrayVar = arrayVariable()
      
      if (exists(optId, isArrayVar)) {
        
        getVariable(optId.get).fold( error => { // if failure
          semanticError(idToken.get, error.message, sync)
          None
        }, id => { // if success
          if (isArrayVar.get) {
            // it was an array assignment
            id.idType match {
              case T_Array(_, innerType) => Some(innerType)
              case _ => {
                // this array assignment was not to an array!
                semanticError(idToken.get,
                    s"array assignment to non-array $q$id$q", sync)
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
      val exprStart = currentToken
      val deferred = expression() match {
        case Some(T_Integer()) => None
        case _ => Some(semanticErrorDeferred(
            exprStart, "array indices must be integers", sync))
      }
      matchToken(SQUAREBRACKET_CLOSE, sync)
      
      deferred.map(f => f())
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
      val idToken = matchToken(ID, sync)
      val optId = extractId(idToken)
      val optParams = optionalExpressionList()
      
      if (exists(optId, optParams)) {
        semanticError(idToken.get, checkCall(optId.get, optParams.get), sync)
      }
    } else {
      syntaxError("CALL", sync)
    }
  }
  
  private def optionalExpressionList(): Option[List[Type]] = {
    val sync = (Set[Token](SEMICOLON, END, ELSE), Set.empty[TokenMatcher])
    
    if (isCurrentToken(PAREN_OPEN)) {
      matchToken(PAREN_OPEN, sync)
      val optList = expressionList()
      matchToken(PAREN_CLOSE, sync)
      optList
    } else if (isCurrentToken(SEMICOLON)) {
      Some(List.empty)
    } else {
      syntaxError("'(', ';'", sync)
      None
    }
  }
  
  private def expressionList(): Option[List[Type]] = {
    val sync = (Set[Token](PAREN_CLOSE), Set.empty[TokenMatcher])
    
    if (isCurrentToken(ID) || isCurrentToken(NUM) 
        || isCurrentToken(PAREN_OPEN, PLUS, MINUS, NOT)) {
      
      val optType = expression()
      val optList = expressionListTail()
      
      if (exists(optType, optList)) {
        Some(optType.get +: optList.get)
      } else {
        None // indicative of errors we've already complained about
      }
    } else {
      syntaxError("ID, NUM, '(', '+', '-', NOT", sync)
      None
    }
  }
  
  private def expressionListTail(): Option[List[Type]] = {
    val sync = (Set[Token](PAREN_CLOSE), Set.empty[TokenMatcher])
    
    if (isCurrentToken(PAREN_CLOSE)) {
      Some(List.empty)
    } else if (isCurrentToken(COMMA)) {
      matchToken(COMMA, sync)
      val optType = expression()
      val optList = expressionListTail()
      
      if (exists(optType, optList)) {
        Some(optType.get +: optList.get)
      } else {
        None // indicative of errors we've already complained about
      }
    } else {
      syntaxError("')', ','", sync)
      None
    }
  }
  
  private def expression(): Option[Type] = {
    val sync = (Set[Token](SEMICOLON, END, ELSE, THEN, DO, SQUAREBRACKET_CLOSE,
        PAREN_CLOSE, COMMA),
      Set.empty[TokenMatcher])
    
    if (isCurrentToken(ID) || isCurrentToken(NUM) 
        || isCurrentToken(PAREN_OPEN, PLUS, MINUS, NOT)) {
      
      val simpType = simpleExpression()
      optionalRelop(simpType) // return this as sType
    } else {
      syntaxError("ID, NUM, '(', '+', '-', NOT", sync)
      None
    }
  }
  
  private def optionalRelop(iType: Option[Type]): Option[Type] = {
    val sync = (Set[Token](SEMICOLON, END, ELSE, THEN, DO, SQUAREBRACKET_CLOSE,
        PAREN_CLOSE, COMMA),
      Set.empty[TokenMatcher])
    
    if (isCurrentToken(PAREN_CLOSE, SQUAREBRACKET_CLOSE, COMMA, SEMICOLON,
        THEN, ELSE, DO, END)) {
      
      iType
    } else if (isCurrentToken(RELOP)) {
      val optRelop = matchToken(RELOP, sync)
      val exprType = simpleExpression()
      
      optRelop match {
        case Some(relop: AttributeToken) => {
          if (assertEquals(relop, exprType, iType,
              s"cannot RELOP differing types: $iType, $exprType", sync)) {
            val opType = relop.attribute.get
            if (opType == "EQUALS" || opType == "NOTEQUALS") {
              Some(T_Boolean())
            } else {
              if (assertNumeric(relop, exprType,
                  "cannot compare non-numeric types", sync)) {
                Some(T_Boolean())
              } else {
                None
              }
            }
          } else {
            None
          }
        }
        case _ => None
      }
    } else {
      syntaxError("')', ']', ',', ';', THEN, ELSE, DO, END, RELOP", sync)
      None
    }
  }
  
  private def simpleExpression(): Option[Type] = {
    val sync = (Set[Token](SEMICOLON, END, ELSE, THEN, DO, SQUAREBRACKET_CLOSE,
        PAREN_CLOSE, COMMA),
      Set[TokenMatcher](RELOP))
    
    if (isCurrentToken(ID) || isCurrentToken(NUM) 
        || isCurrentToken(PAREN_OPEN, NOT)) {
      
      val termType = term()
      optionalAddop(termType) // return this
    } else if (isCurrentToken(PLUS, MINUS)) {
      sign()
      val termType = term()
      optionalAddop(termType) // return this
    } else {
      syntaxError("ID, NUM, '(', '+', '-', NOT", sync)
      None
    }
  }
  
  private def optionalAddop(iType: Option[Type]): Option[Type] = {
    val sync = (Set[Token](SEMICOLON, END, ELSE, THEN, DO, SQUAREBRACKET_CLOSE,
        PAREN_CLOSE, COMMA),
      Set[TokenMatcher](RELOP))
    
    if (isCurrentToken(PAREN_CLOSE, SQUAREBRACKET_CLOSE, COMMA, SEMICOLON,
        THEN, ELSE, DO, END) || isCurrentToken(RELOP)) {
      
      iType
    } else if (isCurrentToken(ADDOP)) {
      val optAddop = matchToken(ADDOP, sync)
      val sType = term()
      optionalAddop(sType)
      
      
      optAddop match {
        case Some(addop: AttributeToken) => {
          if (assertEquals(addop, sType, iType,
              s"cannot ADDOP differing types: $iType, $sType", sync)) {
            if (addop.attribute.get == "OR") {
              if (sType.get != T_Boolean()) {
                semanticError(addop, "cannot OR non-booleans", sync)
              }
              Some(T_Boolean())
            } else {
              if (assertNumeric(addop, sType,
                  "cannot add non-numeric types", sync)) {
                sType
              } else {
                None
              }
            }
          } else {
            None
          }
        }
        case _ => None
      }
    } else {
      syntaxError("')', ']', ',', ';', THEN, ELSE, DO, END, RELOP, ADDOP", sync)
      None
    }
  }
  
  private def term(): Option[Type] = {
    val sync = (Set[Token](SEMICOLON, END, ELSE, THEN, DO, SQUAREBRACKET_CLOSE,
        PAREN_CLOSE, COMMA),
      Set[TokenMatcher](RELOP, ADDOP))
    
    if (isCurrentToken(ID) || isCurrentToken(NUM) 
        || isCurrentToken(PAREN_OPEN, NOT)) {
      
      val factorType = factor()
      optionalMulop(factorType) // return this as sType
    } else {
      syntaxError("ID, NUM, '(', NOT", sync)
      None
    }
  }
  
  private def optionalMulop(iType: Option[Type]): Option[Type] = {
    val sync = (Set[Token](SEMICOLON, END, ELSE, THEN, DO, SQUAREBRACKET_CLOSE,
        PAREN_CLOSE, COMMA),
      Set[TokenMatcher](RELOP, ADDOP))
    
    if (isCurrentToken(PAREN_CLOSE, SQUAREBRACKET_CLOSE, COMMA, SEMICOLON,
        THEN, ELSE, DO, END)
        || isCurrentToken(RELOP) || isCurrentToken(ADDOP)) {
      
      iType
    } else if (isCurrentToken(MULOP)) {
      val optMulop = matchToken(MULOP, sync)
      val sType = factor()
      optionalMulop(sType)
      
      optMulop match {
        case Some(mulop: AttributeToken) => {
          if (assertEquals(mulop, sType, iType,
              s"cannot MULOP differning types: $iType, $sType", sync)) {
            if (mulop.attribute.get == "AND") {
              if (sType.get != T_Boolean()) {
                semanticError(mulop, "cannot AND non-booleans", sync)
              }
              Some(T_Boolean())
            } else {
              if (assertNumeric(mulop, sType,
                  s"cannot multiply ${iType.fold("")(_.toString())}", sync)) {
                sType
              } else {
                None
              }
            }
          } else {
            None
          }
        }
        case _ => None
      }
    } else {
      syntaxError(
          "')', ']', ',', ';', THEN, ELSE, DO, END, RELOP, ADDOP, MULOP", sync)
      None
    }
  }
  
  private def factor(): Option[Type] = {
    val sync = (Set[Token](SEMICOLON, END, ELSE, THEN, DO, SQUAREBRACKET_CLOSE,
        PAREN_CLOSE, COMMA),
      Set[TokenMatcher](RELOP, ADDOP, MULOP))
    
    if (isCurrentToken(ID)) {
      val idToken = matchToken(ID, sync)
      val optId = extractId(idToken)
      val isArray = arrayExpression()
      
      if (exists(optId, isArray)) {
        getVariable(optId.get).fold(error => {
          semanticError(idToken.get, error.message, sync)
          None
        }, id => {
          if (isArray.get) {
            id.idType match {
              case T_Array(_, innerType) => Some(innerType)
              case _ => {
                semanticError(
                    idToken.get, s"array access to non-array $q$id$q", sync)
                None
              }
            }
          } else {
            Some(id.idType)
          }
        })
      } else {
        None
      }
      
    } else if (isCurrentToken(NUM)) {
      matchToken(NUM, sync) match {
        case Some(t: IntegerToken) => Some(T_Integer())
        case Some(t: AttributeToken) => Some(T_Real())
        case _ => throw new AssertionError(
            "NUM matcher somehow matched a non-numeric")
      }
    } else if (isCurrentToken(PAREN_OPEN)) {
      matchToken(PAREN_OPEN, sync)
      val sType = expression()
      matchToken(PAREN_CLOSE, sync)
      sType
    } else if (isCurrentToken(NOT)) {
      matchToken(NOT, sync)
      val factorToken = currentToken
      val optType = factor()
      if (optType.isDefined && optType.get != T_Boolean()) {
        semanticError(factorToken, "NOT can only be applied to booleans", sync)
      }
      Some(T_Boolean()) // purposely always return boolean for error recovery
    } else {
      syntaxError("ID, NUM, '(', NOT", sync)
      None
    }
  }
  
  // return true if arrayExpression
  private def arrayExpression(): Option[Boolean] = {
    val sync = (Set[Token](SEMICOLON, END, ELSE, THEN, DO, SQUAREBRACKET_CLOSE,
        PAREN_CLOSE, COMMA),
      Set[TokenMatcher](RELOP, ADDOP, MULOP))
    
    if (isCurrentToken(PAREN_CLOSE, SQUAREBRACKET_CLOSE, COMMA, SEMICOLON,
        THEN, ELSE, DO, END)
        || isCurrentToken(RELOP) || isCurrentToken(ADDOP)
        || isCurrentToken(MULOP)) {
      Some(false)
    } else if (isCurrentToken(SQUAREBRACKET_OPEN)) {
      matchToken(SQUAREBRACKET_OPEN, sync)
      val exprToken = currentToken
      val deferred = expression() match {
        case Some(T_Integer()) => None
        case _ => Some(semanticErrorDeferred(
            exprToken, "array indices must be integers", sync))
      }
      matchToken(SQUAREBRACKET_CLOSE, sync)
      
      deferred.map(f => f())
      Some(true)
    } else {
      syntaxError(
          "')', '[', ']', ',', ';', THEN, ELSE, DO, END, RELOP, ADDOP, MULOP",
          sync)
      None
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
