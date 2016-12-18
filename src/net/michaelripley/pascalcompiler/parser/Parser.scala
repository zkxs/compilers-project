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
  private val PAREN_OPEN = new AttributeToken("PAREN", "OPEN")
  private val PAREN_CLOSE = new AttributeToken("PAREN", "CLOSE")
  private val SQUAREBRACKET_OPEN = new AttributeToken("SQUAREBRACKET", "OPEN")
  private val SQUAREBRACKET_CLOSE = new AttributeToken("SQUAREBRACKET", "CLOSE")
  private val COMMA = new AttributeToken("COMMA")
  private val COLON = new AttributeToken("COLON")
  private val SEMICOLON = new AttributeToken("SEMICOLON")
  private val FULLSTOP = new AttributeToken("FULLSTOP")
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
      // ODO: error
    }
  }
  
  private def programPrime(): Unit = {
    if (isCurrentToken(PROCEDURE)) {
      subprogramDeclaration()
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
      
    } else if (isCurrentToken(ARRAY)) {
      
    } else {
      //TODO: error
    }
  }
  
  private def standardType(): Unit = {
    
  }
  
  private def subprogramDeclarations(): Unit = {
    
  }
  
  private def optionalSubprogramDeclarations(): Unit = {
    
  }
  
  private def subprogramDeclaration(): Unit = {
    
  }
  
  private def subprogramDeclarationPrime(): Unit = {
    
  }
  
  private def subprogramHead(): Unit = {
    
  }
  
  private def subprogramHeadPrime(): Unit = {
    
  }
  
  private def arguments(): Unit = {
    
  }
  
  private def parameterList(): Unit = {
    
  }
  
  private def parameterListTail(): Unit = {
    
  }
  
  private def compoundStatement(): Unit = {
    
  }
  
  private def compoundStatmentTail(): Unit = {
    
  }
  
  private def statementList(): Unit = {
    
  }
  
  private def statementListTail(): Unit = {
    
  }
  
  private def statement(): Unit = {
    
  }
  
  private def optionalElse(): Unit = {
    
  }
  
  private def variable(): Unit = {
    
  }
  
  private def arrayVariable(): Unit = {
    
  }
  
  private def procedureStatement(): Unit = {
    
  }
  
  private def optionalExpressionList(): Unit = {
    
  }
  
  private def expressionList(): Unit = {
    
  }
  
  private def expressionListTail(): Unit = {
    
  }
  
  private def expression(): Unit = {
    
  }
  
  private def optionalRelop(): Unit = {
    
  }
  
  private def simpleExpression(): Unit = {
    
  }
  
  private def optionalAddop(): Unit = {
    
  }
  
  private def term(): Unit = {
    
  }
  
  private def optionalMulop(): Unit = {
    
  }
  
  private def factor(): Unit = {
    
  }
  
  private def arrayExpression(): Unit = {
    
  }
  
  private def sign(): Unit = {
    
  }
  
}
