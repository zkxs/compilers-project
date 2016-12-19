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
  
  def getVariable(idName: String) = {
    variables.find { _.name == idName }
  }
  
  def hasVariable(idName: String) = {
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
  
  def getSubProgram(idName: String, params: List[TypedIdentifier]) = {
    subPrograms.find { s => s.idName == idName && s.params == params }
  }
  
  def hasSubProgram(idName: String, params: List[TypedIdentifier]) = {
    getSubProgram(idName, params).isDefined
  }
  
  def addSubProgram(idName: String, params: List[TypedIdentifier]): Unit = {
    
  }
  
  /**
   * Recursively check if a subprogram is in scope
   */
  @tailrec
  final def findSubProgram(
      idName: String,
      params: List[TypedIdentifier]): Option[SubProgram] = {
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
  
  def isSubProgramInScope( idName: String, params: List[TypedIdentifier]) = {
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
