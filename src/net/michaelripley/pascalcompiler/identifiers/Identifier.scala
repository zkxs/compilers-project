package net.michaelripley.pascalcompiler.identifiers

import java.util.concurrent.atomic.AtomicInteger

private object Identifier {
  
  private val nextNumber = new AtomicInteger()
  
  private def getUniqueNumber(): Int = {
    nextNumber.getAndIncrement()
  }
  
}

class Identifier(val name: String) {
  val number = Identifier.getUniqueNumber()
}
