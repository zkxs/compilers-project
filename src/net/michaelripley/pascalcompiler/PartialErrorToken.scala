package net.michaelripley.pascalcompiler

class PartialErrorToken(token: String, attribute: Option[String]) extends PartialToken(token, attribute) {
  override def makeToken(lexeme: Lexeme) = {
    new ErrorToken(token, attribute, lexeme)
  }
}