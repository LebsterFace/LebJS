const o = {};
Test.expect(o, o.valueOf());
// Non-standard excluded: Test.equals(new Number(42), Object.prototype.valueOf.call(42));
Test.expect(NaN, {} - 10);
Test.expect(90, {
    toString: () => "100"
} - 10);
Test.expect(990, {
    valueOf: () => "1000"
} - 10);
Test.expect(0, {
    toString: () => "50",
    valueOf: () => "10"
} - 10);

// Using valueOf on custom types
{
    function MyNumberType(n) {
        this.number = n;
    }

    MyNumberType.prototype.valueOf = function() {
        return this.number;
    };

    const myObj = new MyNumberType(4);
    Test.expect(7, myObj + 3);
}

// Using unary plus
{
    Test.expect(5, +"5")
    Test.expect(0, +"")
    Test.expect(NaN, +"1 + 2")
    // FIXME: Test.expect(< same as (new Date()).getTime() >, +new Date())
    Test.expect(NaN, +"foo")
    Test.expect(NaN, +{})
    Test.expect(0, +[])
    Test.expect(1, +[1])
    Test.expect(NaN, +[1, 2])
    // FIXME: Test.expect(NaN, +new Set([1]))
    // FIXME: Test.expectError("TypeError", "Cannot convert a BigInt value to a number", () => +BigInt(1))
    Test.expect(NaN, +undefined)
    Test.expect(0, +null)
    Test.expect(1, +true)
    Test.expect(0, +false)
}