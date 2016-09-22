package net.michaelripley.pascalcompiler

private object LineFragment {
  private val spacePattern = """\s*""".r
}

case class LineFragment(contents: String, location: LineLocation) {
  
  def offset(offset: Int) = {
    if (offset == 0) {
      this
    } else {
      LineFragment(
        contents.substring(offset), 
        LineLocation(
          location.lineNumber,
          location.lineNumber + offset
        )
      )
    }
  }
  
  def removeLeadingSpace = {
    val length = LineFragment.spacePattern.findFirstIn(contents) match {
      case Some(space) => space.length
      case None => 0
    }
    offset(length)
  }
  
}
