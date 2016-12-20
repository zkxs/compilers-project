package net.michaelripley.pascalcompiler.identifiers

sealed trait Type {
  def size: Int
}

sealed trait ArrayableType extends Type

object Type {
  
  case class T_Integer() extends ArrayableType {
    override def size = 4
    override def toString() = "INTEGER"
  }
  
  case class T_Real() extends ArrayableType {
    override def size = 8
    override def toString() = "REAL"
  }
  
  case class T_Boolean() extends Type {
    override def size = 0
    override def toString() = "BOOLEAN"
  }
  
  case class T_ProgramParam() extends Type {
    override def size = 0
    override def toString() = "PROGPARAM"
  }
  
  case class T_Array(val length: Int, val elementType: ArrayableType)
      extends Type {
    override def size = {length * elementType.size}
    override def toString() = s"ARRAY[$elementType]"
  }
  
}
