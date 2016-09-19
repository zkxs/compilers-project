package net.michaelripley.pascalcompiler

trait Tokenizer {
  val spacePattern = """\s+""".r
  
  /**
   * Extract a token. Line may have leading space.
   */
  final def extractToken(lineNumber: Int, offset: Int, line: String): Option[TokenizerResult] = {
    
    spacePattern.findPrefixOf(line) match {
      case Some(leadingSpace) => {
        val restOfLine = line.substring(leadingSpace.size)
        extractTokenNoSpace(lineNumber, offset + leadingSpace.size, restOfLine)
      }
      case None => extractTokenNoSpace(lineNumber, offset, line)
    }
  }
  
  /**
   * Extract a token. Line will not have leading space.
   */
  private final def extractTokenNoSpace(lineNumber: Int, offset: Int, line: String): Option[TokenizerResult] = {
    
    extractTokenImpl(lineNumber, offset, line) match {
      case Some(token) => {
        Some(TokenizerResult(token, line.substring(token.lexeme.size)))
      }
      case None => None
    }
  }
  
  protected def extractTokenImpl(lineNumber: Int, offset: Int, line: String): Option[Token];
}
