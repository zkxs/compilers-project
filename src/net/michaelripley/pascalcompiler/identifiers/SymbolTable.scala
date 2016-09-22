package net.michaelripley.pascalcompiler.identifiers


import scala.collection.mutable.Map

class SymbolTable {
  private val symbolTable = Map[String, Identifier]()
 
  
  /**
   * Register a symbol if it is unregistered, otherwise do nothing
   */
  def registerSymbol(idName: String) = {
    symbolTable.getOrElseUpdate(idName, new Identifier(idName))
  }
  
  def getSymbolNumber(idName: String): Int = {
    symbolTable(idName).number
  }
  
}
