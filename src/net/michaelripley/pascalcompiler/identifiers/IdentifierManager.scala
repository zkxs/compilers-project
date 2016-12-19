package net.michaelripley.pascalcompiler.identifiers

private[identifiers] object IdentifierManager {
  case class IdentifierError(val message: String)
  type Rt = Option[IdentifierError] // return type for failable functions
  private val q = '"'
}

import IdentifierManager._
import scala.collection.mutable.Map
import scala.collection.mutable.MutableList

/**
 * 2nd generation symbol table.  This is a complete rewrite of the old
 * symbol table, and takes into account type, scope, and size.
 */
class IdentifierManager {
  
  private var program: Option[SubProgram] = None
  private var currentScope: Option[SubProgram] = None
  
  // maps to keep track of variable locations
  private val tokenLocations = Map.empty[Identifier, Int]
  private val variableLocations = Map.empty[TypedIdentifier, Int]
  
  private def error(message: String) = {
    Some(IdentifierError(message))
  }
  
  def addProgram(id: Identifier, params: List[TypedIdentifier]): Rt = {
    if (program.isEmpty) {
      program = Some(new SubProgram(id.name, params, None))
      currentScope = program
      None
    } else {
      error("program already defined")
    }
  }
  
  def addVariable(id: Identifier, idType: Type): Rt = {
    currentScope match {
      case Some(scope) => {
        if (scope.addVariable(TypedIdentifier(id.name, idType))) {
          // add successful
          None 
        } else {
          // add failed
          error(s"variable $q${id.name}$q already exists in this scope")
        }
      }
      case _ => error("no scope defined")
    }
  }
  
  def addProcedure(id: Identifier, params: MutableList[Type]): Rt = {
    //TODO
    None
  }
  
  def checkCall(id: Identifier, params: MutableList[Type]): Rt = {
    //TODO
    None
  }
  
  def pop(): Rt = {
    currentScope match {
      case Some(scope) => {
        scope.parent match {
          case parent: Some[SubProgram] => {
            currentScope = parent
            None
          }
          case _ => {
            currentScope = None
            None
          }
        }
      }
      case _ => error("no scope defined")
    }
  }
  
}
