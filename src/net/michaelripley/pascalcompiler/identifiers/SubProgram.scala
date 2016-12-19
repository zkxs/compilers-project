package net.michaelripley.pascalcompiler.identifiers

import scala.annotation.tailrec
import scala.collection.GenSeqLike
import scala.collection.mutable.{MutableList, Set}
import net.michaelripley.Util
import net.michaelripley.pascalcompiler.identifiers.IdentifierManager._

private[identifiers] class SubProgram(
    val idName: String,
    val parent: Option[SubProgram]) {
  
  private val params      = MutableList.empty[TypedIdentifier]
  private val variables   = MutableList.empty[TypedIdentifier]
  private val subPrograms = MutableList.empty[SubProgram]
  
  
  def addParam(id: TypedIdentifier) {
    
  }
  
  def getVariable(idName: String) = {
    variables.find { _.name == idName }
  }
  
  def hasVariable(idName: String) = {
    getVariable(idName).isDefined
  }
  
  def getSubProgram(idName: String, params: MutableList[TypedIdentifier]) = {
    subPrograms.find { s => s.idName == idName && s.params == params }
  }
  
  def hasSubProgram(idName: String, params: MutableList[TypedIdentifier]) = {
    getSubProgram(idName, params).isDefined
  }
  
  /**
   * Recursively check if a subprogram is in scope
   */
  @tailrec
  final def findSubProgram(
      idName: String,
      params: MutableList[TypedIdentifier]): Option[SubProgram] = {
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
  
  def isSubProgramInScope(
      idName: String,
      params: MutableList[TypedIdentifier]) = {
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
