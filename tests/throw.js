function foo() {
    throw 123
}

try {
    foo()
} catch(e) {
    expect(123, e);
}
let err = "oops!";
try {
    {
        throw err;
    }
} catch (e) {
    expect(err, e);
}