// basic behavior
// basic binding
Test.expect(1, Function.prototype.bind.length);

const charAt = String.prototype.charAt.bind("bar");
Test.expect("bar", charAt(0) + charAt(1) + charAt(2));

function getB() {
	return this.toUpperCase().charAt(0);
}
Test.expect("B", getB.bind("bar")());

// bound functions work with array functions
const Make3 = Number.bind(null, 3);
Test.expect(3, [55].map(Make3)[0]);

const MakeTrue = Boolean.bind(null, true);

Test.expect(3, [1, 2, 3].filter(MakeTrue).length);
Test.expect(9, [1, 2, 3].reduce(function (acc, x) { return acc + x; }.bind(null, 4, 5)));
Test.expect(12, [1, 2, 3].reduce(function (acc, x) { return acc + x + this; }.bind(3)));

// name has 'bound' prefix
{
	function foo() { }
	const boundFoo = foo.bind(123);
	Test.expect("foo", foo.name);
	Test.expect("bound foo", boundFoo.name);
};

// prototype is inherited from target function
{
	function foo() { }
	Object.setPrototypeOf(foo, Array.prototype);
	const boundFoo = Function.prototype.bind.call(foo, 123);
	Test.expect(Array.prototype, Object.getPrototypeOf(boundFoo));
};

// bound function arguments
function sum(a, b, c) {
	return a + b + c;
}

const boundSum = sum.bind(null, 10, 5);

// arguments are bound to the function
Test.expect(NaN, boundSum());
Test.expect(20, boundSum(5));
Test.expect(20, boundSum(5, 6, 7));

// arguments are appended to a BoundFunction's bound arguments
Test.expect(20, boundSum.bind(null, 5)());

// binding a constructor's arguments
const Make5 = Number.bind(null, 5);
Test.expect(5, Make5());
Test.expect(5, new Make5().valueOf());

// length property
Test.expect(3, sum.length);
Test.expect(1, boundSum.length);
Test.expect(0, boundSum.bind(null, 5).length);
Test.expect(0, boundSum.bind(null, 5, 6, 7, 8).length);

// bound function `this`
function identity() {
	return this;
}

/* TODO: captures global object as `this` if `this` is null or undefined
Test.expect(globalThis, identity.bind()());
Test.expect(globalThis, identity.bind(null)());
Test.expect(globalThis, identity.bind(undefined)());

function Foo() {
	Test.expect(globalThis, identity.bind()());
	Test.expect(this, identity.bind(this)());
}
new Foo();
*/

// does not capture global object as `this` if `this` is null or undefined in strict mode
{
	"use strict";

	function strictIdentity() {
		return this;
	}

	Test.expect(undefined, strictIdentity.bind()());
	Test.expect(null, strictIdentity.bind(null)());
	Test.expect(undefined, strictIdentity.bind(undefined)());
}

// bound functions retain `this` values passed to them
const obj = { foo: "bar" };
Test.expect(obj, identity.bind(obj)());

// bound `this` cannot be changed after being set
expect(identity.bind("foo").bind(123)()).toBeInstanceOf(String);
// arrow functions cannot be bound
Test.expect(globalThis, (() => this).bind("foo")());

// length of original function is used for bound function
[0, 1, 2147483647, 2147483648, 2147483649].forEach(value => {
	function emptyFunction() { }

	Object.defineProperty(emptyFunction, "length", { value });

	Test.expect(value, emptyFunction.bind().length);
	Test.expect(value, emptyFunction.bind(null).length);
	Test.expect(Math.max(0, value - 1), emptyFunction.bind(null, 0).length);
	Test.expect(Math.max(0, value - 3), emptyFunction.bind(null, 0, 1, 2).length);
});

// bound function constructors
function Bar() {
	this.x = 3;
	this.y = 4;
}

Bar.prototype.baz = "baz";
const BoundBar = Bar.bind({ u: 5, v: 6 });
const bar = new BoundBar();

// bound `this` value does not affect constructor
Test.expect(3, bar.x);
Test.expect(4, bar.y);
Test.expect("undefined", typeof bar.u);
Test.expect("undefined", typeof bar.v);

// bound functions retain original prototype
Test.expect("baz", bar.baz);
// bound functions do not have a prototype property
expect(BoundBar).not.toHaveProperty("prototype");

// errors
// does not accept non-function values
Test.expectError("TypeError", "'foo' is not a function", () => Function.prototype.bind.call("foo"));