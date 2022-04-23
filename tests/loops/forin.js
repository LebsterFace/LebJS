// iterate through empty string
{
	const a = [];
	for (const property in "") {
		a.push(property);
	}
	expect(a.length, 0);
}


// iterate through number
{
	const a = [];
	for (const property in 123) {
		a.push(property);
	}
	expect(a.length, 0);
}

for (const property in {a:1,b:1,c:1,d:1,e:1}) {}
try {
	property
	expect(false, true)
} catch (e) {
	expect("property is not defined", e.message)
	expect("ReferenceError", e.name);
}

// iterate through empty object
{
	const a = [];
	for (const property in {}) {
		a.push(property);
	}
	expect(a.length, 0);
}


// iterate through string
{
	const a = [];
	for (const property in "hello") {
		a.push(property);
	}
	expect(a.join(""), '01234');
}


// iterate through object
{
	const a = [];
	for (const property in { a: 1, b: 2, c: 2 }) {
		a.push(property);
	}
	expect(a.join(""), 'abc');
}


// iterate through undefined
for (const property in undefined) {
	throw "Should not iterate through undefined";
}

// use already-declared variable
let property;
for (property in "abc");
expect(property, "2");


// special left hand sides
// allow member expression as variable
const f = {};
for (f.a in "abc");
expect(f.a, "2");


// allow member expression of function call
const b = {};
function f() {
	return b;
}

for (f().a in "abc");

expect(f().a, "2");
expect(b.a, "2");


// FIXME: remove properties while iterating
//const from = [1, 2, 3];
//const to = [];
//for (const prop in from) {
//	to.push(prop);
//	from.pop();
//}
//expect(to.join(""), "01");


// duplicated properties in prototype
{
	const object = { a: 1 };
	const proto = { a: 2 };
	Object.setPrototypeOf(object, proto);
	const a = [];
	for (const prop in object) {
		a.push(prop);
	}
	expect(a.join(""), "a");
}