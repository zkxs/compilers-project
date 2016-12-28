package net.michaelripley.pascalcompiler.tokens

import java.io.PrintWriter
import scala.collection.mutable.Queue

class ListingPrinter(tokens: List[Token],
    lines: Array[String],
    listWriter: PrintWriter) {
  
  case class Line(val number: Int, val line: String)
  
  val lineBuffer = Queue.empty[Line]
  
  private def realPrintLine(line: Line) = {
    listWriter.println(f"${line.number + 0}%5d: ${line.line}")
  }
  
  private def printBuffer(newLine: Line): Unit = {
    printBuffer()
    lineBuffer.enqueue(newLine)
  }
  
  private def printBuffer(): Unit = {
    lineBuffer.foreach(realPrintLine)
    lineBuffer.clear()
    if (!lineBuffer.isEmpty) {
      throw new AssertionError("couldn't empty line buffer")
    }
  }
  
  private def printBufferTo(lineNumber: Int): Unit = {
    lineBuffer.dequeueAll( l => l.number <= lineNumber ).foreach(realPrintLine)
  }
  
  // print LEXERR token
  def printError(errorToken: ErrorToken): Unit = {
    printBuffer()
    listWriter.println(errorToken.fullErrorString())
  }
  
  // print generic error
  def printError(errorLine: String): Unit = {
    printBuffer()
    listWriter.println(errorLine)
  }
  
  // print semantic error
  def printSemanticError(lineNumber: Int, errorLine: String): Unit = {
    printBufferTo(lineNumber)
    listWriter.println(s"$errorLine [$lineNumber]")
  }
  
  // print entire line
  def printLine(lineNumber: Int): Unit = {
    lineBuffer.enqueue(Line(lineNumber, lines(lineNumber)))
  }
  
  def finishPrinting(): Unit = {
    printBuffer()
  }
  
}
