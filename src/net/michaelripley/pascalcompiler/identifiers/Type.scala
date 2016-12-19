package net.michaelripley.pascalcompiler.identifiers

sealed trait Type {
  def size: Int
}

sealed trait ArrayableType extends Type

object Type {
  
  case class T_Integer() extends ArrayableType {
    override def size = 4
  }
  
  case class T_Real() extends ArrayableType {
    override def size = 8
  }
  
  case class T_Boolean() extends Type {
    override def size = 0
  }
  
  case class T_ProgramParam() extends Type {
    override def size = 0
  }
  
  case class T_Array(val length: Int, val elementType: ArrayableType)
      extends Type {
    override def size = {length * elementType.size}
  }
  
}
