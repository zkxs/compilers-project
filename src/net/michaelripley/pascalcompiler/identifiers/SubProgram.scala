package net.michaelripley.pascalcompiler.identifiers

import scala.annotation.tailrec
import scala.collection.GenSeqLike
import scala.collection.mutable.MutableList
import net.michaelripley.Util

private[identifiers] class SubProgram(
    val idName: String,
    val params: List[Type],
    val parent: Option[SubProgram]) {
  
  private val variables   = MutableList.empty[TypedIdentifier]
  private val subPrograms = MutableList.empty[SubProgram]
  
  def getVariable(idName: String) = {
    variables.find { _.name == idName }
  }
  
  private def hasVariable(idName: String) = {
    getVariable(idName).isDefined
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
    subPrograms.find { s => s.idName == idName && s.params == params }
  }
  
  private def hasSubProgram(idName: String, params: List[Type]) = {
    getSubProgram(idName, params).isDefined
  }
  
  /**
   * Try to add a subprogram
   * @return true if subprogram was added, false otherwise
   */
  def addSubProgram(idName: String, params: List[Type]): Boolean = {
    
    // first, check if we are allowed to add a procedure
    // aka, is there already an identical one in scope?
    
    if (isSubProgramInScope(idName, params)) {
      false
    } else {
      // now, insert the new subprogram
      subPrograms += new SubProgram(idName, params, Some(this))
      true
    }
  }
  
  /**
   * Recursively check if a subprogram is in scope
   */
  @tailrec
  private def findSubProgram(
      idName: String,
      params: List[Type]): Option[SubProgram] = {
    // first try to see if this scope contains an appropriately named subprogram
    getSubProgram(idName, params) match {
      case m: Some[_] => m // if so, we're done
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
            && other.params == params
        ))
      }
      case _ => false
    }
  }
  
  override def hashCode(): Int = {
    Util.hash(idName, params)
  }
  
}
