package net.michaelripley.pascalcompiler

import scala.io.Source
import net.michaelripley.pascalcompiler.lexer.Lexer

/**
 * This is the main class
 */
object Compiler {
  
  def main(args: Array[String]): Unit = {
    
    val lexer = new Lexer(
        resource("/reservedwords.dat"),
        resource("/operators.dat"),
        resource("/punctuation.dat")
    )
    
    args.foreach(lexer.lex)
  }
  
  private def resource(path: String): Source = {
    Source.fromInputStream(getClass.getResourceAsStream(path))
  }
  
}
