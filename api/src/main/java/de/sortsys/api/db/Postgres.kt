package de.sortsys.api.db

import de.sortsys.api.ql.*
import java.lang.Exception
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

class PostgresDB(
    private val pool: SQLConnectionPool
) : SQLDatabase {
    override fun parseQL(query: String): SQL {
        val prog = DefaultQLParser().parse(query)
        return PostgresSQLGenerator().generate(prog)
    }

    override fun querySQL(sql: SQL): Array<Row> {
        return useConn { conn ->
            conn.autoCommit = true
            conn.autoCommit = false
            conn.beginRequest()

            var stmt: PreparedStatement? = null

            if (sql is SQLScript) {
                for (part in sql.parts) {
                    stmt = prepare(conn, part)
                    stmt.execute()
                }
            }

            if (sql is SQLPart) {
                stmt = prepare(conn, sql)
                stmt.execute()
            }

            conn.commit()

            if (stmt != null) getRows(stmt.resultSet) else arrayOf<Row>()
        }
    }

    private fun <T> useConn(handler: ((Connection) -> T)): T {
        val conn = pool.acquire()

        val res: T
        try {
            res = handler(conn)
        } finally {
            pool.release(conn)
        }

        return res
    }

    private fun getRows(rs: ResultSet): Array<Row> {
        val rows = ArrayList<Row>()

        while (rs.next()) {
            val columns = ArrayList<Column>()

            for (column in 1..rs.metaData.columnCount) {
                val columnName = rs.metaData.getColumnName(column)
                columns.add(Column(columnName, rs.getObject(column)))
            }

            rows.add(Row(columns.toTypedArray()))
        }

        return rows.toTypedArray()
    }

    private fun prepare(conn: Connection, sql: SQLPart): PreparedStatement {
        val stmt = conn.prepareStatement(sql.sql)
        sql.args.forEachIndexed { index, arg ->
            stmt.setObject(index + 1, arg)
        }

        return stmt
    }
}

class PostgresSQLGenerator : BaseASTVisitor<SQL>(), SQLGenerator {
    override fun generate(node: ASTNode): SQL {
        return visit(node)
    }

    private fun visitPart(node: ASTNode): SQLPart {
        return visit(node) as SQLPart
    }

    private fun <T : ASTNode> arraySQL(array: Array<T>): SQLPart {
        val gen = array.map { visitPart(it) }
        val sql = gen.joinToString(", ") { it.sql }
        val args = gen.flatMap { it.args.asIterable() }.toTypedArray()

        return SQLPart(sql, args)
    }

    override fun visitQueryProg(node: QueryProg): SQLPart {
        return visitPart(node.query)
    }

    override fun visitScriptProg(node: ScriptProg): SQLScript {
        return SQLScript(
            node.stmts.map { visitPart(it) }.toTypedArray()
        )
    }

    override fun visitSelect(node: Select): SQLPart {
        var sql = "SELECT"
        var args = arrayOf<Any?>()

        if (node.distinctOn.isNotEmpty()) {
            val gen = arraySQL(node.distinctOn)
            sql += " DISTINCT ON (${gen.sql})"
            args = arrayOf(*args, *gen.args)
        }
        if (node.columns.isNotEmpty()) {
            val gen = arraySQL(node.columns)
            sql += " ${gen.sql}"
            args = arrayOf(*args, *gen.args)
        }

        if (node.from != null) {
            val gen = visitPart(node.from)
            sql += " FROM ${gen.sql}"
            args = arrayOf(*args, *gen.args)
        }

        node.joins.forEach {
            val gen = visitPart(it)
            sql += " ${gen.sql}"
            args = arrayOf(*args, *gen.args)
        }

        if (node.where != null) {
            val gen = visitPart(node.where)
            sql += " WHERE ${gen.sql}"
            args = arrayOf(*args, *gen.args)
        }

        if (node.groupBy.isNotEmpty()) {
            val gen = arraySQL(node.groupBy)
            sql += " GROUP BY ${gen.sql}"
            args = arrayOf(*args, *gen.args)
        }

        if (node.limit != null) {
            val gen = visitPart(node.limit)
            sql += " LIMIT ${gen.sql}"
            args = arrayOf(*args, *gen.args)
        }

        if (node.offset != null) {
            val gen = visitPart(node.offset)
            sql += " OFFSET ${gen.sql}"
            args = arrayOf(*args, *gen.args)
        }

        if (node.orderBy.isNotEmpty()) {
            val gen = arraySQL(node.orderBy)
            sql += " ORDER BY ${gen.sql}"
            args = arrayOf(*args, *gen.args)
        }

        return SQLPart(sql, args)
    }

    override fun visitColumn(node: de.sortsys.api.ql.Column): SQLPart {
        val alias = if (node.alias != null) {
            " AS \"${node.alias}\""
        } else ""

        val gen = visitPart(node.value)

        return SQLPart(
            sql = "${gen.sql}$alias",
            args = gen.args,
        )
    }

    override fun visitTable(node: Table): SQLPart {
        val alias = if (node.alias != null) {
            " AS \"${node.alias}\""
        } else ""

        val source = visitPart(node.source)
        val sourceSQL = if (node.source is Query) {
            "(${source.sql})"
        } else source.sql

        return SQLPart(
            sql = "$sourceSQL$alias",
            args = source.args
        )
    }

    override fun visitJoin(node: Join): SQLPart {
        var verbs = node.verbs.joinToString(" ")
        if (verbs.isNotEmpty()) verbs = "$verbs "

        val table = visitPart(node.table)
        val where = visitPart(node.where)

        return SQLPart(
            sql = "${verbs}JOIN ${table.sql} ON ${where.sql}",
            args = arrayOf(*table.args, *where.args),
        )
    }

    override fun visitOrderBy(node: OrderBy): SQL {
        val gen = visitPart(node.value)
        return SQLPart(
            sql = "${gen.sql} ${node.order.uppercase()}",
            args = gen.args,
        )
    }

    override fun visitUnaryExpr(node: UnaryExpr): SQLPart {
        val v = visitPart(node.value)

        val op = when (val op = node.op.lowercase()) {
            "+" -> "+"
            "-" -> "-"
            "not" -> "NOT"

            else -> throw NotImplementedError("'$op': Undefined operator")
        }

        return SQLPart("($op ${v.sql})", v.args)
    }

    override fun visitBinaryExpr(node: BinaryExpr): SQLPart {
        var l = visitPart(node.left)
        var r = visitPart(node.right)

        val op = when (val op = node.op.lowercase()) {
            "==" -> "="
            "!=" -> "<>"
            ">" -> ">"
            "<" -> "<"
            ">=" -> ">="
            "<=" -> "<="

            "+" -> "+"
            "-" -> "-"
            "*" -> "*"
            "/" -> "/"
            "%" -> "%"

            "||" -> "||"

            "and" -> "AND"
            "or" -> "OR"

            else -> throw NotImplementedError("'$op': Undefined operator")
        }

        fun default(): SQLPart {
            return SQLPart("${l.sql} $op ${r.sql}", arrayOf(*l.args, *r.args))
        }

        val sql = when (op) {
            "=", "<>" -> {
                var right = node.right
                if (node.left is Value && node.left.value == null) {
                    right = node.left

                    val tmp = l
                    l = r
                    r = tmp
                }

                val suffix = if (op == "=") "NULL" else "NOT NULL"

                if (right is Value && right.value == null) {
                    SQLPart("${l.sql} IS $suffix", l.args)
                } else default()
            }

            else -> default()
        }

        return SQLPart("(${sql.sql})", sql.args)
    }

    override fun visitValue(node: Value): SQLPart {
        if (node.value is Double || node.value is Int) {
            return SQLPart(node.value.toString())
        }

        if (node.value is Boolean) {
            return SQLPart(if (node.value) "TRUE" else "FALSE")
        }

        if (node.value == null) {
            return SQLPart("NULL")
        }

        return SQLPart("?", arrayOf(node.value))
    }

    override fun visitIdentifier(node: Identifier): SQLPart {
        return SQLPart(
            node.name.split('.').joinToString(".") { part ->
                if (part != "*") "\"$part\""
                else "*"
            }
        )
    }

    override fun visitJsonObject(node: JsonObject): SQLPart {
        val argList = ArrayList<Expr>()

        node.fields.entries.forEach {
            argList.add(Value(it.key))
            argList.add(it.value)
        }

        return visitCallExpr(
            CallExpr(
                callee = "jsonobject",
                args = argList.toTypedArray(),
            )
        )
    }

    override fun visitJsonArray(node: JsonArray): SQLPart {
        return visitCallExpr(
            CallExpr(
                callee = "jsonarray",
                args = node.elements,
            )
        )
    }

    override fun visitCallExpr(node: CallExpr): SQLPart {
        val gen = arraySQL(node.args)

        val callee = when (val callee = node.callee.lowercase()) {
            "array" -> "ARRAY"

            "json" -> "to_jsonb"
            "jsonarray" -> "jsonb_build_array"
            "jsonobject" -> "jsonb_build_object"

            "query" -> "to_tsquery"
            "plainquery" -> "plainto_tsquery"

            "unnest" -> "unnest"

            "abs" -> "abs"
            "ceil" -> "ceil"
            "floor" -> "floor"
            "ln" -> "ln"
            "exp" -> "exp"
            "log" -> "log"
            "sign" -> "sign"
            "sqrt" -> "sqrt"
            "power" -> "power"
            "pi" -> "pi"
            "sin" -> "sin"
            "asin" -> "asin"
            "cos" -> "cos"
            "acos" -> "acos"
            "tan" -> "tan"
            "atan" -> "atan"

            else -> throw NotImplementedError("$callee() not implemented")
        }



        return when (callee.lowercase()) {
            "array" -> if (node.args.size == 1 && node.args[0] is Query) {
                SQLPart("ARRAY(${gen.sql})", gen.args)
            } else {
                SQLPart("ARRAY[${gen.sql}]", gen.args)
            }

            else -> SQLPart("$callee(${gen.sql})", gen.args)
        }
    }

}
