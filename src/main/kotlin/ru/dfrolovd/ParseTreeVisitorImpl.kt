package ru.dfrolovd

import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor
import org.antlr.v4.runtime.tree.TerminalNode

class ParseTreeVisitorImpl : AbstractParseTreeVisitor<String>() {

    override fun defaultResult(): String {
        return ""
    }

    override fun aggregateResult(aggregate: String?, nextResult: String?): String {
        val aggregateVal = aggregate ?: defaultResult()
        val nextVal = nextResult ?: defaultResult()
        return aggregateVal + nextVal
    }

    override fun visitTerminal(node: TerminalNode?): String {
        return node?.toStringTree() ?: defaultResult()
    }
}