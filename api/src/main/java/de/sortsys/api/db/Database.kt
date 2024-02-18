package de.sortsys.api.db

import de.sortsys.api.ql.ASTNode

abstract class SQL {
    abstract fun sqlText(): String
}

data class SQLScript(
    val parts: Array<SQLPart>
) : SQL() {
    override fun sqlText(): String {
        return parts.joinToString(";\n") { it.sqlText() }
    }

}

data class SQLPart(
    val sql: String,
    val args: Array<Any?> = arrayOf(),
) : SQL() {
    override fun sqlText(): String = sql
}

data class Row(val columns: Array<Column>) {
    fun toArray() = columns.map { it.toArray() }.toTypedArray()
}

data class Column(val name: String, val value: Any?) {
    fun toArray() = arrayOf(name, value)
}

interface SQLGenerator {
    fun generate(node: ASTNode): SQL
}

interface SQLDatabase {
    fun parseQL(query: String): SQL

    fun queryQL(query: String): Array<Row> {
        return querySQL(parseQL(query))
    }

    fun querySQL(sql: SQL): Array<Row>
}