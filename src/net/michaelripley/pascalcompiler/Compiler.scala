package net.michaelripley.pascalcompiler

import scala.io.Source
import net.michaelripley.pascalcompiler.lexer.Lexer

/**
 * This is the main class
 */
object Compiler {
  
  val version = "3.0.1"
  
  def main(args: Array[String]): Unit = {
    
    val versionPrompts = """(?i)^--?v(?:ersion)?$""".r
    
    if (args.length == 1 && versionPrompts.findPrefixOf(args(0)).isDefined) {
      println("MichaelCompiler v" + version)
    } else {
      val lexer = new Lexer(
          resource("/reservedwords.dat"),
          resource("/operators.dat"),
          resource("/punctuation.dat")
      )
      
      args.foreach(lexer.lex)
    }
  }
  
  private def resource(path: String): Source = {
    Source.fromInputStream(getClass.getResourceAsStream(path))
  }
  
}
