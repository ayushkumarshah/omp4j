package org.omp4j.test

import java.io.File
import org.scalatest._

import org.antlr.v4.runtime.atn._
import org.antlr.v4.runtime.tree._
import org.antlr.v4.runtime._

import org.omp4j.tree._
import org.omp4j.grammar._
import org.omp4j.exception._
import org.omp4j.preprocessor._
import org.omp4j.extractor.Inheritor

/** LoadedContext with TranslationListener */
class InheritorLoadedContext(path: String) extends AbstractLoadedContext(path) {

	val ompFile = new OMPFile(t, parser)

	/** Variable string in format: "<type> <identifier>" e.g. "int ok1" etc. */
	private def varAsText(v: OMPVariable) = s"${v.varType} ${v.name}"

	/** Return set of possible inherited local variables as formated strings */
	def localsAsText = Inheritor.getPossiblyInheritedLocals(directives.head.ctx).map(varAsText)

	/** Return set of possible inherited params as formated strings */
	def paramsAsText = Inheritor.getPossiblyInheritedParams(directives.head.ctx).map(varAsText)

	/** Return size of parent-list */
	def getParentListSize = Inheritor.getParentList(directives.head.ctx).size

	/** Return set of possible inherited local classes as formated strings */
	def localClassesAsText = Inheritor.getVisibleLocalClasses(directives.head.ctx, ompFile).map(_.name)

	/** Return set of directly visible non-local classes */
	def nonlocalClassesAsText = Inheritor.getVisibleNonLocalClasses(directives.head.ctx, ompFile).map(_.name)
}

/** Unit test for Iheritor */
class InheritorSpec extends AbstractSpec {

	// check inherited vars in block after first (and the only) directive
	(new InheritorLoadedContext("/inheritedLocals/01.java")).localsAsText should contain only ("int ok1", "int ok2", "int ok3")
	(new InheritorLoadedContext("/inheritedLocals/02.java")).localsAsText should contain only ("int ok1", "int ok2", "int ok3", "String ok4", "int ok5", "float ok6", "int ok7")
	(new InheritorLoadedContext("/inheritedLocals/03.java")).localsAsText should contain only ("int ok1", "int ok2")
	(new InheritorLoadedContext("/inheritedLocals/04.java")).localsAsText should contain only ("int ok1", "int ok2")
	(new InheritorLoadedContext("/inheritedLocals/05.java")).localsAsText should contain only ("int ok1", "int ok2", "JButton button")
	(new InheritorLoadedContext("/inheritedLocals/06.java")).localsAsText should contain only ("int x")

	// check method params
	(new InheritorLoadedContext("/inheritedParams/01.java")).paramsAsText should contain only ("String[] args", "int a", "String b", "float c")

	// check parentlist size
	(new InheritorLoadedContext("/parentListSize/01.java")).getParentListSize should equal (33)

	// check local classes visibility
	// local and inner in local
	(new InheritorLoadedContext("/visibleLocalClasses/01.java")).localClassesAsText should contain only ("A", "P", "B", "C", "D", "E", "F", "G")
	(new InheritorLoadedContext("/visibleLocalClasses/01.java")).nonlocalClassesAsText should contain only ("H", "I", "J", "K", "L", "M", "N", "O")
	(new InheritorLoadedContext("/visibleLocalClasses/02.java")).nonlocalClassesAsText should contain allOf ("java", "notjava")
}
