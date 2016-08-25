package net.michaelripley.pascalcompiler

import scala.io.Source

object Lister {
  def main(args: Array[String]): Unit = {
    Source.fromFile("test.pas").getLines().zipWithIndex.foreach {
      case (line, index) => {
        if (line.length > 71) {
          println(s"ERROR: Line $index is longer than 72 characters")
          System.exit(1)
        } else {
          println(f"${index + 1}%5d: $line")
        }
      }
    }
  }
}
