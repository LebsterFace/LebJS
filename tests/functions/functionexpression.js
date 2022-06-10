const square = function(n) {
    return n * n;
}

const doNothing = function (
    n
) { return n }

const add = function (a, b, callback) {
    return callback(a + b);
}

const myFunc = function myFunc(a, b) {
    return add(a, square(b), doNothing);
}

Test.expect(5, myFunc(1, 2));
Test.expectError("ReferenceError", "a is not defined", () => a);

Test.expect(16, add(2, 2, square));
Test.expect(null, add(2, 2, function(n) {
    if (n === 4) {
        return null;
    } else {
        return "Something else";
    }
}));