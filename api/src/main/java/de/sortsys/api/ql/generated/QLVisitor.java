// Generated from QL.g4 by ANTLR 4.13.1
package de.sortsys.api.ql.generated;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link QLParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface QLVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link QLParser#prog}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProg(QLParser.ProgContext ctx);
	/**
	 * Visit a parse tree produced by {@link QLParser#stmts}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStmts(QLParser.StmtsContext ctx);
	/**
	 * Visit a parse tree produced by {@link QLParser#stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStmt(QLParser.StmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link QLParser#query}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitQuery(QLParser.QueryContext ctx);
	/**
	 * Visit a parse tree produced by {@link QLParser#select}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelect(QLParser.SelectContext ctx);
	/**
	 * Visit a parse tree produced by {@link QLParser#columns}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitColumns(QLParser.ColumnsContext ctx);
	/**
	 * Visit a parse tree produced by {@link QLParser#column}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitColumn(QLParser.ColumnContext ctx);
	/**
	 * Visit a parse tree produced by {@link QLParser#distinct}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDistinct(QLParser.DistinctContext ctx);
	/**
	 * Visit a parse tree produced by {@link QLParser#from}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFrom(QLParser.FromContext ctx);
	/**
	 * Visit a parse tree produced by {@link QLParser#join}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJoin(QLParser.JoinContext ctx);
	/**
	 * Visit a parse tree produced by {@link QLParser#where}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhere(QLParser.WhereContext ctx);
	/**
	 * Visit a parse tree produced by {@link QLParser#groupBy}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGroupBy(QLParser.GroupByContext ctx);
	/**
	 * Visit a parse tree produced by {@link QLParser#having}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHaving(QLParser.HavingContext ctx);
	/**
	 * Visit a parse tree produced by {@link QLParser#limit}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLimit(QLParser.LimitContext ctx);
	/**
	 * Visit a parse tree produced by {@link QLParser#offset}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOffset(QLParser.OffsetContext ctx);
	/**
	 * Visit a parse tree produced by {@link QLParser#orderBy}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOrderBy(QLParser.OrderByContext ctx);
	/**
	 * Visit a parse tree produced by {@link QLParser#orders}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOrders(QLParser.OrdersContext ctx);
	/**
	 * Visit a parse tree produced by {@link QLParser#order}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOrder(QLParser.OrderContext ctx);
	/**
	 * Visit a parse tree produced by {@link QLParser#table}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTable(QLParser.TableContext ctx);
	/**
	 * Visit a parse tree produced by {@link QLParser#exprs}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprs(QLParser.ExprsContext ctx);
	/**
	 * Visit a parse tree produced by {@link QLParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpr(QLParser.ExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link QLParser#term}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTerm(QLParser.TermContext ctx);
	/**
	 * Visit a parse tree produced by {@link QLParser#term6}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTerm6(QLParser.Term6Context ctx);
	/**
	 * Visit a parse tree produced by {@link QLParser#term5}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTerm5(QLParser.Term5Context ctx);
	/**
	 * Visit a parse tree produced by {@link QLParser#term4}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTerm4(QLParser.Term4Context ctx);
	/**
	 * Visit a parse tree produced by {@link QLParser#term3}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTerm3(QLParser.Term3Context ctx);
	/**
	 * Visit a parse tree produced by {@link QLParser#term2}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTerm2(QLParser.Term2Context ctx);
	/**
	 * Visit a parse tree produced by {@link QLParser#term1}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTerm1(QLParser.Term1Context ctx);
	/**
	 * Visit a parse tree produced by {@link QLParser#term0}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTerm0(QLParser.Term0Context ctx);
	/**
	 * Visit a parse tree produced by {@link QLParser#literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLiteral(QLParser.LiteralContext ctx);
	/**
	 * Visit a parse tree produced by {@link QLParser#str}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStr(QLParser.StrContext ctx);
	/**
	 * Visit a parse tree produced by {@link QLParser#json}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJson(QLParser.JsonContext ctx);
	/**
	 * Visit a parse tree produced by {@link QLParser#jsonArray}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJsonArray(QLParser.JsonArrayContext ctx);
	/**
	 * Visit a parse tree produced by {@link QLParser#jsonObject}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJsonObject(QLParser.JsonObjectContext ctx);
	/**
	 * Visit a parse tree produced by {@link QLParser#jsonProps}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJsonProps(QLParser.JsonPropsContext ctx);
	/**
	 * Visit a parse tree produced by {@link QLParser#jsonProp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitJsonProp(QLParser.JsonPropContext ctx);
	/**
	 * Visit a parse tree produced by {@link QLParser#call}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCall(QLParser.CallContext ctx);
	/**
	 * Visit a parse tree produced by {@link QLParser#identifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIdentifier(QLParser.IdentifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link QLParser#word}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWord(QLParser.WordContext ctx);
	/**
	 * Visit a parse tree produced by {@link QLParser#keyword}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitKeyword(QLParser.KeywordContext ctx);
}