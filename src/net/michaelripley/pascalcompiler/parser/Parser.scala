package net.michaelripley.pascalcompiler.parser

import java.io.PrintWriter
import net.michaelripley.pascalcompiler.tokens._

class Parser(tokens: List[Token], listWriter: PrintWriter) {
  
  // easy tokens to match
  private val PROGRAM = new AttributeToken("PROGRAM")
  private val PROCEDURE = new AttributeToken("PROCEDURE")
  private val BEGIN = new AttributeToken("BEGIN")
  private val CALL = new AttributeToken("CALL")
  private val VAR = new AttributeToken("VAR")
  private val INTEGER = new AttributeToken("INTEGER")
  private val REAL = new AttributeToken("REAL")
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
  private val EOF = new Token("EOF")
  
  // harder tokens to match (they can be different literals)
  
  private type TokenMatcher = (Token => (Boolean, String))
  
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
		  
  private def parse() = {
    nextToken()
    program()
    matchToken(EOF)
  }
  
  private def matchToken(m: TokenMatcher): Unit = {
    val (matched, name) = m(currentToken)
    if (matched) {
      if (currentToken != EOF) {
        nextToken()
      } else {
        /* Typically, you would exit the parser after reading an expected EOF,
         * however the only case in which this happens in this program is at
         * the end of parse(), so simply returning is acceptable behavior.
         */
      }
    } else {
      syntaxError(name, Set.empty) //TODO: pass parent sync set to here
    }
  }
  
  private def matchToken(t: Token): Unit = {
    matchToken(t => (t == currentToken, 
      t match {
        case at: AttributeToken => {
          at.attribute match {
            case Some(attr) => s"${at.tokenName}_$attr"
            case _ => at.tokenName
          }
        }
        case _ => t.tokenName
      }
    ))
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
  
  /**
   * Perform generic error reporting and recovery
   * @param message Error message
   * @param sync1 Sync set
   * @param sync2 Extra sync set of TokenMatchers
   */
  private def error(message: String,
      sync1: Set[Token], sync2: Set[TokenMatcher]): Unit = {
    
    listWriter.println(message)
    
    //TODO: gobble tokens not in sync1, sync2, or EOF
  }
  
  /**
   * Perform generic error reporting and recovery
   * @param message Error message
   * @param sync Sync set
   */
  private def error(message: String, sync: Set[Token]): Unit = {
    error(message, sync, Set.empty[TokenMatcher])
  }
  
  private def syntaxError(expectedTokens: String,
      sync1: Set[Token], sync2: Set[TokenMatcher]): Unit = {
    
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
        s"""^ SYNERR: expected one of: $expectedTokens but got "${lexeme.lexeme}""""
      }
    }
    
    error(space + errorString, sync1, sync2)
  }
  
  private def syntaxError(expectedTokens: String, sync: Set[Token]): Unit = {
    syntaxError(expectedTokens, sync, Set.empty[TokenMatcher])
  }
  
  /* *************************************************************************
   *      BEGINNING OF RECURSIVE DESCENT PARSER COOKIE-CUTTER FUNCTIONS
   * *************************************************************************/
  
  private def program(): Unit = {
    if (isCurrentToken(PROGRAM)) {
      matchToken(PROGRAM)
      matchToken(ID)
      matchToken(PAREN_OPEN)
      identifierList()
      matchToken(PAREN_CLOSE)
      matchToken(SEMICOLON)
      programPrime()
    } else {
      syntaxError("PROGRAM", Set(PROGRAM))
    }
  }
  
  private def programPrime(): Unit = {
    if (isCurrentToken(PROCEDURE)) {
      subprogramDeclarations()
      compoundStatement()
      matchToken(FULLSTOP)
    } else if (isCurrentToken(BEGIN)) {
      compoundStatement()
      matchToken(FULLSTOP)
    } else if (isCurrentToken(VAR)) {
      declarations()
      programPrime()
    } else {
      syntaxError("PROCEDURE, BEGIN, VAR", Set(PROCEDURE, BEGIN, VAR))
    }
  }
  
  private def identifierList(): Unit = {
    if (isCurrentToken(ID)) {
      matchToken(ID)
      identifierListTail()
    } else {
      syntaxError("ID", Set.empty, Set(ID))
    }
  }
  
  private def identifierListTail(): Unit = {
    if (isCurrentToken(PAREN_CLOSE)) {
      Unit
    } else if (isCurrentToken(COMMA)) {
      matchToken(COMMA)
      matchToken(ID)
      identifierListTail()
    } else {
      syntaxError("')', ','", Set(PAREN_CLOSE, COMMA))
    }
  }
  
  private def declarations(): Unit = {
    if (isCurrentToken(VAR)) {
      matchToken(VAR)
      matchToken(ID)
      matchToken(COLON)
      anyType()
      matchToken(SEMICOLON)
      optionalDeclarations()
    } else {
      syntaxError("VAR", Set(VAR))
    }
  }
  
  private def optionalDeclarations(): Unit = {
    if (isCurrentToken(PROCEDURE, BEGIN)) {
      Unit
    } else if (isCurrentToken(VAR)) {
      declarations()
    } else {
      syntaxError("PROCEDURE, BEGIN, VAR", Set(PROCEDURE, BEGIN, VAR))
    }
  }
  
  // could not name this function type, as type is a reserved word in Scala
  private def anyType(): Unit = {
    if (isCurrentToken(INTEGER, REAL)) {
      standardType()
    } else if (isCurrentToken(ARRAY)) {
      matchToken(ARRAY)
      matchToken(SQUAREBRACKET_OPEN)
      matchToken(NUM)
      matchToken(ARRAYRANGE)
      matchToken(NUM)
      matchToken(SQUAREBRACKET_CLOSE)
      matchToken(OF)
      standardType()
    } else {
      syntaxError("INTEGER, REAL, ARRAY", Set(INTEGER, REAL, ARRAY))
    }
  }
  
  private def standardType(): Unit = {
    if (isCurrentToken(INTEGER)) {
      matchToken(INTEGER)
    } else if (isCurrentToken(REAL)) {
      matchToken(REAL)
    } else {
       syntaxError("INTEGER, REAL", Set(INTEGER, REAL))
    }
  }
  
  private def subprogramDeclarations(): Unit = {
    if (isCurrentToken(PROCEDURE)) {
      subprogramDeclaration()
      matchToken(SEMICOLON)
      optionalSubprogramDeclarations()
    } else {
      syntaxError("PROCEDURE, ';'", Set(PROCEDURE, SEMICOLON))
    }
  }
  
  private def optionalSubprogramDeclarations(): Unit = {
    if (isCurrentToken(PROCEDURE)) {
      subprogramDeclarations()
    } else if (isCurrentToken(BEGIN)) {
      Unit
    } else {
      syntaxError("PROCEDURE, BEGIN", Set(PROCEDURE, BEGIN))
    }
  }
  
  private def subprogramDeclaration(): Unit = {
    if (isCurrentToken(PROCEDURE)) {
      subprogramHead()
      subprogramDeclarationPrime()
    } else {
      syntaxError("PROCEDURE", Set(PROCEDURE))
    }
  }
  
  private def subprogramDeclarationPrime(): Unit = {
    if (isCurrentToken(PROCEDURE)) {
      subprogramDeclarations()
      compoundStatement()
    } else if (isCurrentToken(BEGIN)) {
      compoundStatement()
    } else if (isCurrentToken(VAR)) {
      declarations()
      subprogramDeclarationPrime()
    } else {
      syntaxError("PROCEDURE, BEGIN, VAR", Set(PROCEDURE, BEGIN, VAR))
    }
  }
  
  private def subprogramHead(): Unit = {
    if (isCurrentToken(PROCEDURE)) {
      matchToken(PROCEDURE)
      matchToken(ID)
      subprogramHeadPrime()
    } else {
      syntaxError("PROCEDURE", Set(PROCEDURE))
    }
  }
  
  private def subprogramHeadPrime(): Unit = {
    if (isCurrentToken(PAREN_OPEN)) {
      arguments()
      matchToken(SEMICOLON)
    } else if (isCurrentToken(SEMICOLON)) {
      matchToken(SEMICOLON)
    } else {
      syntaxError("'(', ';'", Set(PAREN_OPEN, SEMICOLON))
    }
  }
  
  private def arguments(): Unit = {
    if (isCurrentToken(PAREN_OPEN)) {
      matchToken(PAREN_OPEN)
      parameterList()
      matchToken(PAREN_CLOSE)
    } else {
      syntaxError("'('", Set(PAREN_OPEN))
    }
  }
  
  private def parameterList(): Unit = {
    if (isCurrentToken(ID)) {
      matchToken(ID)
      matchToken(COLON)
      anyType()
      parameterListTail()
    } else {
      syntaxError("ID", Set.empty, Set(ID))
    }
  }
  
  private def parameterListTail(): Unit = {
    if (isCurrentToken(PAREN_CLOSE)) {
      Unit
    } else if (isCurrentToken(SEMICOLON)) {
      matchToken(SEMICOLON)
      matchToken(ID)
      matchToken(COLON)
      anyType()
      parameterListTail()
    } else {
      syntaxError("')', ';'", Set(PAREN_CLOSE, SEMICOLON))
    }
  }
  
  private def compoundStatement(): Unit = {
    if (isCurrentToken(BEGIN)) {
      matchToken(BEGIN)
      compoundStatmentTail()
    } else {
      syntaxError("BEGIN", Set(BEGIN))
    }
  }
  
  private def compoundStatmentTail(): Unit = {
    if (isCurrentToken(BEGIN, CALL, IF, WHILE) || isCurrentToken(ID)) {
      statementList()
      matchToken(END)
    } else if (isCurrentToken(END)) {
      matchToken(END)
    } else {
      syntaxError("BEGIN, CALL, ID, IF, WHILE, END",
          Set(BEGIN, CALL, IF, WHILE, END), Set(ID))
    }
  }
  
  private def statementList(): Unit = {
    if (isCurrentToken(BEGIN, CALL, IF, WHILE) || isCurrentToken(ID)) {
      statement()
      statementListTail()
    } else {
      syntaxError("BEGIN, CALL, ID, IF, WHILE",
          Set(BEGIN, CALL, IF, WHILE), Set(ID))
    }
  }
  
  private def statementListTail(): Unit = {
    if (isCurrentToken(SEMICOLON)) {
      matchToken(SEMICOLON)
      statement()
      statementListTail()
    } else if (isCurrentToken(END)) {
      Unit
    } else {
      syntaxError("';', END", Set(SEMICOLON, END))
    }
  }
  
  private def statement(): Unit = {
    if (isCurrentToken(BEGIN)) {
      compoundStatement()
    } else if (isCurrentToken(CALL)) {
      procedureStatement()
    } else if (isCurrentToken(ID)) {
      variable()
      matchToken(ASSIGNOP)
      expression()
    } else if (isCurrentToken(IF)) {
      matchToken(IF)
      expression()
      matchToken(THEN)
      statement()
      optionalElse()
    } else if (isCurrentToken(WHILE)) {
      matchToken(WHILE)
      expression()
      matchToken(DO)
      statement()
    } else {
      syntaxError("BEGIN, CALL, ID, IF, WHILE",
          Set(BEGIN, CALL, IF, WHILE), Set(ID))
    }
  }
  
  private def optionalElse(): Unit = {
    if (isCurrentToken(SEMICOLON, END)) {
      Unit
    } else if (isCurrentToken(ELSE)) {
      matchToken(ELSE)
      statement()
    } else {
      syntaxError("';', END, ELSE", Set(SEMICOLON, END, ELSE))
    }
  }
  
  private def variable(): Unit = {
    if (isCurrentToken(ID)) {
      matchToken(ID)
      arrayVariable()
    } else {
      syntaxError("ID", Set.empty, Set(ID))
    }
  }
  
  private def arrayVariable(): Unit = {
    if (isCurrentToken(SQUAREBRACKET_OPEN)) {
      matchToken(SQUAREBRACKET_OPEN)
      expression()
      matchToken(SQUAREBRACKET_CLOSE)
    } else if (isCurrentToken(ASSIGNOP)) {
      Unit
    } else {
      syntaxError("SQUAREBRACKET_OPEN, ASSIGNOP",
          Set(SQUAREBRACKET_OPEN, ASSIGNOP))
    }
  }
  
  private def procedureStatement(): Unit = {
    if (isCurrentToken(CALL)) {
      matchToken(CALL)
      matchToken(ID)
      optionalExpressionList()
    } else {
      syntaxError("CALL", Set(CALL))
    }
  }
  
  private def optionalExpressionList(): Unit = {
    if (isCurrentToken(PAREN_OPEN)) {
      matchToken(PAREN_OPEN)
      expressionList()
      matchToken(PAREN_CLOSE)
    } else if (isCurrentToken(SEMICOLON)) {
      Unit
    } else {
      syntaxError("'(', ';'", Set(PAREN_OPEN, SEMICOLON))
    }
  }
  
  private def expressionList(): Unit = {
    if (isCurrentToken(ID) || isCurrentToken(NUM) 
        || isCurrentToken(PAREN_OPEN, PLUS, MINUS, NOT)) {
      
      expression()
      expressionListTail()
    } else {
      syntaxError("ID, NUM, '(', '+', '-', NOT",
          Set(PAREN_OPEN, PLUS, MINUS, NOT), Set(ID, NUM))
    }
  }
  
  private def expressionListTail(): Unit = {
    if (isCurrentToken(PAREN_CLOSE)) {
      Unit
    } else if (isCurrentToken(COMMA)) {
      matchToken(COMMA)
      expression()
      expressionListTail()
    } else {
      syntaxError("')', ','", Set(PAREN_CLOSE, COMMA))
    }
  }
  
  private def expression(): Unit = {
    if (isCurrentToken(ID) || isCurrentToken(NUM) 
        || isCurrentToken(PAREN_OPEN, PLUS, MINUS, NOT)) {
      
      simpleExpression()
      optionalRelop()
    } else {
      syntaxError("ID, NUM, '(', '+', '-', NOT",
          Set(PAREN_OPEN, PLUS, MINUS, NOT), Set(ID, NUM))
    }
  }
  
  private def optionalRelop(): Unit = {
    if (isCurrentToken(PAREN_CLOSE, SQUAREBRACKET_CLOSE, COMMA, SEMICOLON,
        THEN, ELSE, DO, END)) {
      
      Unit
    } else if (isCurrentToken(RELOP)) {
      matchToken(RELOP)
      simpleExpression()
    } else {
      syntaxError("')', ']', ',', ';', THEN, ELSE, DO, END, RELOP",
          Set(PAREN_CLOSE, SQUAREBRACKET_CLOSE, COMMA, SEMICOLON, THEN, ELSE,
              DO, END),
          Set(RELOP))
    }
  }
  
  private def simpleExpression(): Unit = {
    if (isCurrentToken(ID) || isCurrentToken(NUM) 
        || isCurrentToken(PAREN_OPEN, NOT)) {
      
      term()
      optionalAddop()
    } else if (isCurrentToken(PLUS, MINUS)) {
      sign()
      term()
      optionalAddop()
    } else {
      syntaxError("ID, NUM, '(', '+', '-', NOT",
          Set(PAREN_OPEN, PLUS, MINUS, NOT), Set(ID, NUM))
    }
  }
  
  private def optionalAddop(): Unit = {
    if (isCurrentToken(PAREN_CLOSE, SQUAREBRACKET_CLOSE, COMMA, SEMICOLON,
        THEN, ELSE, DO, END) || isCurrentToken(RELOP)) {
      
      Unit
    } else if (isCurrentToken(ADDOP)) {
      matchToken(ADDOP)
      term()
      optionalAddop()
    } else {
      syntaxError("')', ']', ',', ';', THEN, ELSE, DO, END, RELOP, ADDOP",
          Set(PAREN_CLOSE, SQUAREBRACKET_CLOSE, COMMA, SEMICOLON, THEN, ELSE,
              DO, END),
          Set(RELOP, ADDOP))
    }
  }
  
  private def term(): Unit = {
    if (isCurrentToken(ID) || isCurrentToken(NUM) 
        || isCurrentToken(PAREN_OPEN, NOT)) {
      
      factor()
      optionalMulop()
    } else {
      syntaxError("ID, NUM, '(', NOT",
          Set(PAREN_OPEN, NOT), Set(ID, NUM))
    }
  }
  
  private def optionalMulop(): Unit = {
    if (isCurrentToken(PAREN_CLOSE, SQUAREBRACKET_CLOSE, COMMA, SEMICOLON,
        THEN, ELSE, DO, END)
        || isCurrentToken(RELOP) || isCurrentToken(ADDOP)) {
      
      Unit
    } else if (isCurrentToken(MULOP)) {
      matchToken(MULOP)
      factor()
      optionalMulop()
    } else {
      syntaxError(
          "')', ']', ',', ';', THEN, ELSE, DO, END, RELOP, ADDOP, MULOP",
          Set(PAREN_CLOSE, SQUAREBRACKET_CLOSE, COMMA, SEMICOLON, THEN, ELSE,
              DO, END),
          Set(RELOP, ADDOP, MULOP))
    }
  }
  
  private def factor(): Unit = {
    if (isCurrentToken(ID)) {
      matchToken(ID)
      arrayExpression()
    } else if (isCurrentToken(NUM)) {
      matchToken(NUM)
    } else if (isCurrentToken(PAREN_OPEN)) {
      matchToken(PAREN_OPEN)
      expression()
      matchToken(PAREN_CLOSE)
    } else if (isCurrentToken(NOT)) {
      matchToken(NOT)
      factor()
    } else {
      syntaxError("ID, NUM, '(', NOT",
          Set(PAREN_OPEN, NOT), Set(ID, NUM))
    }
  }
  
  private def arrayExpression(): Unit = {
    if (isCurrentToken(PAREN_CLOSE, SQUAREBRACKET_CLOSE, COMMA, SEMICOLON,
        THEN, ELSE, DO, END)
        || isCurrentToken(RELOP) || isCurrentToken(ADDOP)
        || isCurrentToken(MULOP)) {
      
      Unit
    } else if (isCurrentToken(SQUAREBRACKET_OPEN)) {
      matchToken(SQUAREBRACKET_OPEN)
      expression()
      matchToken(SQUAREBRACKET_CLOSE)
    } else {
      syntaxError(
          "')', '[', ']', ',', ';', THEN, ELSE, DO, END, RELOP, ADDOP, MULOP",
          Set(PAREN_CLOSE, SQUAREBRACKET_OPEN, SQUAREBRACKET_CLOSE, COMMA,
              SEMICOLON, THEN, ELSE, DO, END),
          Set(RELOP, ADDOP, MULOP))
    }
  }
  
  private def sign(): Unit = {
    if (isCurrentToken(PLUS)) {
      matchToken(PLUS)
    } else if (isCurrentToken(MINUS)) {
      matchToken(MINUS)
    } else {
      syntaxError("'+', '-'", Set(PLUS, MINUS))
    }
  }
  
}
