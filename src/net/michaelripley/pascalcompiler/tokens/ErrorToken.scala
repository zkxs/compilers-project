package net.michaelripley.pascalcompiler.tokens

private object ErrorToken {
  private val emptyString = ""
}

import net.michaelripley.Util
import net.michaelripley.pascalcompiler.lexer.Lexeme
import ErrorToken._

class ErrorToken(
    tokenName: String,
    attribute: Option[String],
    lexeme: Lexeme) extends AttributeToken(tokenName, attribute, lexeme) {
  
  override def canEqual(other: Any): Boolean = {
    other.isInstanceOf[ErrorToken]
  }
  
  override def equals(other: Any): Boolean = {
    other match {
      case other: ErrorToken => {
        (other canEqual this) && ((other eq this) || (
            other.tokenName == tokenName
            && other.attribute == attribute
            && other.lexeme == lexeme
        ))
      }
      case _ => false
    }
  }
  
  override def hashCode(): Int = {
    Util.hash(tokenName, attribute, lexeme)
  }
  
  def errorString(): String = {
    val q = '"'
    attribute match {
      case Some(attr) => s"$attr: $q${lexeme.lexeme}$q"
      case None       => s"$q${lexeme.lexeme}$q"
    }
  }
  
  def fullErrorString(): String = {
    val space = " " * (lexeme.location.columnOffset + 7)
    space + s"^ $tokenName: " + errorString()
  }
}
