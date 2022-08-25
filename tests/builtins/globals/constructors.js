Test.expect(true, "String" in globalThis);
Test.expect(true, "Number" in globalThis);
Test.expect(true, "Boolean" in globalThis);
Test.expect(true, "Symbol" in globalThis);
Test.expect(true, "Object" in globalThis);
Test.expect(true, "ShadowRealm" in globalThis);

Test.expect(String, String.prototype.constructor);
Test.expect(Number, Number.prototype.constructor);
Test.expect(Boolean, Boolean.prototype.constructor);
Test.expect(Object, Object.prototype.constructor);
Test.expect(Array, Array.prototype.constructor);
Test.expect(Function, Function.prototype.constructor);

Test.expect("String", String.name);
Test.expect("Number", Number.name);
Test.expect("Boolean", Boolean.name);
Test.expect("Symbol", Symbol.name);
Test.expect("Object", Object.name);
Test.expect("ShadowRealm", ShadowRealm.name);

Test.expect(String(true), 'true');
Test.expect(String(false), 'false');
Test.expect(String(null), 'null');
Test.expect(String(undefined), 'undefined');
Test.expect(String(0), '0');
Test.expect(String, String.prototype.constructor);

Test.expect(Number(true), 1);
Test.expect(Number(false), 0);
Test.expect(Number(null), 0);
Test.expect(Number(undefined), NaN);
Test.expect(Number(0), 0);
Test.expect(Number("21"), 21);
Test.expect(Number("21.5"), 21.5);
Test.expect(Number("hello"), NaN);
Test.expect(Number, Number.prototype.constructor);

Test.expect(Boolean(true), true);
Test.expect(Boolean(false), false);
Test.expect(Boolean(null), false);
Test.expect(Boolean(undefined), false);
Test.expect(Boolean(0), false);
Test.expect(Boolean(""), false);
Test.expect(Boolean("hello"), true);
Test.expect(Boolean, Boolean.prototype.constructor);

Test.expect(Symbol, Symbol.prototype.constructor);

Test.expect(true, Object(true) instanceof Boolean);
Test.expect(true, Object(true).valueOf());
Test.expect(true, Object(null) instanceof Object);
Test.expect(true, Object(undefined) instanceof Object);
Test.expect(true, Object(0) instanceof Number);
Test.expect(0, Object(0).valueOf());
Test.expect(true, Object("hello") instanceof String);
Test.expect("hello", Object("hello").valueOf());
const symb = Symbol();
Test.expect(true, Object(symb) instanceof Symbol);
Test.expect(symb, Object(symb).valueOf());
const obj = [];
Test.expect(obj, Object(obj));
Test.expect(Object, Object.prototype.constructor);

Test.expect(ShadowRealm, ShadowRealm.prototype.constructor);