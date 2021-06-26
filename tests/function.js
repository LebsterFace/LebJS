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