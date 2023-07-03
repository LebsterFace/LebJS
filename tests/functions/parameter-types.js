function none() {}
Test.expect(undefined, none());

function one(arg) {
    return arg;
}

let obj = { a: 1, b: 2 };
Test.expect(obj, one(obj));
Test.expectError("ReferenceError", "arg is not defined", () => arg);

function multiple(a, b, c, d) {
    return a + b + c + d;
}

Test.expect(40, multiple(10, 10, 10, 10));
Test.expect("undefinedundefinedundefined", multiple(""))
Test.expectError("ReferenceError", "a is not defined", () => a);
Test.expectError("ReferenceError", "b is not defined", () => b);
Test.expectError("ReferenceError", "c is not defined", () => c);
Test.expectError("ReferenceError", "d is not defined", () => d);

function arrayDestructuring([ a, b, c, d ]) {
    return a + b + c + d;
}

Test.expect(40, arrayDestructuring([10, 10, 10, 10]));
Test.expect("undefinedundefinedundefined", arrayDestructuring([""]))
Test.expectError("ReferenceError", "a is not defined", () => a);
Test.expectError("ReferenceError", "b is not defined", () => b);
Test.expectError("ReferenceError", "c is not defined", () => c);
Test.expectError("ReferenceError", "d is not defined", () => d);

function objectDestructuring({ a, b, c, d }) {
    return a + b + c + d;
}

Test.expect(40, objectDestructuring({ a: 10, b: 10, c: 10, d: 10 }));
Test.expect("undefinedundefinedundefined", objectDestructuring({ a: "" }))
Test.expectError("ReferenceError", "a is not defined", () => a);
Test.expectError("ReferenceError", "b is not defined", () => b);
Test.expectError("ReferenceError", "c is not defined", () => c);
Test.expectError("ReferenceError", "d is not defined", () => d);

function restArgument(...rest) {
    return rest.join("");
}

Test.expect('abcd', restArgument("a", "b", "c", "d"));
Test.expect('', restArgument());
Test.expectError("ReferenceError", "rest is not defined", () => rest);