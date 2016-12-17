package net.michaelripley.pascalcompiler.lexer

import net.michaelripley.pascalcompiler.lexer.LineLocation

private object LineFragment {
  private val spacePattern = """\s*""".r
}

case class LineFragment(contents: String, location: LineLocation) {
  
  def offset(offset: Int): LineFragment = {
    if (offset == 0) {
      this
    } else {
      LineFragment(
        contents.substring(offset), 
        LineLocation(
          location.lineNumber,
          location.columnOffset + offset
        )
      )
    }
  }
  
  def removeLeadingSpace(): LineFragment = {
    val length = LineFragment.spacePattern.findPrefixOf(contents) match {
      case Some(space) => space.length
      case None => 0
    }
    offset(length)
  }
  
  def length() = {
    contents.length()
  }
  
  def isEmpty() = {
    contents.isEmpty()
  }
  
}
