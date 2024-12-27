package xyz.lebster.core.node.expression;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.environment.Environment;
import xyz.lebster.core.node.FunctionNode;
import xyz.lebster.core.node.FunctionParameters;
import xyz.lebster.core.node.SourceRange;
import xyz.lebster.core.node.statement.BlockStatement;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.function.Executable;
import xyz.lebster.core.value.function.OrdinaryFunction;
import xyz.lebster.core.value.object.AccessorDescriptor;
import xyz.lebster.core.value.object.Key;
import xyz.lebster.core.value.object.ObjectValue;
import xyz.lebster.core.value.primitive.string.StringValue;

import java.util.ArrayList;

@SpecificationURL("https://tc39.es/ecma262/multipage#sec-object-initializer")
public final class ObjectExpression implements Expression {
	public final ArrayList<ObjectEntryNode> entries = new ArrayList<>();
	public SourceRange range;

	public ObjectExpression() {
	}

	@Override
	public ObjectValue execute(Interpreter interpreter) throws AbruptCompletion {
		final ObjectValue result = new ObjectValue(interpreter.intrinsics);
		for (final ObjectEntryNode entryNode : entries)
			entryNode.insertInto(result, interpreter);
		return result;
	}

	public interface ObjectEntryNode {
		void insertInto(ObjectValue result, Interpreter interpreter) throws AbruptCompletion;
	}

	@Override
	public SourceRange range() {
		return range;
	}

	public record EntryNode(Expression key, Expression value) implements ObjectEntryNode {
		@Override
		public void insertInto(ObjectValue result, Interpreter interpreter) throws AbruptCompletion {
			final Key<?> key = this.key.execute(interpreter).toPropertyKey(interpreter);
			final Value<?> value = Executable.namedEvaluation(interpreter, this.value, key);
			result.put(key, value, true, true, true);
		}
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#prod-MethodDefinition")
	public record MethodNode(Expression name, FunctionParameters parameters, BlockStatement body, SourceRange range) implements FunctionNode, ObjectEntryNode {
		@Override
		@SpecificationURL("https://tc39.es/ecma262/multipage#sec-runtime-semantics-definemethod")
		public OrdinaryFunction execute(Interpreter interpreter) throws AbruptCompletion {
			// 1. Let propKey be ? Evaluation of ClassElementName.
			// NOTE: This is handled by the caller
			// 2. Let env be the running execution context's LexicalEnvironment.
			final Environment env = interpreter.environment();
			// TODO: 3. Let privateEnv be the running execution context's PrivateEnvironment.
			// TODO: 4. If functionPrototype is present, then
			// TODO: a. Let prototype be functionPrototype.
			// TODO: 5. Else,
			// TODO: a. Let prototype be %Function.prototype%.
			// TODO: 6. Let sourceText be the source text matched by MethodDefinition.
			// 7. Let closure be OrdinaryFunctionCreate(prototype, sourceText, UniqueFormalParameters, FunctionBody, non-lexical-this, env, privateEnv).
			// TODO: 8. Perform MakeMethod(closure, object).
			// 9. Return the Record { [[Key]]: propKey, [[Closure]]: closure }.
			return new OrdinaryFunction(interpreter.intrinsics, env, this);
		}

		@Override
		public void insertInto(ObjectValue result, Interpreter interpreter) throws AbruptCompletion {
			final Key<?> key = name.execute(interpreter).toPropertyKey(interpreter);
			final OrdinaryFunction value = execute(interpreter);
			value.updateName(key.toFunctionName());
			result.put(key, value, true, true, true);
		}
	}

	public record GetterSetterNode(boolean getter, MethodNode value) implements ObjectEntryNode {
		@Override
		public void insertInto(ObjectValue result, Interpreter interpreter) throws AbruptCompletion {
			final OrdinaryFunction function = this.value.execute(interpreter);
			final Key<?> key = this.value.name.execute(interpreter).toPropertyKey(interpreter);
			final String newName = "%s %s".formatted(getter ? "get" : "set", key.toFunctionName().value);
			function.updateName(new StringValue(newName));
			result.put(key, function, true, true, true);
			final var existing = result.value.get(key) instanceof AccessorDescriptor A ? A : null;
			final var descriptor = existing == null ? new AccessorDescriptor(null, null, true, true) : existing;
			if (getter) descriptor.setGetter(function);
			else descriptor.setSetter(function);
			if (existing == null) result.value.put(key, descriptor);
		}
	}

	public record ShorthandNode(StringValue key) implements ObjectEntryNode {
		@Override
		public void insertInto(ObjectValue result, Interpreter interpreter) throws AbruptCompletion {
			result.put(key, interpreter.getBinding(key).getValue(interpreter), true, true, true);
		}
	}

	public record SpreadNode(Expression name) implements ObjectEntryNode {
		@Override
		public void insertInto(ObjectValue result, Interpreter interpreter) throws AbruptCompletion {
			final ObjectValue value = name.execute(interpreter).toObjectValue(interpreter);
			for (final var entry : value.value.entrySet()) {
				if (entry.getValue().isEnumerable()) {
					result.put(entry.getKey(), value.get(interpreter, entry.getKey()));
				}
			}
		}
	}
}