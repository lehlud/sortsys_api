package de.sortsys.api.ql

import de.sortsys.api.ql.generated.QLBaseVisitor
import de.sortsys.api.ql.generated.QLParser
import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.tree.ParseTree
import com.google.gson.Gson
import kotlin.Pair

@Suppress("UNCHECKED_CAST")
class DefaultParseTreeVisitor : QLBaseVisitor<Any?>() {

    private fun <T> safeVisit(ctx: ParseTree): T {
        return this.visit(ctx) as T
    }

    private fun <T> safeVisit(ctx: ParseTree?, default: T): T {
        if (ctx == null) return default
        return safeVisit(ctx)
    }

    override fun visitProg(ctx: QLParser.ProgContext): Prog {
        if (ctx.query() != null) {
            return QueryProg(visitQuery(ctx.query()))
        }

        if (ctx.stmts() != null) {
            return ScriptProg(visitStmts(ctx.stmts()))
        }

        throw NotImplementedError()
    }

    override fun visitStmts(ctx: QLParser.StmtsContext): Array<Query> {
        var arr = arrayOf(visitStmt(ctx.stmt()))

        if (ctx.stmts() != null) {
            arr = arrayOf(*visitStmts(ctx.stmts()), *arr)
        }

        return arr
    }

    override fun visitStmt(ctx: QLParser.StmtContext): Query {
        return visitQuery(ctx.query())
    }

    override fun visitQuery(ctx: QLParser.QueryContext): Query {
        return safeVisit(ctx.getChild(0)) as Query
    }

    override fun visitSelect(ctx: QLParser.SelectContext): Select {
        return Select(
            columns = visitColumns(ctx.columns()),
            distinctOn = safeVisit(ctx.distinct(), arrayOf()),
            from = safeVisit<Table?>(ctx.from(), null),
            joins = ctx.join().map { j -> visitJoin(j) }.toTypedArray(),
            where = safeVisit<Expr?>(ctx.where(), null),
            groupBy = safeVisit(ctx.groupBy(), arrayOf()),
            having = safeVisit<Expr?>(ctx.having(), null),
            limit = safeVisit<Expr?>(ctx.limit(), null),
            offset = safeVisit<Expr?>(ctx.offset(), null),
            orderBy = safeVisit(ctx.orderBy(), arrayOf())
        )
    }

    override fun visitColumns(ctx: QLParser.ColumnsContext): Array<Column> {
        var arr = arrayOf(visitColumn(ctx.column()))

        if (ctx.columns() != null) {
            arr = arrayOf(*visitColumns(ctx.columns()), *arr)
        }

        return arr
    }

    override fun visitColumn(ctx: QLParser.ColumnContext): Column {
        return Column(
            value = visitExpr(ctx.expr()),
            alias = ctx.word()?.text
        )
    }

    override fun visitDistinct(ctx: QLParser.DistinctContext): Array<Expr> {
        return visitExprs(ctx.exprs())
    }

    override fun visitFrom(ctx: QLParser.FromContext): Table {
        return visitTable(ctx.table())
    }

    override fun visitJoin(ctx: QLParser.JoinContext): Join {
        var verbs = arrayOf<String>()

        if (ctx.FULL() != null) verbs = arrayOf(*verbs, "FULL")
        if (ctx.LEFT() != null) verbs = arrayOf(*verbs, "LEFT")
        if (ctx.RIGHT() != null) verbs = arrayOf(*verbs, "RIGHT")
        if (ctx.INNER() != null) verbs = arrayOf(*verbs, "INNER")
        if (ctx.OUTER() != null) verbs = arrayOf(*verbs, "OUTER")

        return Join(
            verbs = verbs,
            table = visitTable(ctx.table()),
            where = visitExpr(ctx.expr()),
        )
    }

    override fun visitWhere(ctx: QLParser.WhereContext): Expr {
        return visitExpr(ctx.expr())
    }

    override fun visitGroupBy(ctx: QLParser.GroupByContext): Array<Expr> {
        return visitExprs(ctx.exprs())
    }

    override fun visitHaving(ctx: QLParser.HavingContext): Expr {
        return visitExpr(ctx.expr())
    }

    override fun visitLimit(ctx: QLParser.LimitContext): Expr {
        return visitExpr(ctx.expr())
    }

    override fun visitOffset(ctx: QLParser.OffsetContext): Expr {
        return visitExpr(ctx.expr())
    }

    override fun visitOrderBy(ctx: QLParser.OrderByContext): Array<OrderBy> {
        return visitOrders(ctx.orders())
    }

    override fun visitOrders(ctx: QLParser.OrdersContext): Array<OrderBy> {
        var arr = arrayOf(visitOrder(ctx.order()))

        if (ctx.orders() != null) {
            arr = arrayOf(*visitOrders(ctx.orders()), *arr)
        }

        return arr
    }

    override fun visitOrder(ctx: QLParser.OrderContext): OrderBy {
        return OrderBy(
            value = visitExpr(ctx.expr()),
            order = ctx.getChild(1).text,
        )
    }


    override fun visitTable(ctx: QLParser.TableContext): Table {
        val expr = if (ctx.expr() != null) visitExpr(ctx.expr())
        else if (ctx.query() != null) visitQuery(ctx.query())
        else throw NotImplementedError()

        val alias = ctx.word()?.text
        return Table(expr, alias)
    }

    override fun visitExprs(ctx: QLParser.ExprsContext): Array<Expr> {
        var arr = arrayOf(visitExpr(ctx.expr()))

        if (ctx.exprs() != null) {
            arr = arrayOf(*visitExprs(ctx.exprs()), *arr)
        }

        return arr
    }

    override fun visitExpr(ctx: QLParser.ExprContext): Expr {
        return safeVisit(ctx.getChild(0))
    }

    override fun visitTerm(ctx: QLParser.TermContext): Expr {
        return safeVisit(ctx.getChild(0))
    }

    private fun visitBinExpr(ctx: ParserRuleContext): Expr {
        if (ctx.childCount != 3) return safeVisit(ctx.getChild(0)) as Expr

        return BinaryExpr(
            left = safeVisit(ctx.getChild(0)) as Expr,
            right = safeVisit(ctx.getChild(2)) as Expr,
            op = ctx.getChild(1).text,
        )
    }

    override fun visitTerm6(ctx: QLParser.Term6Context): Expr {
        return visitBinExpr(ctx)
    }

    override fun visitTerm5(ctx: QLParser.Term5Context): Expr {
        return visitBinExpr(ctx)
    }

    override fun visitTerm4(ctx: QLParser.Term4Context): Expr {
        return visitBinExpr(ctx)
    }

    override fun visitTerm3(ctx: QLParser.Term3Context): Expr {
        return visitBinExpr(ctx)
    }

    override fun visitTerm2(ctx: QLParser.Term2Context): Expr {
        return visitBinExpr(ctx)
    }

    override fun visitTerm1(ctx: QLParser.Term1Context): Expr {
        return visitBinExpr(ctx)
    }

    override fun visitTerm0(ctx: QLParser.Term0Context): Expr {
        if (ctx.call() != null) return visitCall(ctx.call())

        if (ctx.literal() != null) return visitLiteral(ctx.literal())

        if (ctx.expr() != null) return visitExpr(ctx.expr())

        if (ctx.op != null) return UnaryExpr(
            op = ctx.op.text,
            value = safeVisit(ctx.term0()) as Expr,
        )

        throw NotImplementedError()
    }

    override fun visitLiteral(ctx: QLParser.LiteralContext): Expr {
        if (ctx.str() != null) return Value(visitStr(ctx.str()))

        if (ctx.json() != null) return visitJson(ctx.json())

        if (ctx.NUM() != null) {
            val int = ctx.NUM().text.toIntOrNull();
            if (int != null) return Value(int)

            return Value(ctx.NUM().text.toDouble())
        }

        if (ctx.NULL() != null) return Value(null)

        if (ctx.BOOL() != null) {
            return Value(ctx.BOOL().text.lowercase() == "true")
        }

        if (ctx.identifier() != null) {
            return Identifier(ctx.identifier().text)
        }

        throw NotImplementedError()
    }

    override fun visitStr(ctx: QLParser.StrContext): String {
        return Gson().fromJson(ctx.STR().text, String::class.java)
    }

    override fun visitJson(ctx: QLParser.JsonContext): Json {
        return safeVisit(ctx.getChild(0)) as Json
    }

    override fun visitJsonArray(ctx: QLParser.JsonArrayContext): JsonArray {
        return JsonArray(
            safeVisit(ctx.exprs(), arrayOf())
        )
    }

    override fun visitJsonObject(ctx: QLParser.JsonObjectContext): JsonObject {
        return JsonObject(
            safeVisit(ctx.jsonProps(), mapOf())
        )
    }

    override fun visitJsonProps(ctx: QLParser.JsonPropsContext): Map<String, Expr> {
        var map = mapOf(visitJsonProp(ctx.jsonProp()))

        if (ctx.jsonProps() != null) {
            map = visitJsonProps(ctx.jsonProps()) + map
        }

        return map
    }

    override fun visitJsonProp(ctx: QLParser.JsonPropContext): Pair<String, Expr> {
        return Pair(visitStr(ctx.str()), visitExpr(ctx.expr()))
    }

    override fun visitCall(ctx: QLParser.CallContext): CallExpr {
        var args: Array<Expr> = arrayOf()

        if (ctx.query() != null) {
            args = arrayOf(visitQuery(ctx.query()))
        }

        if (ctx.exprs() != null) {
            args = visitExprs(ctx.exprs())
        }

        return CallExpr(
            callee = ctx.word().text,
            args = args,
        )
    }
}