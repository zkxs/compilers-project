package net.michaelripley.pascalcompiler.identifiers

import java.util.concurrent.atomic.AtomicInteger
import scala.collection.mutable.Map

class SymbolTable {
  
  private val symbolTable = Map[String, Identifier]()
  
  private val nextNumber = new AtomicInteger()
  
  private def getUniqueNumber(): Int = {
    nextNumber.getAndIncrement()
  }
  
  /**
   * Register a symbol if it is unregistered, otherwise do nothing
   */
  def registerSymbol(idName: String) = {
    symbolTable.getOrElseUpdate(idName, new Identifier(idName, getUniqueNumber()))
  }
  
  def getSymbolNumber(idName: String): Int = {
    symbolTable(idName).number
  }
  
}
