package de.sortsys.api.ql

import de.sortsys.api.ql.generated.QLLexer
import de.sortsys.api.ql.generated.QLParser
import org.antlr.v4.runtime.*
import org.antlr.v4.runtime.atn.ATNConfigSet
import org.antlr.v4.runtime.dfa.DFA
import java.lang.Exception
import java.util.*

interface Parser {
    fun parse(input: String): Prog
}

class ParserException(msg: String) : Exception(msg)

class DefaultQLParser : Parser {
    override fun parse(input: String): Prog {
        val stream = CharStreams.fromString(input)
        val lexer = QLLexer(stream)
        val tokens = CommonTokenStream(lexer)

        var error: String? = null
        val errorListener = object : BaseErrorListener() {
            override fun syntaxError(
                recognizer: Recognizer<*, *>?,
                offendingSymbol: Any?,
                line: Int,
                charPositionInLine: Int,
                msg: String?,
                e: RecognitionException?
            ) {
                error = "line $line:$charPositionInLine syntax error"
            }
        }

        val parser = QLParser(tokens)
        parser.removeErrorListeners()
        parser.addErrorListener(errorListener)

        val tree = parser.prog()
        if (error != null) throw ParserException(error!!)

        val prog = DefaultParseTreeVisitor().visitProg(tree)

        return prog
    }

}