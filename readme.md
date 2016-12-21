# Compiler Project

This is the front-end of a compiler made for Dr. Shenoi's CS-4013-01: Compiler Construction course at the University of Tulsa.  It identifies errors in source files written in a variant of Pascal via the use of a recursive descent parser.

## How do I run it?
Go grab a copy of `compiler.jar` from the [latest release](https://github.com/zkxs/compilers-project/releases/latest).  You'll need a Java 7 or higher runtime, as well as a v2.11 or higher [scala-library.jar](https://mvnrepository.com/artifact/org.scala-lang/scala-library/2.11.8).  Simply place `compiler.jar` and `scala-library.jar` in the same folder, then invoke the compiler with

    java -jar compiler.jar 

Valid parameters are either a list of source files, or a lone `--version` flag.

The compiler will output three files for each input pascal source.

- A `filename.listing` file that includes any lexical, syntax, or semantic errors found
- A `filename.tokens` file listing all tokens output by the lexer
- A `filename.locations` file listing the type and memory offset of all variables

Note that nothing being written to standard out is expected behavior.
