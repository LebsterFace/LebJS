function foo() {
	throw 123
	expect(false, true)
}
try {
	foo()
	expect(false, true)
} catch (e) {
	expect(123, e);
}
let err = "oops!";
try {
	{
		throw err;
		expect(false, true)
	}
	expect(false, true)
} catch (e) {
	expect(err, e);
}
try {
	fake
	expect(false, true)
} catch (e) {
	expect("fake is not defined", e.message)
	expect("ReferenceError", e.name);
}
try {
	try {
		error
		expect(false, true)
	} catch (e) {
		alsoError
		expect(false, true)
	}
	expect(false, true)
} catch (e) {
	expect("alsoError is not defined", e.message);
	expect("ReferenceError", e.name);
}