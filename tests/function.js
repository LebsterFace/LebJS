function double(x) {
    return x * 2;
}

expect(8, double(4));
expect(16, double(double(4)));
let num = double(4);
expect(16, double(num));

function doubleSquare(x) {
    return double(x) * double(x);
}

expect(256, doubleSquare(num));

function noParams() {
    return 123;
}

expect(123, noParams());
expect(123, noParams(1, 2, 3, 4));

function multiply(num1, num2) {
    let result = num1 * num2;
    return result;
}

expect(28, multiply(4, 7))
expect(400, multiply(20, 20))
expect(1.5, multiply(0.5, 3))

function pow(x, n) {
    let result = 1;

    // multiply result by x n times in the loop
    for (let i = 0; i < n; i++) {
        result *= x;
    }

    return result;
}

expect(4, pow(2, 2))
expect(8, pow(2, 3))
expect(16, pow(2, 4))

function recursive(x, n) {
    if (n === 1) {
        return x;
    } else {
        return x * pow(x, n - 1);
    }
}

expect(4, recursive(2, 2))
expect(8, recursive(2, 3))
expect(16, recursive(2, 4))