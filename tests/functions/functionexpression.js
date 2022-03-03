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

expect(5, myFunc(1, 2));
expect(16, add(2, 2, square));
expect(null, add(2, 2, function(n) {
    if (n === 4) {
        return null;
    } else {
        return "Something else";
    }
}));