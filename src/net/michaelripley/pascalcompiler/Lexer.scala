package net.michaelripley.pascalcompiler

import scala.io.Source

object Lexer {
  def main(args: Array[String]): Unit = {
    val lexer = new Lexer(Source.fromFile("test.pas"))
    lexer.lex()
  }
}

class Lexer(val source: Source) {
  def lex(): Unit = {
    source.getLines().zipWithIndex.foreach {
      case (line, index) => {
        println(f"${index + 1}%5d: $line")
      }
    }
  }
}
