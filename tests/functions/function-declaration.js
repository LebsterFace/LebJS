function
double(
    x
)
{
    return x * 2;
}

Test.expect(8, double(4));
Test.expect(16, double(double(4)));
let num = double(4);
Test.expect(16, double(num));

try { x; Test.fail(); } catch(e) {
    Test.expect(e.name, 'ReferenceError');
    Test.expect(e.message, 'x is not defined');
}

function doubleSquare(x) {
    return double(x) * double(x);
}

Test.expect(256, doubleSquare(num));

function noParams() {
    return 123;
}

Test.expect(123, noParams());
Test.expect(123, noParams(1, 2, 3, 4));

function multiply(num1, num2) {
    let result = num1 * num2;
    return result;
}

Test.expect(28, multiply(4, 7))
Test.expect(400, multiply(20, 20))
Test.expect(1.5, multiply(0.5, 3))

function pow(x, n) {
    let result = 1;

    // multiply result by x n times in the loop
    for (let i = 0; i < n; i++) {
        result *= x;
    }

    return result;
}

Test.expect(4, pow(2, 2))
Test.expect(8, pow(2, 3))
Test.expect(16, pow(2, 4))

function recursive(x, n) {
    if (n === 1) {
        return x;
    } else {
        return x * pow(x, n - 1);
    }
}

Test.expect(4, recursive(2, 2))
Test.expect(8, recursive(2, 3))
Test.expect(16, recursive(2, 4))

function example(a, b, c, d) {
    return a + b + c + d;
}

Test.expect("1234", example("1", "2", "3", "4"))
Test.expect("123undefined", example("1", "2", "3"))
Test.expect("12undefinedundefined", example("1", "2"))
Test.expect("1undefinedundefinedundefined", example("1"))
Test.expect(true, isNaN(example()))