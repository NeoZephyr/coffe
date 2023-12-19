// Generated from Script.g4 by ANTLR 4.10.1

package compile.antlr.script;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link ScriptParser}.
 */
public interface ScriptListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link ScriptParser#classDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterClassDeclaration(ScriptParser.ClassDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link ScriptParser#classDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitClassDeclaration(ScriptParser.ClassDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link ScriptParser#classBody}.
	 * @param ctx the parse tree
	 */
	void enterClassBody(ScriptParser.ClassBodyContext ctx);
	/**
	 * Exit a parse tree produced by {@link ScriptParser#classBody}.
	 * @param ctx the parse tree
	 */
	void exitClassBody(ScriptParser.ClassBodyContext ctx);
	/**
	 * Enter a parse tree produced by {@link ScriptParser#classBodyDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterClassBodyDeclaration(ScriptParser.ClassBodyDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link ScriptParser#classBodyDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitClassBodyDeclaration(ScriptParser.ClassBodyDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link ScriptParser#memberDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterMemberDeclaration(ScriptParser.MemberDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link ScriptParser#memberDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitMemberDeclaration(ScriptParser.MemberDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link ScriptParser#functionDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterFunctionDeclaration(ScriptParser.FunctionDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link ScriptParser#functionDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitFunctionDeclaration(ScriptParser.FunctionDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link ScriptParser#functionBody}.
	 * @param ctx the parse tree
	 */
	void enterFunctionBody(ScriptParser.FunctionBodyContext ctx);
	/**
	 * Exit a parse tree produced by {@link ScriptParser#functionBody}.
	 * @param ctx the parse tree
	 */
	void exitFunctionBody(ScriptParser.FunctionBodyContext ctx);
	/**
	 * Enter a parse tree produced by {@link ScriptParser#typeOrVoid}.
	 * @param ctx the parse tree
	 */
	void enterTypeOrVoid(ScriptParser.TypeOrVoidContext ctx);
	/**
	 * Exit a parse tree produced by {@link ScriptParser#typeOrVoid}.
	 * @param ctx the parse tree
	 */
	void exitTypeOrVoid(ScriptParser.TypeOrVoidContext ctx);
	/**
	 * Enter a parse tree produced by {@link ScriptParser#qualifiedNameList}.
	 * @param ctx the parse tree
	 */
	void enterQualifiedNameList(ScriptParser.QualifiedNameListContext ctx);
	/**
	 * Exit a parse tree produced by {@link ScriptParser#qualifiedNameList}.
	 * @param ctx the parse tree
	 */
	void exitQualifiedNameList(ScriptParser.QualifiedNameListContext ctx);
	/**
	 * Enter a parse tree produced by {@link ScriptParser#formalParameters}.
	 * @param ctx the parse tree
	 */
	void enterFormalParameters(ScriptParser.FormalParametersContext ctx);
	/**
	 * Exit a parse tree produced by {@link ScriptParser#formalParameters}.
	 * @param ctx the parse tree
	 */
	void exitFormalParameters(ScriptParser.FormalParametersContext ctx);
	/**
	 * Enter a parse tree produced by {@link ScriptParser#formalParameterList}.
	 * @param ctx the parse tree
	 */
	void enterFormalParameterList(ScriptParser.FormalParameterListContext ctx);
	/**
	 * Exit a parse tree produced by {@link ScriptParser#formalParameterList}.
	 * @param ctx the parse tree
	 */
	void exitFormalParameterList(ScriptParser.FormalParameterListContext ctx);
	/**
	 * Enter a parse tree produced by {@link ScriptParser#formalParameter}.
	 * @param ctx the parse tree
	 */
	void enterFormalParameter(ScriptParser.FormalParameterContext ctx);
	/**
	 * Exit a parse tree produced by {@link ScriptParser#formalParameter}.
	 * @param ctx the parse tree
	 */
	void exitFormalParameter(ScriptParser.FormalParameterContext ctx);
	/**
	 * Enter a parse tree produced by {@link ScriptParser#lastFormalParameter}.
	 * @param ctx the parse tree
	 */
	void enterLastFormalParameter(ScriptParser.LastFormalParameterContext ctx);
	/**
	 * Exit a parse tree produced by {@link ScriptParser#lastFormalParameter}.
	 * @param ctx the parse tree
	 */
	void exitLastFormalParameter(ScriptParser.LastFormalParameterContext ctx);
	/**
	 * Enter a parse tree produced by {@link ScriptParser#variableModifier}.
	 * @param ctx the parse tree
	 */
	void enterVariableModifier(ScriptParser.VariableModifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link ScriptParser#variableModifier}.
	 * @param ctx the parse tree
	 */
	void exitVariableModifier(ScriptParser.VariableModifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link ScriptParser#qualifiedName}.
	 * @param ctx the parse tree
	 */
	void enterQualifiedName(ScriptParser.QualifiedNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link ScriptParser#qualifiedName}.
	 * @param ctx the parse tree
	 */
	void exitQualifiedName(ScriptParser.QualifiedNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link ScriptParser#fieldDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterFieldDeclaration(ScriptParser.FieldDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link ScriptParser#fieldDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitFieldDeclaration(ScriptParser.FieldDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link ScriptParser#constructorDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterConstructorDeclaration(ScriptParser.ConstructorDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link ScriptParser#constructorDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitConstructorDeclaration(ScriptParser.ConstructorDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link ScriptParser#variableDeclarators}.
	 * @param ctx the parse tree
	 */
	void enterVariableDeclarators(ScriptParser.VariableDeclaratorsContext ctx);
	/**
	 * Exit a parse tree produced by {@link ScriptParser#variableDeclarators}.
	 * @param ctx the parse tree
	 */
	void exitVariableDeclarators(ScriptParser.VariableDeclaratorsContext ctx);
	/**
	 * Enter a parse tree produced by {@link ScriptParser#variableDeclarator}.
	 * @param ctx the parse tree
	 */
	void enterVariableDeclarator(ScriptParser.VariableDeclaratorContext ctx);
	/**
	 * Exit a parse tree produced by {@link ScriptParser#variableDeclarator}.
	 * @param ctx the parse tree
	 */
	void exitVariableDeclarator(ScriptParser.VariableDeclaratorContext ctx);
	/**
	 * Enter a parse tree produced by {@link ScriptParser#variableDeclaratorId}.
	 * @param ctx the parse tree
	 */
	void enterVariableDeclaratorId(ScriptParser.VariableDeclaratorIdContext ctx);
	/**
	 * Exit a parse tree produced by {@link ScriptParser#variableDeclaratorId}.
	 * @param ctx the parse tree
	 */
	void exitVariableDeclaratorId(ScriptParser.VariableDeclaratorIdContext ctx);
	/**
	 * Enter a parse tree produced by {@link ScriptParser#variableInitializer}.
	 * @param ctx the parse tree
	 */
	void enterVariableInitializer(ScriptParser.VariableInitializerContext ctx);
	/**
	 * Exit a parse tree produced by {@link ScriptParser#variableInitializer}.
	 * @param ctx the parse tree
	 */
	void exitVariableInitializer(ScriptParser.VariableInitializerContext ctx);
	/**
	 * Enter a parse tree produced by {@link ScriptParser#arrayInitializer}.
	 * @param ctx the parse tree
	 */
	void enterArrayInitializer(ScriptParser.ArrayInitializerContext ctx);
	/**
	 * Exit a parse tree produced by {@link ScriptParser#arrayInitializer}.
	 * @param ctx the parse tree
	 */
	void exitArrayInitializer(ScriptParser.ArrayInitializerContext ctx);
	/**
	 * Enter a parse tree produced by {@link ScriptParser#classOrInterfaceType}.
	 * @param ctx the parse tree
	 */
	void enterClassOrInterfaceType(ScriptParser.ClassOrInterfaceTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link ScriptParser#classOrInterfaceType}.
	 * @param ctx the parse tree
	 */
	void exitClassOrInterfaceType(ScriptParser.ClassOrInterfaceTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link ScriptParser#typeArgument}.
	 * @param ctx the parse tree
	 */
	void enterTypeArgument(ScriptParser.TypeArgumentContext ctx);
	/**
	 * Exit a parse tree produced by {@link ScriptParser#typeArgument}.
	 * @param ctx the parse tree
	 */
	void exitTypeArgument(ScriptParser.TypeArgumentContext ctx);
	/**
	 * Enter a parse tree produced by {@link ScriptParser#literal}.
	 * @param ctx the parse tree
	 */
	void enterLiteral(ScriptParser.LiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link ScriptParser#literal}.
	 * @param ctx the parse tree
	 */
	void exitLiteral(ScriptParser.LiteralContext ctx);
	/**
	 * Enter a parse tree produced by {@link ScriptParser#integerLiteral}.
	 * @param ctx the parse tree
	 */
	void enterIntegerLiteral(ScriptParser.IntegerLiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link ScriptParser#integerLiteral}.
	 * @param ctx the parse tree
	 */
	void exitIntegerLiteral(ScriptParser.IntegerLiteralContext ctx);
	/**
	 * Enter a parse tree produced by {@link ScriptParser#floatLiteral}.
	 * @param ctx the parse tree
	 */
	void enterFloatLiteral(ScriptParser.FloatLiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link ScriptParser#floatLiteral}.
	 * @param ctx the parse tree
	 */
	void exitFloatLiteral(ScriptParser.FloatLiteralContext ctx);
	/**
	 * Enter a parse tree produced by {@link ScriptParser#prog}.
	 * @param ctx the parse tree
	 */
	void enterProg(ScriptParser.ProgContext ctx);
	/**
	 * Exit a parse tree produced by {@link ScriptParser#prog}.
	 * @param ctx the parse tree
	 */
	void exitProg(ScriptParser.ProgContext ctx);
	/**
	 * Enter a parse tree produced by {@link ScriptParser#block}.
	 * @param ctx the parse tree
	 */
	void enterBlock(ScriptParser.BlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link ScriptParser#block}.
	 * @param ctx the parse tree
	 */
	void exitBlock(ScriptParser.BlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link ScriptParser#blockStatements}.
	 * @param ctx the parse tree
	 */
	void enterBlockStatements(ScriptParser.BlockStatementsContext ctx);
	/**
	 * Exit a parse tree produced by {@link ScriptParser#blockStatements}.
	 * @param ctx the parse tree
	 */
	void exitBlockStatements(ScriptParser.BlockStatementsContext ctx);
	/**
	 * Enter a parse tree produced by {@link ScriptParser#blockStatement}.
	 * @param ctx the parse tree
	 */
	void enterBlockStatement(ScriptParser.BlockStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link ScriptParser#blockStatement}.
	 * @param ctx the parse tree
	 */
	void exitBlockStatement(ScriptParser.BlockStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link ScriptParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterStatement(ScriptParser.StatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link ScriptParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitStatement(ScriptParser.StatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link ScriptParser#switchBlockStatementGroup}.
	 * @param ctx the parse tree
	 */
	void enterSwitchBlockStatementGroup(ScriptParser.SwitchBlockStatementGroupContext ctx);
	/**
	 * Exit a parse tree produced by {@link ScriptParser#switchBlockStatementGroup}.
	 * @param ctx the parse tree
	 */
	void exitSwitchBlockStatementGroup(ScriptParser.SwitchBlockStatementGroupContext ctx);
	/**
	 * Enter a parse tree produced by {@link ScriptParser#switchLabel}.
	 * @param ctx the parse tree
	 */
	void enterSwitchLabel(ScriptParser.SwitchLabelContext ctx);
	/**
	 * Exit a parse tree produced by {@link ScriptParser#switchLabel}.
	 * @param ctx the parse tree
	 */
	void exitSwitchLabel(ScriptParser.SwitchLabelContext ctx);
	/**
	 * Enter a parse tree produced by {@link ScriptParser#forControl}.
	 * @param ctx the parse tree
	 */
	void enterForControl(ScriptParser.ForControlContext ctx);
	/**
	 * Exit a parse tree produced by {@link ScriptParser#forControl}.
	 * @param ctx the parse tree
	 */
	void exitForControl(ScriptParser.ForControlContext ctx);
	/**
	 * Enter a parse tree produced by {@link ScriptParser#forInit}.
	 * @param ctx the parse tree
	 */
	void enterForInit(ScriptParser.ForInitContext ctx);
	/**
	 * Exit a parse tree produced by {@link ScriptParser#forInit}.
	 * @param ctx the parse tree
	 */
	void exitForInit(ScriptParser.ForInitContext ctx);
	/**
	 * Enter a parse tree produced by {@link ScriptParser#enhancedForControl}.
	 * @param ctx the parse tree
	 */
	void enterEnhancedForControl(ScriptParser.EnhancedForControlContext ctx);
	/**
	 * Exit a parse tree produced by {@link ScriptParser#enhancedForControl}.
	 * @param ctx the parse tree
	 */
	void exitEnhancedForControl(ScriptParser.EnhancedForControlContext ctx);
	/**
	 * Enter a parse tree produced by {@link ScriptParser#parExpression}.
	 * @param ctx the parse tree
	 */
	void enterParExpression(ScriptParser.ParExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link ScriptParser#parExpression}.
	 * @param ctx the parse tree
	 */
	void exitParExpression(ScriptParser.ParExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link ScriptParser#expressionList}.
	 * @param ctx the parse tree
	 */
	void enterExpressionList(ScriptParser.ExpressionListContext ctx);
	/**
	 * Exit a parse tree produced by {@link ScriptParser#expressionList}.
	 * @param ctx the parse tree
	 */
	void exitExpressionList(ScriptParser.ExpressionListContext ctx);
	/**
	 * Enter a parse tree produced by {@link ScriptParser#functionCall}.
	 * @param ctx the parse tree
	 */
	void enterFunctionCall(ScriptParser.FunctionCallContext ctx);
	/**
	 * Exit a parse tree produced by {@link ScriptParser#functionCall}.
	 * @param ctx the parse tree
	 */
	void exitFunctionCall(ScriptParser.FunctionCallContext ctx);
	/**
	 * Enter a parse tree produced by {@link ScriptParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpression(ScriptParser.ExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link ScriptParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpression(ScriptParser.ExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link ScriptParser#primary}.
	 * @param ctx the parse tree
	 */
	void enterPrimary(ScriptParser.PrimaryContext ctx);
	/**
	 * Exit a parse tree produced by {@link ScriptParser#primary}.
	 * @param ctx the parse tree
	 */
	void exitPrimary(ScriptParser.PrimaryContext ctx);
	/**
	 * Enter a parse tree produced by {@link ScriptParser#typeList}.
	 * @param ctx the parse tree
	 */
	void enterTypeList(ScriptParser.TypeListContext ctx);
	/**
	 * Exit a parse tree produced by {@link ScriptParser#typeList}.
	 * @param ctx the parse tree
	 */
	void exitTypeList(ScriptParser.TypeListContext ctx);
	/**
	 * Enter a parse tree produced by {@link ScriptParser#type}.
	 * @param ctx the parse tree
	 */
	void enterType(ScriptParser.TypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link ScriptParser#type}.
	 * @param ctx the parse tree
	 */
	void exitType(ScriptParser.TypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link ScriptParser#functionType}.
	 * @param ctx the parse tree
	 */
	void enterFunctionType(ScriptParser.FunctionTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link ScriptParser#functionType}.
	 * @param ctx the parse tree
	 */
	void exitFunctionType(ScriptParser.FunctionTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link ScriptParser#primitiveType}.
	 * @param ctx the parse tree
	 */
	void enterPrimitiveType(ScriptParser.PrimitiveTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link ScriptParser#primitiveType}.
	 * @param ctx the parse tree
	 */
	void exitPrimitiveType(ScriptParser.PrimitiveTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link ScriptParser#creator}.
	 * @param ctx the parse tree
	 */
	void enterCreator(ScriptParser.CreatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link ScriptParser#creator}.
	 * @param ctx the parse tree
	 */
	void exitCreator(ScriptParser.CreatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link ScriptParser#superSuffix}.
	 * @param ctx the parse tree
	 */
	void enterSuperSuffix(ScriptParser.SuperSuffixContext ctx);
	/**
	 * Exit a parse tree produced by {@link ScriptParser#superSuffix}.
	 * @param ctx the parse tree
	 */
	void exitSuperSuffix(ScriptParser.SuperSuffixContext ctx);
	/**
	 * Enter a parse tree produced by {@link ScriptParser#arguments}.
	 * @param ctx the parse tree
	 */
	void enterArguments(ScriptParser.ArgumentsContext ctx);
	/**
	 * Exit a parse tree produced by {@link ScriptParser#arguments}.
	 * @param ctx the parse tree
	 */
	void exitArguments(ScriptParser.ArgumentsContext ctx);
}