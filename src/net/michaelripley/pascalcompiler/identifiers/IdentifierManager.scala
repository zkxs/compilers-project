package net.michaelripley.pascalcompiler.identifiers

private[identifiers] object IdentifierManager {
  case class IdentifierError(val message: String)
  type Rt = Option[IdentifierError] // return type for failable functions
}

import IdentifierManager._
import scala.collection.mutable.Map

/**
 * 2nd generation symbol table.  This is a complete rewrite of the old
 * symbol table, and takes into account type, scope, and size.
 */
class IdentifierManager {
  
  private var program: Option[SubProgram] = None
  private var currentScope: Option[SubProgram] = None
  
  private val tokenLocations = Map.empty[Identifier, Int]
  private val variableLocations = Map.empty[TypedIdentifier, Int]
  
  private def error(message: String) = {
    Some(IdentifierError(message))
  }
  
  def addProgram(id: Identifier): Rt = {
    if (program.isEmpty) {
      program = Some(new SubProgram(id.name, None))
      currentScope = program
      None
    } else {
      error("program already defined")
    }
  }
  
  def addParam(id: Identifier, idType: Type): Rt = {
    currentScope match {
      case Some(scope) => {
        //scope. //TODO
        None
      }
      case _ => error("no scope defined")
    }
  }
  
  def addProcedure(idName: String, params: Iterable[Type]) = {
    //TODO
  }
  
  def addVariable(idName: String, idType: Type) = {
    //TODO
  }
  
  def checkCall(procedureName: String, procedureParams: Iterable[Type]) = {
    //TODO
  }
  
  def pop() = {
    currentScope match {
      case Some(scope) => {
        scope.parent match {
          case parent: Some[SubProgram] => currentScope = parent
          case _ => currentScope = None
        }
      }
      case _ => error("no scope defined")
    }
  }
  
}
