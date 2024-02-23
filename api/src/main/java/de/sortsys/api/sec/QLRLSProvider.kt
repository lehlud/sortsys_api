package de.sortsys.api.sec

import de.sortsys.api.ql.*

/**
 * QL Row-Level-Security Provider
 */
interface QLRLSProvider {
    fun ensure(prog: Prog): Prog {
        return when(prog) {
            is QueryProg -> prog.copy(query = ensure(prog.query))
            is ScriptProg -> prog.copy(stmts = prog.stmts.map { ensure(it) }.toTypedArray())
            else -> throw NotImplementedError()
        }
    }

    fun ensure(query: Query): Query {
        return when(query) {
            is Select -> query.copy(
                from = if (query.from != null) ensure(query.from) else null
            )

            else -> throw NotImplementedError()
        }
    }

    fun ensure(table: Table): Table {
        return Table(
            source = TODO(),
            alias = table.alias,
        )
    }
}
