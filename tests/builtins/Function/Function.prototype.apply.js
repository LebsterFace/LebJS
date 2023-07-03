// basic functionality
function Foo(arg) {
	this.foo = arg;
}

function Bar(arg) {
	this.bar = arg;
}

function FooBar(arg) {
	Foo.apply(this, [arg]);
	Bar.apply(this, [arg]);
}

function FooBarBaz(arg) {
	Foo.apply(this, [arg]);
	Bar.apply(this, [arg]);
	this.baz = arg;
}

Test.expect(2, Function.prototype.apply.length);

const foo = new Foo("test");
Test.expect("test", foo.foo);
Test.expect(undefined, foo.bar);
Test.expect(undefined, foo.baz);

const bar = new Bar("test");
Test.expect(undefined, bar.foo);
Test.expect("test", bar.bar);
Test.expect(undefined, bar.baz);

const foobar = new FooBar("test");
Test.expect("test", foobar.foo);
Test.expect("test", foobar.bar);
Test.expect(undefined, foobar.baz);

const foobarbaz = new FooBarBaz("test");
Test.expect("test", foobarbaz.foo);
Test.expect("test", foobarbaz.bar);
Test.expect("test", foobarbaz.baz);

Test.expect(1, Math.abs.apply(null, [-1]));

const add = (x, y) => x + y;
Test.expect(3, add.apply(null, [1, 2]));

const multiply = function (x, y) {
	return x * y;
};

Test.expect(12, multiply.apply(null, [3, 4]));
Test.expect(globalThis, (() => this).apply("foo"));

// errors
// does not accept non-function values
Test.expectError("TypeError", "'foo' is not a function", () => Function.prototype.apply.call("foo"));
Test.expectError("TypeError", "undefined is not a function", () => Function.prototype.apply.call(undefined));
Test.expectError("TypeError", "null is not a function", () => Function.prototype.apply.call(null));