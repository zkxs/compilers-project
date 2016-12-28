package net.michaelripley.pascalcompiler.tokens

import java.io.PrintWriter
import scala.collection.mutable.{Map, Queue}

class ListingPrinter(tokens: List[Token],
    lines: Array[String],
    listWriter: PrintWriter) {
  
  val lineBuffer = Map.empty[Int, Queue[String]]
  
  lines.indices.foreach(printLine)
  
  private def putLine(lineNumber: Int, line: String): Unit = {
    val lineNBuffer = lineBuffer.getOrElseUpdate(lineNumber, Queue.empty)
    lineNBuffer.enqueue(line)
  }
  
  // print LEXERR token
  def printError(errorToken: ErrorToken): Unit = {
    putLine(errorToken.lexeme.location.lineNumber, errorToken.fullErrorString())
  }
  
  // print generic error
  def printError(lineNumber: Int, errorLine: String): Unit = {
    putLine(lineNumber, errorLine)
  }
  
  // print entire line
  private def printLine(lineNumber: Int): Unit = {
    putLine(lineNumber, f"${lineNumber + 1}%5d: ${lines(lineNumber)}")
  }
  
  def finishPrinting(): Unit = {
    lineBuffer
        .toVector
        .sortBy(x => x._1)
        .foreach(tup => tup._2.foreach(line => listWriter.println(line)))
  }
  
}
