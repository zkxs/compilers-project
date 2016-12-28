package net.michaelripley.pascalcompiler.tokens

import java.io.PrintWriter

class ListingPrinter(tokens: List[Token],
    lines: Array[String],
    listWriter: PrintWriter) {
  
  // print LEXERR token
  def printError(errorToken: ErrorToken) = {
    listWriter.println(errorToken.fullErrorString())
  }
  
  // print generic error
  def printError(errorLine: String) = {
    listWriter.println(errorLine)
  }
  
  // print entire line
  def printLine(lineNumber: Int) = {
    listWriter.println(f"${lineNumber + 1}%5d: ${lines(lineNumber)}")
  }
  
  def finishPrinting() = {
    
  }
  
}
