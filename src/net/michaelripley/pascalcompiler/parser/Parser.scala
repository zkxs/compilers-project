package net.michaelripley.pascalcompiler.parser

import net.michaelripley.pascalcompiler.tokens._

class Parser(tokens: List[Token]) {
  
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
  
  private type TokenMatcher = (Token => Boolean)
  
  private val ID: TokenMatcher = (t: Token) => {
    t match {
      case i: IdentifierToken => true
      case _ => false
    }
  }
  
  private val NUM: TokenMatcher = (t: Token) => {
    t match {
      case _: IntegerToken => true
      case a: AttributeToken
        if (a.tokenName == "REAL" || a.tokenName == "LONGREAL") => true
      case _ => false
    }
  }
  
  private val RELOP: TokenMatcher = (t: Token) => {
    t match {
      case a: AttributeToken if a.tokenName == "RELOP" => true
      case _ => false
    }
  }
  
  private val ADDOP: TokenMatcher = (t: Token) => {
    t match {
      case a: AttributeToken if a.tokenName == "ADDOP" => true
      case _ => false
    }
  }
  
  private val MULOP: TokenMatcher = (t: Token) => {
    t match {
      case a: AttributeToken if a.tokenName == "MULOP" => true
      case _ => false
    }
  }
  
  private val tokenIterator = tokens.iterator
  private var currentToken: Token = _
  
  private def nextToken(): Unit = {
    currentToken = tokenIterator.next()
  }
		  
  private def parse() = {
    nextToken()
    try {
      program()
    } catch {
      case e: UnexpectedEofException => {
        //TODO: handle unexpected EOF
      }
    }
    
    matchToken(EOF)
  }
  
  private def matchToken(m: TokenMatcher): Unit = {
    if (m(currentToken)) {
      nextToken()
    } else if (currentToken == EOF) {
      throw new UnexpectedEofException
    } else {
      //TODO: error recovery
    }
  }
  
  private def matchToken(t: Token): Unit = {
    matchToken(t => t == currentToken)
  }
  
  private def isCurrentToken(m: TokenMatcher): Boolean = {
    m(currentToken)
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
      //TODO: error
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
      //TODO: error
    }
  }
  
  private def identifierList(): Unit = {
    if (isCurrentToken(ID)) {
      matchToken(ID)
      identifierListTail()
    } else {
      //TODO: Error
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
      //TODO: error
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
      //TODO: error
    }
  }
  
  private def optionalDeclarations(): Unit = {
    if (isCurrentToken(PROCEDURE, BEGIN)) {
      Unit
    } else if (isCurrentToken(VAR)) {
      declarations()
    } else {
      //TODO: error
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
      //TODO: error
    }
  }
  
  private def standardType(): Unit = {
    if (isCurrentToken(INTEGER)) {
      matchToken(INTEGER)
    } else if (isCurrentToken(REAL)) {
      matchToken(REAL)
    } else {
      //TODO: error
    }
  }
  
  private def subprogramDeclarations(): Unit = {
    if (isCurrentToken(PROCEDURE)) {
      subprogramDeclaration()
      matchToken(SEMICOLON)
      optionalSubprogramDeclarations()
    } else {
      //TODO: error
    }
  }
  
  private def optionalSubprogramDeclarations(): Unit = {
    if (isCurrentToken(PROCEDURE)) {
      subprogramDeclarations()
    } else if (isCurrentToken(BEGIN)) {
      Unit
    } else {
      //TODO: error
    }
  }
  
  private def subprogramDeclaration(): Unit = {
    if (isCurrentToken(PROCEDURE)) {
      subprogramHead()
      subprogramDeclarationPrime()
    } else {
      //TODO: error
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
      //TODO: error
    }
  }
  
  private def subprogramHead(): Unit = {
    if (isCurrentToken(PROCEDURE)) {
      matchToken(PROCEDURE)
      matchToken(ID)
      subprogramHeadPrime()
    } else {
      //TODO: error
    }
  }
  
  private def subprogramHeadPrime(): Unit = {
    if (isCurrentToken(PAREN_OPEN)) {
      arguments()
      matchToken(SEMICOLON)
    } else if (isCurrentToken(SEMICOLON)) {
      matchToken(SEMICOLON)
    } else {
      //TODO: error
    }
  }
  
  private def arguments(): Unit = {
    if (isCurrentToken(PAREN_OPEN)) {
      matchToken(PAREN_OPEN)
      parameterList()
      matchToken(PAREN_CLOSE)
    } else {
      //TODO: error
    }
  }
  
  private def parameterList(): Unit = {
    if (isCurrentToken(ID)) {
      matchToken(ID)
      matchToken(COLON)
      anyType()
      parameterListTail()
    } else {
      //TODO: error
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
      //TODO: error
    }
  }
  
  private def compoundStatement(): Unit = {
    if (isCurrentToken(BEGIN)) {
      matchToken(BEGIN)
      compoundStatmentTail()
    } else {
      //TODO: error
    }
  }
  
  private def compoundStatmentTail(): Unit = {
    if (isCurrentToken(BEGIN, CALL, IF, WHILE) || isCurrentToken(ID)) {
      statementList()
      matchToken(END)
    } else if (isCurrentToken(END)) {
      matchToken(END)
    } else {
      //TODO: error
    }
  }
  
  private def statementList(): Unit = {
    if (isCurrentToken(BEGIN, CALL, IF, WHILE) || isCurrentToken(ID)) {
      statement()
      statementListTail()
    } else {
      //TODO: error
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
      //TODO: error
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
      //TODO: error
    }
  }
  
  private def optionalElse(): Unit = {
    if (isCurrentToken(SEMICOLON, END)) {
      Unit
    } else if (isCurrentToken(ELSE)) {
      matchToken(ELSE)
      statement()
    } else {
      //TODO: error
    }
  }
  
  private def variable(): Unit = {
    if (isCurrentToken(ID)) {
      matchToken(ID)
      arrayVariable()
    } else {
      //TODO: error
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
      //TODO: error
    }
  }
  
  private def procedureStatement(): Unit = {
    if (isCurrentToken(CALL)) {
      matchToken(CALL)
      matchToken(ID)
      optionalExpressionList()
    } else {
      //TODO: error
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
      //TODO: error
    }
  }
  
  private def expressionList(): Unit = {
    if (isCurrentToken(ID) || isCurrentToken(NUM) 
        || isCurrentToken(PAREN_OPEN, PLUS, MINUS, NOT)) {
      
      expression()
      expressionListTail()
    } else {
      //TODO: error
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
      //TODO: error
    }
  }
  
  private def expression(): Unit = {
    if (isCurrentToken(ID) || isCurrentToken(NUM) 
        || isCurrentToken(PAREN_OPEN, PLUS, MINUS, NOT)) {
      
      simpleExpression()
      optionalRelop()
    } else {
      //TODO: error
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
      //TODO: error
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
      //TODO: error
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
      //TODO: error
    }
  }
  
  private def term(): Unit = {
    if (isCurrentToken(ID) || isCurrentToken(NUM) 
        || isCurrentToken(PAREN_OPEN, NOT)) {
      
      factor()
      optionalMulop()
    } else {
      //TODO: error
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
      //TODO: error
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
      //TODO: error
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
      //TODO: error
    }
  }
  
  private def sign(): Unit = {
    if (isCurrentToken(PLUS)) {
      matchToken(PLUS)
    } else if (isCurrentToken(MINUS)) {
      matchToken(MINUS)
    } else {
      //TODO: error
    }
  }
  
}
