package ru.dfrolovd

import ru.dfrolovd.JavaParser.*
import java.util.function.Consumer

class Visitor {
    private val newLine = System.lineSeparator()
    private val tab = "    "
    private val treeVisitor = ParseTreeVisitorImpl()


    fun visitFile(stringBuilder: StringBuilder, context: FileContext) {
        val pkg = context.package_()
        if (pkg != null) {
            visitPackage(stringBuilder, pkg)
            stringBuilder.append(newLine)
        }
        context.importLine().forEach { visitImport(stringBuilder, it) }
        if (context.importLine().isNotEmpty()) {
            stringBuilder.append(newLine)
        }
        visitClass(stringBuilder, context.class_())
    }

    private fun visitPackage(stringBuilder: StringBuilder, context: PackageContext) {
        stringBuilder.append("package ${context.packageName().accept(treeVisitor)};").append(newLine)
    }

    private fun visitImport(stringBuilder: StringBuilder, context: ImportLineContext) {
        stringBuilder.append("import ")
        if (context.STATIC() != null) {
            stringBuilder.append("static ")
        }
        stringBuilder.append("${context.importName().accept(treeVisitor)};").append(newLine)
    }

    private fun visitClass(stringBuilder: StringBuilder, context: ClassContext) {
        context.classModifier().forEach { visitClassModifier(stringBuilder, it) }
        stringBuilder.append("class ")
        visitClassDeclaration(stringBuilder, context.classDeclaration())
    }

    private fun visitClassDeclaration(stringBuilder: StringBuilder, context: ClassDeclarationContext) {
        stringBuilder.append(context.className().accept(treeVisitor)).append(" ").append("{")
        stringBuilder.append(newLine)
        inDepthScope(stringBuilder) { visitClassBody(it, context.classBody()) }
        stringBuilder.append(newLine)
        stringBuilder.append("}")
    }

    private fun visitClassBody(stringBuilder: StringBuilder, context: ClassBodyContext) {
        if (context.classBodyMember().size > 0) {
            visitClassBodyMember(stringBuilder, context.classBodyMember(0))
        }

        context.classBodyMember().drop(1).forEach {
            stringBuilder.append(newLine)
            visitClassBodyMember(stringBuilder, it)
        }
    }

    private fun visitClassBodyMember(stringBuilder: StringBuilder, context: ClassBodyMemberContext) {
        if (context.classField() != null) {
            visitClassField(stringBuilder, context.classField())
        }
        if (context.classMethod() != null) {
            visitClassMethod(stringBuilder, context.classMethod())
        }
    }

    private fun visitClassField(stringBuilder: StringBuilder, context: ClassFieldContext) {
        if (context.modifier() != null) {
            stringBuilder.append(context.modifier().accept(treeVisitor)).append(" ")
        }
        if (context.constant() != null) {
            visitConstant(stringBuilder, context.constant())
            stringBuilder.append(" ")
        }
        visitVariableLine(stringBuilder, context.variableLine())
    }

    private fun visitConstant(stringBuilder: StringBuilder, context: ConstantContext) {
        stringBuilder.append(context.constants().joinToString(" ") { it.accept(treeVisitor) })
    }

    private fun visitVariableLine(stringBuilder: StringBuilder, context: VariableLineContext) {
        visitType(stringBuilder, context.type())
        stringBuilder.append(" ")
        visitVariableName(stringBuilder, context.variableName())
        if (context.expression() != null) {
            stringBuilder.append(" = ")
            visitExpression(stringBuilder, context.expression())
        }
        stringBuilder.append(";")
    }

    private fun visitExpression(stringBuilder: StringBuilder, context: ExpressionContext) {
        if (context.functionExpression() != null) {
            visitFunctionExpression(stringBuilder, context.functionExpression())
        } else if (context.literal() != null) {
            visitLiteral(stringBuilder, context.literal())
        } else if (context.binaryOp() != null) {
            visitExpression(stringBuilder, context.expression(0))
            stringBuilder.append(" ")
            stringBuilder.append(context.binaryOp().accept(treeVisitor))
            stringBuilder.append(" ")
            visitExpression(stringBuilder, context.expression(1))
        } else if (context.prefixUnatyOp() != null) {
            stringBuilder.append(context.prefixUnatyOp().accept(treeVisitor))
            visitExpression(stringBuilder, context.expression(0))
        } else {
            visitExpression(stringBuilder, context.expression(0))
            stringBuilder.append(context.suffixUnaryOp().accept(treeVisitor))
        }
    }

    private fun visitLiteral(stringBuilder: StringBuilder, context: LiteralContext) {
        stringBuilder.append(context.accept(treeVisitor))
    }

    //    : className ('.' functionCall) +
    //    | functionCall ('.' functionCall) +
    private fun visitFunctionExpression(stringBuilder: StringBuilder, context: FunctionExpressionContext) {
        if (context.className() != null) {
            stringBuilder.append(context.className().accept(treeVisitor))
            context.functionCall().forEach {
                stringBuilder.append(".")
                visitFunctionCall(stringBuilder, it)
            }
        } else {
            visitFunctionCall(stringBuilder, context.functionCall(0))
            context.functionCall().drop(1).forEach {
                stringBuilder.append(".")
                visitFunctionCall(stringBuilder, it)
            }
        }
    }

    private fun visitFunctionCall(stringBuilder: StringBuilder, context: FunctionCallContext) {
        stringBuilder.append(context.methodName().accept(treeVisitor))
        stringBuilder.append("(")
        if (context.expression() != null) {
            if (context.expression().size >= 1) {
                visitExpression(stringBuilder, context.expression(0))
                if (context.expression().size > 1) {
                    context.expression().drop(1).forEach {
                        stringBuilder.append(", ")
                        visitExpression(stringBuilder, it)
                    }
                }
            }
        }
        stringBuilder.append(")")
    }

    private fun visitVariableName(stringBuilder: StringBuilder, context: VariableNameContext) {
        stringBuilder.append(context.accept(treeVisitor))
    }

    private fun visitClassMethod(stringBuilder: StringBuilder, context: ClassMethodContext) {
        if (context.modifier() != null) {
            stringBuilder.append(context.modifier().accept(treeVisitor)).append(" ")
        }
        visitType(stringBuilder, context.type())
        stringBuilder.append(" ")
        stringBuilder.append(context.methodName().accept(treeVisitor))
        visitMethodParameters(stringBuilder, context.methodParameters())
        stringBuilder.append(" ")
        visitMethodBody(stringBuilder, context.methodBody())
    }

    private fun visitType(stringBuilder: StringBuilder, context: TypeContext) {
        stringBuilder.append(context.accept(treeVisitor))
    }

    private fun visitMethodParameters(stringBuilder: StringBuilder, context: MethodParametersContext) {
        stringBuilder.append("(")
        if (context.methodParametersDeclaration() != null) {
            visitMethodParametersDeclaration(stringBuilder, context.methodParametersDeclaration())
        }
        stringBuilder.append(")")
    }

    private fun visitMethodBody(stringBuilder: StringBuilder, context: MethodBodyContext) {
        stringBuilder.append("{")
        if (context.instruction() == null || context.instruction().isEmpty()) {
            stringBuilder.append("}")
        } else {
            stringBuilder.append(newLine)
            for (instruction in context.instruction()) {
                inDepthScope(stringBuilder) { visitInstruction(it, instruction) }
                stringBuilder.append(newLine)
            }

            stringBuilder.append("}")
        }

    }

    private fun visitInstruction(stringBuilder: StringBuilder, context: InstructionContext) {
        if (context.variableLine() != null) {
            visitVariableLine(stringBuilder, context.variableLine())
        } else {
            visitStatement(stringBuilder, context.statement())
        }
    }

    private fun visitStatement(stringBuilder: StringBuilder, context: StatementContext) {
        if (context.return_() != null) {
            visitReturn(stringBuilder, context.return_())
        } else if (context.if_() != null) {
            visitIfExpression(stringBuilder, context.if_())
        } else if (context.while_() != null) {
            visitWhileExpression(stringBuilder, context.while_())
        } else if (context.for_() != null) {
            visitForExpression(stringBuilder, context.for_())
        } else {
            visitExpression(stringBuilder, context.expression())
            stringBuilder.append(";")
        }
    }

    private fun visitReturn(stringBuilder: StringBuilder, context: ReturnContext) {
        stringBuilder.append("return")
        if (context.expression() != null) {
            stringBuilder.append(" ")
            visitExpression(stringBuilder, context.expression())
        }
        stringBuilder.append(";")
    }

    private fun visitIfExpression(stringBuilder: StringBuilder, context: IfContext) {
        stringBuilder.append("if (")
        visitExpression(stringBuilder, context.expression())
        stringBuilder.append(") ")
        visitMethodBody(stringBuilder, context.methodBody(0))
        if (context.methodBody().size == 2) {
            stringBuilder.append(" else ")
            visitMethodBody(stringBuilder, context.methodBody(1))
        }
    }

    private fun visitForExpression(stringBuilder: StringBuilder, context: ForContext) {
        stringBuilder.append("for (")
        visitVariableLine(stringBuilder, context.variableLine())
        stringBuilder.append(" ")
        visitExpression(stringBuilder, context.expression(0))
        stringBuilder.append("; ")
        visitExpression(stringBuilder, context.expression(1))
        stringBuilder.append(") ")
        visitMethodBody(stringBuilder, context.methodBody())
    }

    private fun visitWhileExpression(stringBuilder: StringBuilder, context: WhileContext) {
        stringBuilder.append("while (")
        visitExpression(stringBuilder, context.expression())
        stringBuilder.append(") ")
        visitMethodBody(stringBuilder, context.methodBody())
        stringBuilder.append(newLine)
    }

    private fun visitMethodParametersDeclaration(
        stringBuilder: StringBuilder,
        context: MethodParametersDeclarationContext
    ) {
        visitType(stringBuilder, context.type())
        stringBuilder.append(" ")
        stringBuilder.append(context.variableName().accept(treeVisitor))
        if (context.methodParametersDeclaration() != null) {
            stringBuilder.append(", ")
            visitMethodParametersDeclaration(stringBuilder, context.methodParametersDeclaration())
        }
    }

    private fun visitClassModifier(stringBuilder: StringBuilder, context: ClassModifierContext) {
        context.children.forEach { stringBuilder.append(it).append(" ") }
    }

    private fun inDepthScope(stringBuilder: StringBuilder, collector: Consumer<StringBuilder>) {
        val scopeBuilder = StringBuilder()
        collector.accept(scopeBuilder)
        stringBuilder.append(scopeBuilder.lineSequence().map { applyDepth(it) }.joinToString(newLine))
    }

    private fun applyDepth(line: String): String {
        if (line.trimIndent() == "") {
            return line
        }
        return tab + line
    }
}
