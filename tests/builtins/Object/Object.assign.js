Test.expect(2, Object.assign.length);

// first argument must coercible to object
Test.expectError("TypeError", "Cannot convert null to object", () => Object.assign(null));
Test.expectError("TypeError", "Cannot convert undefined to object", () => Object.assign(undefined));

// returns first argument coerced to object
let o = {};
Test.expect(o, Object.assign(o));
Test.expect(o, Object.assign(o, {}));
Test.expectEqual(Object(42), Object.assign(42));

// alters first argument object if sources are given
o = { foo: 0 };
Test.expect(o, Object.assign(o, { foo: 1 }));
Test.expectEqual({ foo: 1 }, o);

// merges objects
const s = Symbol();
const result = Object.assign({},
    { foo: 0, bar: "baz" },
    { [s]: [1, 2, 3] },
    { foo: 1 },
    { [42]: "test" }
);

Test.expectEqual({ foo: 1, bar: "baz", [s]: [1, 2, 3], 42: "test" }, result);
