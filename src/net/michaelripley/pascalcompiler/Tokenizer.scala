package net.michaelripley.pascalcompiler

trait Tokenizer {
  val spacePattern = """\s+""".r
  
  /**
   * Extract a token. Will not have leading space.
   */
  def extractToken(line: String, lineNumber: Int, columnOffset: Int): Option[Token];
}
