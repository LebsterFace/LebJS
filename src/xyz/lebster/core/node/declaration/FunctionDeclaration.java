package xyz.lebster.core.node.declaration;

import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.node.FunctionNode;
import xyz.lebster.core.node.FunctionParameters;
import xyz.lebster.core.node.SourceRange;
import xyz.lebster.core.node.expression.literal.PrimitiveLiteral;
import xyz.lebster.core.node.statement.BlockStatement;
import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.function.ConstructorFunction;
import xyz.lebster.core.value.globals.Undefined;
import xyz.lebster.core.value.primitive.string.StringValue;

import static xyz.lebster.core.node.declaration.Kind.Let;

public record FunctionDeclaration(SourceRange range, BlockStatement body, PrimitiveLiteral<StringValue> name, FunctionParameters parameters) implements FunctionNode, Declaration {
	@Override
	public Value<?> execute(Interpreter interpreter) throws AbruptCompletion {
		final StringValue name = this.name == null ? Names.EMPTY : this.name.execute(interpreter);
		final ConstructorFunction function = new ConstructorFunction(interpreter.intrinsics, name, interpreter.environment(), this);
		interpreter.declareVariable(Let, name, function);
		return Undefined.instance;
	}
}