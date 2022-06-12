function foo() {
	throw 123
	Test.fail()
}
try {
	foo()
	Test.fail()
} catch (e) {
	Test.expect(123, e);
}
let err = "oops!";
try {
	{
		throw err;
		Test.fail()
	}
	Test.fail()
} catch (e) {
	Test.expect(err, e);
}
try {
	fake
	Test.fail()
} catch (e) {
	Test.expect("fake is not defined", e.message)
	Test.expect("ReferenceError", e.name);
}
try {
	try {
		error
		Test.fail()
	} catch (e) {
		alsoError
		Test.fail()
	}
	Test.fail()
} catch (e) {
	Test.expect("alsoError is not defined", e.message);
	Test.expect("ReferenceError", e.name);
}