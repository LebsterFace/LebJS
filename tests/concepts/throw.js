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

try {
    fake
} catch(e) {
    expect("fake is not defined", e.message)
}

try {
    try {
        error
    } catch(e) {
        alsoError
    }
} catch (e) {
    expect("alsoError is not defined", e.message);
    expect("ReferenceError", e.name);
}