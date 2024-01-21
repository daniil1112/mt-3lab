package ru.dfrolovd

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream

private fun getLexer(source: String): JavaLexer {
    return JavaLexer(CharStreams.fromString(source))
}

private fun getParser(lexer: JavaLexer): JavaParser {
    return JavaParser(CommonTokenStream(lexer))
}

fun parse(source: String): String {
    val parser = getParser(getLexer(source))
    parser.addErrorListener(VerboseErrorListener())
    val visitor = Visitor()
    val sb = StringBuilder()
    visitor.visitFile(sb, parser.file())
    return sb.toString()
}