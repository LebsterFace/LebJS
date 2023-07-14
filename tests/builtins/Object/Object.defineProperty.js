// normal functionality

let s = Symbol("foo");

// non-configurable string property
{
	let o = {};
	Object.defineProperty(o, "foo", { value: 1, writable: false, enumerable: false });

	Test.expect(1, o.foo);
	Test.expectError("TypeError", "Cannot assign to read-only property 'foo'", () => o.foo = 2);
	Test.expect(1, o.foo);

	Test.equals({ value: 1, writable: false, enumerable: false, configurable: false }, Object.getOwnPropertyDescriptor(o, 'foo'));
}

// non-configurable symbol property
{
	let o = {};
	Object.defineProperty(o, s, { value: 1, writable: false, enumerable: false });

	Test.expect(1, o[s]);
	Test.expectError("TypeError", "Cannot assign to read-only property Symbol(foo)", () => o[s] = 2);
	Test.expect(1, o[s]);

	Test.equals({ value: 1, writable: false, enumerable: false, configurable: false }, Object.getOwnPropertyDescriptor(o, s));
}

// array index getter
{
	let o = {};
	Object.defineProperty(o, 2, { get() { return 10; } });
	Test.expect(10, o[2]);
}

// symbol property getter
{
	let o = {};
	Object.defineProperty(o, s, { get() { return 10; } });
	Test.expect(10, o[s]);
}

// configurable string property
{
	let o = {};
	Object.defineProperty(o, "foo", { value: "hi", writable: true, enumerable: true });

	Test.expect("hi", o.foo);
	o.foo = "ho";
	Test.expect("ho", o.foo);

	Test.equals({ value: "ho", writable: true, enumerable: true, configurable: false }, Object.getOwnPropertyDescriptor(o, 'foo'));
}

// configurable symbol property
{
	let o = {};
	Object.defineProperty(o, s, { value: "hi", writable: true, enumerable: true });

	Test.expect("hi", o[s]);
	o[s] = "ho";
	Test.expect("ho", o[s]);

	Test.equals({ value: "ho", writable: true, enumerable: true, configurable: false }, Object.getOwnPropertyDescriptor(o, s));
}

// reconfigure configurable string property
{
	let o = {};
	Object.defineProperty(o, "foo", { value: 9, configurable: true, writable: false });
	Object.defineProperty(o, "foo", { configurable: true, writable: true });

	Test.equals({ value: 9, writable: true, enumerable: false, configurable: true }, Object.getOwnPropertyDescriptor(o, 'foo'));
}

// reconfigure configurable symbol property
{
	let o = {};
	Object.defineProperty(o, s, { value: 9, configurable: true, writable: false });
	Object.defineProperty(o, s, { configurable: true, writable: true });

	Test.equals({ value: 9, writable: true, enumerable: false, configurable: true }, Object.getOwnPropertyDescriptor(o, s));
}

// define string accessor
{
	let o = {};

	Object.defineProperty(o, "foo", {
		configurable: true,
		get() {
			return o.secret_foo + 1;
		},
		set(value) {
			this.secret_foo = value + 1;
		},
	});

	o.foo = 10;
	Test.expect(12, o.foo);
	o.foo = 20;
	Test.expect(22, o.foo);

	Object.defineProperty(o, "foo", { configurable: true, value: 4 });

	Test.expect(4, o.foo);
	Test.expect(5, (o.foo = 5));
	Test.expect(4, (o.foo = 4));
}

// define symbol accessor
{
	let o = {};

	Object.defineProperty(o, s, {
		configurable: true,
		get() {
			return o.secret_foo + 1;
		},
		set(value) {
			this.secret_foo = value + 1;
		},
	});

	o[s] = 10;
	Test.expect(12, o[s]);
	o[s] = 20;
	Test.expect(22, o[s]);

	Object.defineProperty(o, s, { configurable: true, value: 4 });

	Test.expect(4, o[s]);
	Test.expect(5, (o[s] = 5));
	Test.expect(4, (o[s] = 4));
}

{
	const o = {};
	for (let i = 0; i < 101; ++i) o[`property${i}`] = i;
	Object.defineProperty(o, "x", { configurable: true });
	Object.defineProperty(o, "x", { configurable: false });
}

// errors

// redefine non-configurable property
{
	let o = {};
	Object.defineProperty(o, "foo", { value: 1, writable: true, enumerable: true });
	Test.expectError("TypeError", "Object's [[DefineOwnProperty]] method returned false", () => Object.defineProperty(o, "foo", { value: 2, writable: true, enumerable: false }));
}

// redefine non-configurable symbol property
{
	let o = {};
	let s = Symbol("foo");
	Object.defineProperty(o, s, { value: 1, writable: true, enumerable: true });
	Test.expectError("TypeError", "Object's [[DefineOwnProperty]] method returned false", () => Object.defineProperty(o, s, { value: 2, writable: true, enumerable: false }));
}

// cannot define 'value' and 'get' in the same descriptor
{
	let o = {};
	Test.expectError("TypeError", "Accessor property descriptor cannot specify a value or writable key", () => Object.defineProperty(o, "a", { get() { }, value: 9, }));
}

// cannot define 'value' and 'set' in the same descriptor
{
	let o = {};
	Test.expectError("TypeError", "Accessor property descriptor cannot specify a value or writable key", () => Object.defineProperty(o, "a", { set() { }, writable: true, }));
}

// redefine non-configurable accessor
{
	let o = {};

	Object.defineProperty(o, "foo", {
		configurable: false,
		get() {
			return this.secret_foo + 2;
		},
		set(value) {
			o.secret_foo = value + 2;
		},
	});

	Test.expectError("TypeError", "Object's [[DefineOwnProperty]] method returned false", () => Object.defineProperty(o, "foo", { configurable: false, get() { return this.secret_foo + 2; }, }));
}