object Util {
  private def prime = 41 // prime for use creating aggregate hashcodes
  
  /**
   * Create an aggregate hashcode for several objects
   */
  def hash(objects: Any*): Int = {
    if (objects.size == 0) {
      throw new IllegalArgumentException("Cannot hash zero objects")
    } else if (objects.size == 1) {
      objects.head.hashCode()
    } else {
      var accumulator = objects.head.hashCode() + prime
      objects.tail.foreach { o => 
        accumulator *= prime;
        accumulator += o.hashCode() 
      }
      accumulator
    }
  }
}