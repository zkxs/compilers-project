package net.michaelripley.pascalcompiler.parser

import scala.annotation.switch
import net.michaelripley.pascalcompiler.tokens._

class Parser(tokens: List[Token]) {
  
  // easy tokens to match
  val PROGRAM = new AttributeToken("PROGRAM")
  val PROCEDURE = new AttributeToken("PROCEDURE")
  val BEGIN = new AttributeToken("BEGIN")
  val CALL = new AttributeToken("CALL")
  val VAR = new AttributeToken("VAR")
  val INTEGER = new AttributeToken("INTEGER")
  val REAL = new AttributeToken("REAL")
  val ARRAY = new AttributeToken("ARRAY")
  val PAREN_OPEN = new AttributeToken("PAREN", "OPEN")
  val PAREN_CLOSE = new AttributeToken("PAREN", "CLOSE")
  val SQUAREBRACKET_OPEN = new AttributeToken("SQUAREBRACKET", "OPEN")
  val SQUAREBRACKET_CLOSE = new AttributeToken("SQUAREBRACKET", "CLOSE")
  val COMMA = new AttributeToken("COMMA")
  val SEMICOLON = new AttributeToken("SEMICOLON")
  val FULLSTOP = new AttributeToken("FULLSTOP")
  val PLUS = new AttributeToken("ADDOP", "ADD")
  val MINUS = new AttributeToken("ADDOP", "SUBTRACT")
  val ASSIGNOP = new AttributeToken("ASSIGNOP")
  val NOT = new AttributeToken("NOT")
  val IF = new AttributeToken("IF")
  val THEN = new AttributeToken("THEN")
  val ELSE = new AttributeToken("ELSE")
  val DO = new AttributeToken("DO")
  val WHILE = new AttributeToken("WHILE")
  val END = new AttributeToken("END")
  val EOF = new Token("EOF")
  
  // harder tokens to match (they can be different literals)
  
  val ID = (t: Token) => {
    t match {
      case i: IdentifierToken => true
      case _ => false
    }
  }
  
  val NUM = (t: Token) => {
    t match {
      case _: IntegerToken => true
      case a: AttributeToken
        if (a.tokenName == "REAL" || a.tokenName == "LONGREAL") => true
      case _ => false
    }
  }
  
  val RELOP = (t: Token) => {
    t match {
      case a: AttributeToken if a.tokenName == "RELOP" => true
      case _ => false
    }
  }
  
  val ADDOP = (t: Token) => {
    t match {
      case a: AttributeToken if a.tokenName == "ADDOP" => true
      case _ => false
    }
  }
  
  val MULOP = (t: Token) => {
    t match {
      case a: AttributeToken if a.tokenName == "MULOP" => true
      case _ => false
    }
  }
  
  val tokenIterator = tokens.iterator
  var currentToken: Token = _
  
  def nextToken(): Unit = {
    currentToken = tokenIterator.next()
  }
		  
  def parse() = {
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
  
  def matchToken(m: Token => Boolean): Unit = {
    if (m(currentToken)) {
      
    } else if (currentToken == EOF) {
      throw new UnexpectedEofException
    } else {
      //TODO: error recovery
    }
  }
  
  def matchToken(t: Token): Unit = {
    matchToken(t => t == currentToken)
  }
  
  def program() = {
    if (currentToken == PROGRAM) {
      matchToken(PROGRAM)
      matchToken(ID)
      matchToken(PAREN_OPEN)
      identifierList()
      matchToken(PAREN_CLOSE)
      matchToken(SEMICOLON)
      programPrime()
    } else {
      // TODO: error
    }
  }
  
  def programPrime() = {
    
  }
  
  def identifierList() = {
    
  }
  
  def identifierListTail() = {
    
  }
  
  def declarations() = {
    
  }
  
  def optionalDeclarations() = {
    
  }
  
  // could not name this function type, as type is a reserved word in Scala
  def anyType() = {
    
  }
  
  def standardType() = {
    
  }
  
  def subprogramDeclarations() = {
    
  }
  
  def optionalSubprogramDeclarations() = {
    
  }
  
  def subprogramDeclaration() = {
    
  }
  
  def subprogramDeclarationPrime() = {
    
  }
  
  def subprogramHead() = {
    
  }
  
  def subprogramHeadPrime() = {
    
  }
  
  def arguments() = {
    
  }
  
  def parameterList() = {
    
  }
  
  def parameterListTail() = {
    
  }
  
  def compoundStatement() = {
    
  }
  
  def compoundStatmentTail() = {
    
  }
  
  def statementList() = {
    
  }
  
  def statementListTail() = {
    
  }
  
  def statement() = {
    
  }
  
  def optionalElse() = {
    
  }
  
  def variable() = {
    
  }
  
  def arrayVariable() = {
    
  }
  
  def procedureStatement() = {
    
  }
  
  def optionalExpressionList() = {
    
  }
  
  def expressionList() = {
    
  }
  
  def expressionListTail() = {
    
  }
  
  def expression() = {
    
  }
  
  def optionalRelop() = {
    
  }
  
  def simpleExpression() = {
    
  }
  
  def optionalAddop() = {
    
  }
  
  def term() = {
    
  }
  
  def optionalMulop() = {
    
  }
  
  def factor() = {
    
  }
  
  def arrayExpression() = {
    
  }
  
  def sign() = {
    
  }
  
}
