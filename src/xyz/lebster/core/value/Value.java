package xyz.lebster.core.value;

import xyz.lebster.core.Interpreter;
import xyz.lebster.core.node.Expression;

abstract public class Value<JType> extends Expression {
    public final Type type;
    public final JType value;

    public Value(Type type, JType value) {
        this.type = type;
        this.value = value;
    }

    @Override
    public void dump(int indent) {
        Interpreter.dumpIndent(indent);
        System.out.print(type);
        System.out.print(": ");
        System.out.println(value);
    }

    @Override
    public Value<JType> execute(Interpreter interpreter) {
        return this;
    }
}
