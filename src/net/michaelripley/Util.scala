package net.michaelripley

object Util {
  private def prime = 41 // prime for use creating aggregate hashcodes
  
  /**
   * Create an aggregate hashcode for several objects
   */
  def hash(objects: Any*): Int = {
    if (objects.size == 0) {
      throw new IllegalArgumentException("Cannot hash zero objects")
    } else if (objects.size == 1) {
      hashAny(objects.head)
    } else {
      var accumulator = hashAny(objects.head) + prime
      objects.tail.foreach { o => 
        accumulator *= prime;
        accumulator += hashAny(o)
      }
      accumulator
    }
  }
  
  /**
   * Allows us to pull out certain types we don't like the default hashing for and screw with them
   */
  private def hashAny(thing: Any): Int = {
    thing match {
      case optional: Option[Any] => hashOption(optional)
      case anything => anything.hashCode()
    }
  }
  
  /**
   * Manually hash Options by extracting the thing if present, otherwise 0
   */
  private def hashOption(o: Option[Any]): Int = {
    o match {
      case Some(something) => hashAny(something)
      case None => 0
    }
  }
}