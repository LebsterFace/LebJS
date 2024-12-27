// correct behavior
Test.expect(3, JSON.stringify.length);

// basic functionality
Test.expect("5", JSON.stringify(5));
Test.expect(undefined, JSON.stringify(undefined));
Test.expect("null", JSON.stringify(null));
Test.expect("null", JSON.stringify(NaN));
Test.expect("null", JSON.stringify(-NaN));
Test.expect("null", JSON.stringify(Infinity));
Test.expect("null", JSON.stringify(-Infinity));
Test.expect("true", JSON.stringify(true));
Test.expect("false", JSON.stringify(false));
Test.expect('"test"', JSON.stringify("test"));
Test.expect("5", JSON.stringify(Object(5)));
Test.expect("false", JSON.stringify(Object(false)));
Test.expect('"test"', JSON.stringify(Object("test")));
Test.expect(undefined, JSON.stringify(() => { }));
Test.expect('[1,2,"foo"]', JSON.stringify([1, 2, "foo"]));
// FIXME: Correct ordering: Test.expect('{"foo":1,"bar":"baz"}', JSON.stringify({ foo: 1, bar: "baz", qux() { } }));
Test.expect('{"bar":"baz","foo":1}', JSON.stringify({ foo: 1, bar: "baz", qux() { } }));
// FIXME: Correct ordering: Test.expect('{"var1":1,"var2":10}', JSON.stringify({
Test.expect('{"var2":10,"var1":1}', JSON.stringify({
	var1: 1,
	var2: 2, toJSON(key) {
		let o = this;
		o.var2 = 10;
		return o;
	}
},));


/* TODO: serialize BigInt with a toJSON property
Object.defineProperty(BigInt.prototype, "toJSON", {
	configurable: true, // Allows deleting this property at the end of this test case.
	get() {
		"use strict";
		return () => typeof this;
	},
});

Test.expect('"bigint"', JSON.stringify(1n));
delete BigInt.prototype.toJSON; */

/* TODO: ignores non-enumerable properties
let o = { foo: "bar" };
Object.defineProperty(o, "baz", { value: "qux", enumerable: false });
Test.expect('{"foo":"bar"}', JSON.stringify(o)); */

// ignores symbol properties
let o = { foo: "bar" };
let sym = Symbol("baz");
o[sym] = "qux";
Test.expect('{"foo":"bar"}', JSON.stringify(o));

// escape surrogate codepoints in strings
Test.expect('"😄"', JSON.stringify("\ud83d\ude04"));
Test.expect('"\\ud83d"', JSON.stringify("\ud83d"));
Test.expect('"\\ude04"', JSON.stringify("\ude04"));
Test.expect('"\\ud83d😄😄\\ude04"', JSON.stringify("\ud83d\ud83d\ude04\ud83d\ude04\ude04"));
Test.expect('"\\ude04😄😄\\ud83d"', JSON.stringify("\ude04\ud83d\ude04\ud83d\ude04\ud83d"));

// errors
// cannot serialize BigInt
Test.expectError("TypeError", "Cannot serialize BigInt value to JSON", () => JSON.stringify(5n));

// cannot serialize circular structures
let bad1 = {};
bad1.foo = bad1;
Test.expectError("TypeError", "Cannot stringify circular object", () => JSON.stringify(bad1));


let bad2 = [];
bad2[5] = [[[bad2]]];
Test.expectError("TypeError", "Cannot stringify circular object", () => JSON.stringify(bad2));

let bad3a = { foo: "bar" };
let bad3b = [1, 2, bad3a];
bad3a.bad = bad3b;
Test.expectError("TypeError", "Cannot stringify circular object", () => JSON.stringify(bad3a));

o = {
	foo: 1,
	bar: "baz",
	qux: {
		get x() {
			return 10;
		},
		y() {
			return 20;
		},
		arr: [1, 2, 3],
	},
};

/* FIXME: Correct ordering:
Test.expect(`{
    "foo": 1,
    "bar": "baz",
    "qux": {
        "x": 10,
        "arr": [
            1,
            2,
            3
        ]
    }
}`, JSON.stringify(o, null, 4)); */
Test.expect(JSON.stringify(o, null, 4), `{
    "bar": "baz",
    "qux": {
        "arr": [
            1,
            2,
            3
        ],
        "x": 10
    },
    "foo": 1
}`.replaceAll("\r\n", "\n"));

Test.expect(JSON.stringify(o, null, "abcd"), `{
abcd"bar": "baz",
abcd"qux": {
abcdabcd"arr": [
abcdabcdabcd1,
abcdabcdabcd2,
abcdabcdabcd3
abcdabcd],
abcdabcd"x": 10
abcd},
abcd"foo": 1
}`.replaceAll("\r\n", "\n"));

o = {
	var1: "foo",
	var2: 42,
	arr: [
		1,
		2,
		{
			nested: {
				hello: "world",
			},
			get x() {
				return 10;
			},
		},
	],
	obj: {
		subarr: [3],
	},
};

const string = JSON.stringify(o, (key, value) => {
	if (key === "hello") return undefined;
	if (value === 10) return 20;
	if (key === "subarr") return [3, 4, 5];
	return value;
});

// FIXME: Correct ordering: Test.expect('{"var1":"foo","var2":42,"arr":[1,2,{"nested":{},"x":20}],"obj":{"subarr":[3,4,5]}}', string);
Test.expect('{"arr":[1,2,{"x":20,"nested":{}}],"obj":{"subarr":[3,4,5]},"var2":42,"var1":"foo"}', string);

// FIXME: Correct ordering: Test.expect('{"var1":"foo","var2":42,"obj":{}}', string);
Test.expect('{"obj":{},"var2":42,"var1":"foo"}', JSON.stringify(o, ["var1", "var1", "var2", "obj"]));

// FIXME: Correct ordering: Test.expect('{"var1":"foo","var2":42,"obj":{"subarr":[3]}}', JSON.stringify(o, ["var1", "var1", "var2", "obj", "subarr"]));
Test.expect('{"obj":{"subarr":[3]},"var2":42,"var1":"foo"}', JSON.stringify(o, ["var1", "var1", "var2", "obj", "subarr"]));

/* TODO:
let p = new Proxy([], {
	get(_, key) {
		if (key === "length") return 3;
		return Number(key);
	},
});

Test.expect("[0,1,2]", JSON.stringify(p));
Test.expect("[[[0,1,2]]]", JSON.stringify([[new Proxy(p, {})]]));  */

/* TODO:
o = { key1: "key1", key2: "key2", key3: "key3" };
Object.defineProperty(o, "defined", {
	enumerable: true,
	get() {
		o.prop = "prop";
		return "defined";
	}
});

o.key4 = "key4";

o[2] = 2;
o[0] = 0;
o[1] = 1;

delete o.key1;
delete o.key3;

o.key1 = "key1";

Test.expect('{"0":0,"1":1,"2":2,"key2":"key2","defined":"defined","key4":"key4","key1":"key1"}', JSON.stringify(o));
*/

o = {
	get foo() {
		throw Error("Fake error");
	}
};

Test.expectError("Error", "Fake error", () => JSON.stringify(o, (_, value) => value));