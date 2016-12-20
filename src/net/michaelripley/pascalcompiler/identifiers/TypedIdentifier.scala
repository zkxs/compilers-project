package net.michaelripley.pascalcompiler.identifiers

case class TypedIdentifier(val name: String, val idType: Type) {
  override def toString() = {
    s"$name:$idType"
  }
}
