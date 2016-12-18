package net.michaelripley.pascalcompiler.parser

import net.michaelripley.pascalcompiler.tokens.Token

class Parser(tokens: List[Token]) {
  
  val EOF = new Token("EOF")
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
  
  def matchToken(t: Token) = {
    if (t == currentToken) {
      
    } else if (t == EOF) {
      throw new UnexpectedEofException
    } else {
      //TODO: error recovery
    }
  }
  
  
  def program() = {
    
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
