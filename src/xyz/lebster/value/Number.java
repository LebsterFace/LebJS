package xyz.lebster.value;

public class Number extends Value<Double> {
    public Number(double value) {
        super(Type.Number, value);
    }
}
