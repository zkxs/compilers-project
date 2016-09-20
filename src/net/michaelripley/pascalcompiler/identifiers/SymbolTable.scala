package net.michaelripley.pascalcompiler.identifiers

import java.util.concurrent.atomic.AtomicInteger
import scala.collection.mutable.Map

class SymbolTable {
  val symbolTable = Map[Identifier, Int]()
  val nextNumber = new AtomicInteger()
  
  def registerSymbol(id: Identifier): Unit = {
    symbolTable.getOrElseUpdate(id, getUniqueNumber)
  }
  
  def getSymbolNumber(id: Identifier): Int = {
    symbolTable(id)
  }
  
  private def getUniqueNumber(): Int = {
    nextNumber.getAndIncrement()
  }
}
