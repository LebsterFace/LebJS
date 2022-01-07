package xyz.lebster.core.node.value;

import xyz.lebster.core.ANSI;
import xyz.lebster.core.exception.NotImplemented;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.StringRepresentation;
import xyz.lebster.core.node.value.object.ObjectLiteral;

public final class BooleanLiteral extends Primitive<Boolean> {
	public static final BooleanLiteral TRUE = new BooleanLiteral(Boolean.TRUE);
	public static final BooleanLiteral FALSE = new BooleanLiteral(Boolean.FALSE);

	private BooleanLiteral(boolean value) {
		super(value, Type.Boolean);
	}

	public static BooleanLiteral of(boolean b) {
		return b ? TRUE : FALSE;
	}

	@Override
	public void represent(StringRepresentation representation) {
		representation.append(ANSI.BRIGHT_YELLOW);
		representation.append(value);
		representation.append(ANSI.RESET);
	}

	@Override
	public StringLiteral toStringLiteral(Interpreter interpreter) throws AbruptCompletion {
		return new StringLiteral(value.toString());
	}

	@Override
	public NumericLiteral toNumericLiteral(Interpreter interpreter) {
		return new NumericLiteral(value ? 1.0 : 0.0);
	}

	@Override
	public BooleanLiteral toBooleanLiteral(Interpreter interpreter) {
		return this;
	}

	@Override
	public ObjectLiteral toObjectLiteral(Interpreter interpreter) {
		throw new NotImplemented("BooleanWrapper");
	}

	public BooleanLiteral not() {
		return new BooleanLiteral(!value);
	}

	@Override
	public String typeOf(Interpreter interpreter) {
		return "boolean";
	}
}