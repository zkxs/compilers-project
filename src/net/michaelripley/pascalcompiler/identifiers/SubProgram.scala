package net.michaelripley.pascalcompiler.identifiers

import scala.annotation.tailrec
import scala.collection.GenSeqLike
import scala.collection.mutable.MutableList
import net.michaelripley.Util

private[identifiers] class SubProgram(
    val idName: String,
    val params: List[TypedIdentifier],
    val parent: Option[SubProgram]) {
  
  private val variables   = MutableList.empty[TypedIdentifier]
  private val subPrograms = MutableList.empty[SubProgram]
  
  private def getVariable(idName: String) = {
    variables.find( _.name == idName )
  }
  
  private def hasVariable(idName: String) = {
    getVariable(idName).isDefined
  }
  
  private def getParam(idName: String) = {
    params.find( p => p.name == idName )
  }
  
  private def hasParam(idName: String) = {
    getParam(idName).isDefined
  }
  
  private def paramsEqual(a: List[Type]): Boolean = {
    a == params.map( _.idType )
  }
  
  private def paramsEqual(a: List[TypedIdentifier]): Boolean = {
    paramsEqual(a.map( _.idType ))
  }
  
  /**
   * Try to add a variable
   * @return true if variable was added, false otherwise
   */
  def addVariable(id: TypedIdentifier): Boolean = {
    if (hasVariable(id.name)) {
      false
    } else {
      variables += id
      true
    }
  }
  
  private def getSubProgram(idName: String, params: List[Type]) = {
    subPrograms.find( s => s.idName == idName && s.paramsEqual(params) )
  }
  
  private def hasSubProgram(idName: String, params: List[Type]) = {
    getSubProgram(idName, params).isDefined
  }
  
  /**
   * Try to add a subprogram
   * @return Optionally return the newly added subprogram
   */
  def addSubProgram(
      idName: String, params: List[TypedIdentifier]): Option[SubProgram] = {
    
    // first, check if we are allowed to add a procedure
    // aka, is there already an identical one in scope?
    
    if (isSubProgramInScope(idName, params.map(_.idType))) {
      None
    } else {
      // now, insert the new subprogram
      val subProgram = new SubProgram(idName, params, Some(this))
      subPrograms += subProgram
      Some(subProgram)
    }
  }
  
  /**
   * Recursively check if a variable is in scope
   */
  @tailrec
  final def findVariable(idName: String): Option[TypedIdentifier] = {
    // first check to see if there is a matching parameter
    getParam(idName) match {
      case p: Some[TypedIdentifier] => p // done
      case _ => {
        // next try to see if this scope
        // contains an appropriately named variable
        getVariable(idName) match {
          case m: Some[TypedIdentifier] => m // if so, we're done
          case _ => {
            parent match { // otherwise, check if we have a parent
              case Some(p) => {
                // if so, have it do the same thing we're doing now
                p.findVariable(idName)
              }
              case _ => None // if no more parents, we're done
            }
          }
        }
      }
    }
  }
  
  /**
   * Recursively check if a subprogram is in scope
   */
  @tailrec
  private def findSubProgram(
      idName: String,
      params: List[Type]): Option[SubProgram] = {
    // first check for recursive calls
    if (idName == this.idName && paramsEqual(params)) {
      Some(this)
    } else {
      // next try to see if this scope contains an appropriately named subprogram
      getSubProgram(idName, params) match {
        case m: Some[SubProgram] => m // if so, we're done
        case _ => {
          parent match { // otherwise, check if we have a parent
            case Some(p) => { // if so, have it do the same thing we're doing now
              p.findSubProgram(idName, params)
            }
            case _ => None // if no more parents, we're done
          }
        }
      }
    }
  }
  
  def isSubProgramInScope( idName: String, params: List[Type]) = {
    findSubProgram(idName, params).isDefined
  }
  
  def canEqual(other: Any): Boolean = {
    other.isInstanceOf[SubProgram]
  }
  
  /**
   * Subprograms are considered to be equal if their names and parameter lists
   * are equal.
   */
  override def equals(other: Any): Boolean = {
    other match {
      case other: SubProgram => {
        (other canEqual this) && ((other eq this) || (
            other.idName == idName
            && paramsEqual(other.params)
        ))
      }
      case _ => false
    }
  }
  
  override def hashCode(): Int = {
    Util.hash(idName, params)
  }
  
  override def toString(): String = {
    s"$idName(${params.mkString(", ")})"
  }
  
}
