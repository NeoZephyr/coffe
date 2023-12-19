// Generated from Script.g4 by ANTLR 4.10.1

package compile.antlr.script;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link ScriptParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface ScriptVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link ScriptParser#classDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitClassDeclaration(ScriptParser.ClassDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link ScriptParser#classBody}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitClassBody(ScriptParser.ClassBodyContext ctx);
	/**
	 * Visit a parse tree produced by {@link ScriptParser#classBodyDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitClassBodyDeclaration(ScriptParser.ClassBodyDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link ScriptParser#memberDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMemberDeclaration(ScriptParser.MemberDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link ScriptParser#functionDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionDeclaration(ScriptParser.FunctionDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link ScriptParser#functionBody}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionBody(ScriptParser.FunctionBodyContext ctx);
	/**
	 * Visit a parse tree produced by {@link ScriptParser#typeOrVoid}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTypeOrVoid(ScriptParser.TypeOrVoidContext ctx);
	/**
	 * Visit a parse tree produced by {@link ScriptParser#qualifiedNameList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitQualifiedNameList(ScriptParser.QualifiedNameListContext ctx);
	/**
	 * Visit a parse tree produced by {@link ScriptParser#formalParameters}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormalParameters(ScriptParser.FormalParametersContext ctx);
	/**
	 * Visit a parse tree produced by {@link ScriptParser#formalParameterList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormalParameterList(ScriptParser.FormalParameterListContext ctx);
	/**
	 * Visit a parse tree produced by {@link ScriptParser#formalParameter}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFormalParameter(ScriptParser.FormalParameterContext ctx);
	/**
	 * Visit a parse tree produced by {@link ScriptParser#lastFormalParameter}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLastFormalParameter(ScriptParser.LastFormalParameterContext ctx);
	/**
	 * Visit a parse tree produced by {@link ScriptParser#variableModifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariableModifier(ScriptParser.VariableModifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link ScriptParser#qualifiedName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitQualifiedName(ScriptParser.QualifiedNameContext ctx);
	/**
	 * Visit a parse tree produced by {@link ScriptParser#fieldDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFieldDeclaration(ScriptParser.FieldDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link ScriptParser#constructorDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstructorDeclaration(ScriptParser.ConstructorDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link ScriptParser#variableDeclarators}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariableDeclarators(ScriptParser.VariableDeclaratorsContext ctx);
	/**
	 * Visit a parse tree produced by {@link ScriptParser#variableDeclarator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariableDeclarator(ScriptParser.VariableDeclaratorContext ctx);
	/**
	 * Visit a parse tree produced by {@link ScriptParser#variableDeclaratorId}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariableDeclaratorId(ScriptParser.VariableDeclaratorIdContext ctx);
	/**
	 * Visit a parse tree produced by {@link ScriptParser#variableInitializer}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVariableInitializer(ScriptParser.VariableInitializerContext ctx);
	/**
	 * Visit a parse tree produced by {@link ScriptParser#arrayInitializer}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArrayInitializer(ScriptParser.ArrayInitializerContext ctx);
	/**
	 * Visit a parse tree produced by {@link ScriptParser#classOrInterfaceType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitClassOrInterfaceType(ScriptParser.ClassOrInterfaceTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link ScriptParser#typeArgument}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTypeArgument(ScriptParser.TypeArgumentContext ctx);
	/**
	 * Visit a parse tree produced by {@link ScriptParser#literal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLiteral(ScriptParser.LiteralContext ctx);
	/**
	 * Visit a parse tree produced by {@link ScriptParser#integerLiteral}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIntegerLiteral(ScriptParser.IntegerLiteralContext ctx);
	/**
	 * Visit a parse tree produced by {@link ScriptParser#floatLiteral}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFloatLiteral(ScriptParser.FloatLiteralContext ctx);
	/**
	 * Visit a parse tree produced by {@link ScriptParser#prog}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProg(ScriptParser.ProgContext ctx);
	/**
	 * Visit a parse tree produced by {@link ScriptParser#block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlock(ScriptParser.BlockContext ctx);
	/**
	 * Visit a parse tree produced by {@link ScriptParser#blockStatements}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlockStatements(ScriptParser.BlockStatementsContext ctx);
	/**
	 * Visit a parse tree produced by {@link ScriptParser#blockStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlockStatement(ScriptParser.BlockStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link ScriptParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatement(ScriptParser.StatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link ScriptParser#switchBlockStatementGroup}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSwitchBlockStatementGroup(ScriptParser.SwitchBlockStatementGroupContext ctx);
	/**
	 * Visit a parse tree produced by {@link ScriptParser#switchLabel}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSwitchLabel(ScriptParser.SwitchLabelContext ctx);
	/**
	 * Visit a parse tree produced by {@link ScriptParser#forControl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitForControl(ScriptParser.ForControlContext ctx);
	/**
	 * Visit a parse tree produced by {@link ScriptParser#forInit}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitForInit(ScriptParser.ForInitContext ctx);
	/**
	 * Visit a parse tree produced by {@link ScriptParser#enhancedForControl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEnhancedForControl(ScriptParser.EnhancedForControlContext ctx);
	/**
	 * Visit a parse tree produced by {@link ScriptParser#parExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParExpression(ScriptParser.ParExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link ScriptParser#expressionList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpressionList(ScriptParser.ExpressionListContext ctx);
	/**
	 * Visit a parse tree produced by {@link ScriptParser#functionCall}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionCall(ScriptParser.FunctionCallContext ctx);
	/**
	 * Visit a parse tree produced by {@link ScriptParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression(ScriptParser.ExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link ScriptParser#primary}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrimary(ScriptParser.PrimaryContext ctx);
	/**
	 * Visit a parse tree produced by {@link ScriptParser#typeList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTypeList(ScriptParser.TypeListContext ctx);
	/**
	 * Visit a parse tree produced by {@link ScriptParser#type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitType(ScriptParser.TypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link ScriptParser#functionType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionType(ScriptParser.FunctionTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link ScriptParser#primitiveType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrimitiveType(ScriptParser.PrimitiveTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link ScriptParser#creator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCreator(ScriptParser.CreatorContext ctx);
	/**
	 * Visit a parse tree produced by {@link ScriptParser#superSuffix}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSuperSuffix(ScriptParser.SuperSuffixContext ctx);
	/**
	 * Visit a parse tree produced by {@link ScriptParser#arguments}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArguments(ScriptParser.ArgumentsContext ctx);
}