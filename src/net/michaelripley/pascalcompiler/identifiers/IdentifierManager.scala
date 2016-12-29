package net.michaelripley.pascalcompiler.identifiers

private object IdentifierManager {
  private type Err = Option[IdentifierError] // return type for failable functions
  private val q = '"'
}

import IdentifierManager._
import scala.collection.mutable.Map

/**
 * 2nd generation symbol table.  This is a complete rewrite of the old
 * symbol table, and takes into account type, scope, and size.
 */
class IdentifierManager {
  
  // pointers to head and current location in tree
  private var program: Option[SubProgram] = None
  private var currentScope: Option[SubProgram] = None
  
  // offset in memory of next variable
  private var nextScopeIndex: Int = 0
  
  // maps to keep track of variable locations
  private val tokenLocations = Map.empty[Identifier, (Int, Int)]
  private val variableLocations = Map.empty[TypedIdentifier, (Int, Int)]
  
  def getTokenLocations() = {
    tokenLocations.toMap
  }
  
  def getVariableLocations() = {
    variableLocations.toMap
  }
  
  private def error(message: String) = {
    Some(IdentifierError(message))
  }
  
  def addProgram(id: Identifier, params: List[TypedIdentifier]): Err = {
    if (program.isEmpty) {
      program = Some(new SubProgram(id.name, params, None, nextScopeIndex))
      nextScopeIndex += 1
      currentScope = program
      None
    } else {
      error("program already defined")
    }
  }
  
  def addVariable(id: Identifier, idType: Type): Err = {
    currentScope match {
      case Some(scope) => {
        val typedId = TypedIdentifier(id.name, idType)
        if (scope.addVariable(typedId)) {
          // add successful
          tokenLocations.put(id, (scope.scopeIndex, scope.offset))
          variableLocations.put(typedId, (scope.scopeIndex, scope.offset))
          scope.offset += typedId.idType.size
          None 
        } else {
          // add failed
          error(s"variable $q${id.name}$q already exists in this scope")
        }
      }
      case _ => error("addVariable(): no scope defined")
    }
  }
  
  /**
   * @return either an error or the found TypedIdentifier
   */
  def getVariable(id: Identifier): Either[IdentifierError, TypedIdentifier] = {
    currentScope match {
      case Some(scope) => {
        scope.findVariable(id.name) match {
          case Some(v) => {
            // record the new id that points to this variable
            variableLocations.get(v).map( loc => tokenLocations.put(id, loc))
            
            Right(v)
          }
          case _ => Left(IdentifierError(s"$q${id.name}$q not declared"))
        }
      }
      case _ => Left(IdentifierError("getVariable(): no scope defined"))
    }
  }
  
  def addProcedure(id: Identifier, params: List[TypedIdentifier]): Err = {
    currentScope match {
      case Some(scope) => {
        val err = scope.addSubProgram(id.name, params, nextScopeIndex) match {
          case sp: Some[SubProgram] => {
            // add successful
            currentScope = sp
            None
          }
          case None => {
            // add failed
            error(s"procedure ${id.name}(${params.mkString(", ")}) already exists in this scope")
          }
        }
        nextScopeIndex += 1
        err // return
      }
      case _ => error("addProcedure(): no scope defined")
    }
  }
  
  /**
   * Assert that a procedure being called is accessible from this scope
   */
  def checkCall(id: Identifier, params: List[Type]): Err = {
    currentScope match {
      case Some(scope) => {
        if (scope.isSubProgramInScope(id.name, Some(params))) {
          None
        } else {
          if (scope.isSubProgramInScope(id.name, None)) {
            error(s"procedure ${id.name}(${params.mkString(", ")}) has incorrect parameters")
          } else {
        	  error(s"procedure ${id.name}(${params.mkString(", ")}) does not exist in this scope")
          }
        }
      }
      case _ => error("checkCall(): no scope defined")
    }
  }
  
  def pop(): Err = {
    currentScope match {
      case Some(scope) => {
        // if we are in a scope
        scope.parent match {
          case parent: Some[SubProgram] => {
            // if parent exists, update scope to it
            currentScope = parent
            None
          }
          case _ => {
            // if no parent, scope is now None
            currentScope = None
            None
          }
        }
      }
      case _ => error("pop(): no scope defined") // we are not in a scope.
    }
  }
  
}
