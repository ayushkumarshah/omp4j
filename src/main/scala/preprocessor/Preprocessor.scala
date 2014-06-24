package org.omp4j.preprocessor

import org.antlr.v4.runtime.atn._
import org.antlr.v4.runtime.tree._
import org.antlr.v4.runtime._

import java.io._
import org.omp4j.preprocessor.grammar._
import org.omp4j.preprocessor.exception._

/** Class representing the preprocessor itself.
  * @constructor Create preprocessor for files given.
  * @param fileStrs Relative paths to files to be parsed.
  */
class Preprocessor(filesStrs: Array[String]) {

	/** Start parsing file by file
	  * @throws IllegalArgumentException when non-existing file is passed.
	  * @throws ParseException when some parse error occures
	  */
	def run() = {
		val files = for (fs <- filesStrs; f = new File(fs)) yield f
		files.map{ f => if (!f.exists())
			throw new IllegalArgumentException("File '" + f.getPath() + "' does not exist")
		}

		for (f <- files) {
			try parseFile(f)
			catch {
				case e: Exception => throw new ParseException("From file '" + f.getPath() + "'", e)
			}
		}
	}

	/** Parse one particular file.
	  * @param file Valid source file to be parsed
	  * @throws ParseException TODO
	  */
	private def parseFile(file: File) = {
		val lexer = new Java8Lexer(new ANTLRFileStream(file.getPath()))
		val tokens = new CommonTokenStream(lexer)
		val parser = new Java8Parser(tokens)
		val t: Java8Parser.CompilationUnitContext = parser.compilationUnit()

		// t.inspect(parser);	// display gui tree

		val ompFile = new OMPFile(t, parser)
		val directives = (new DirectiveVisitor(tokens, parser)).visit(t)

		// directives.foreach(d => println(d + "\n"))

		val translator = new Translator(directives, tokens, t, ompFile)
		println(translator.translate())
	}
}