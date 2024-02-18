package de.sortsys.api.ql

interface ASTVisitor<T> {
    fun visit(node: ASTNode): T
    fun visitQueryProg(node: QueryProg): T
    fun visitScriptProg(node: ScriptProg): T
    fun visitSelect(node: Select): T
    fun visitColumn(node: Column): T
    fun visitTable(node: Table): T
    fun visitJoin(node: Join): T
    fun visitOrderBy(node: OrderBy): T
    fun visitUnaryExpr(node: UnaryExpr): T
    fun visitBinaryExpr(node: BinaryExpr): T
    fun visitValue(node: Value): T
    fun visitIdentifier(node: Identifier): T
    fun visitJsonObject(node: JsonObject): T
    fun visitJsonArray(node: JsonArray): T
    fun visitCallExpr(node: CallExpr): T
}

abstract class BaseASTVisitor<T> : ASTVisitor<T> {
    override fun visit(node: ASTNode): T =
        when (node) {
            is QueryProg -> visitQueryProg(node)
            is ScriptProg -> visitScriptProg(node)
            is Select -> visitSelect(node)
            is Column -> visitColumn(node)
            is Table -> visitTable(node)
            is Join -> visitJoin(node)
            is OrderBy -> visitOrderBy(node)
            is UnaryExpr -> visitUnaryExpr(node)
            is BinaryExpr -> visitBinaryExpr(node)
            is Value -> visitValue(node)
            is Identifier -> visitIdentifier(node)
            is JsonObject -> visitJsonObject(node)
            is JsonArray -> visitJsonArray(node)
            is CallExpr -> visitCallExpr(node)

            else -> throw NotImplementedError()
        }
}


abstract class ASTNode {
    fun optimize() {}
}

abstract class Prog : ASTNode()

data class QueryProg(val query: Query) : Prog()

data class ScriptProg(val stmts: Array<Query>) : Prog()

abstract class Expr : ASTNode()

abstract class Query : Expr()

data class Select(
    val columns: Array<Column>,
    val distinctOn: Array<Expr> = arrayOf(),
    val from: Table? = null,
    val joins: Array<Join> = arrayOf(),
    val where: Expr? = null,
    val groupBy: Array<Expr> = arrayOf(),
    val having: Expr? = null,
    val limit: Expr? = null,
    val offset: Expr? = null,
    val orderBy: Array<OrderBy> = arrayOf(),
) : Query()

data class Column(
    val value: Expr,
    val alias: String?
) : ASTNode()

data class Table(
    val source: Expr,
    val alias: String? = null,
) : ASTNode()

data class Join(
    val verbs: Array<String> = arrayOf(),
    val table: Table,
    val where: Expr,
) : ASTNode()

data class OrderBy(
    val value: Expr,
    val order: String,
) : ASTNode()

data class UnaryExpr(
    val op: String,
    val value: Expr,
) : Expr()

data class BinaryExpr(
    val left: Expr,
    val right: Expr,
    val op: String,
) : Expr()

data class Value(val value: Any?) : Expr()

data class Identifier(val name: String) : Expr()

abstract class Json : Expr()

data class JsonObject(val fields: Map<String, Expr>) : Json()

data class JsonArray(val elements: Array<Expr>) : Json()

data class CallExpr(
    val callee: String,
    val args: Array<Expr>,
) : Expr()