package net.michaelripley.pascalcompiler

import scala.io.Source

object Lister {
  def main(args: Array[String]): Unit = {
    Source.fromFile("test.pas").getLines().zipWithIndex.foreach {
      case (line, index) => {
        println(f"${index + 1}%5d: $line")
      }
    }
  }
}
