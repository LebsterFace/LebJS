// prototype method
Test.expect(true, "123".includes('2'));

// String prototype
let StringPrototype = Object.getPrototypeOf("123");
Test.expect(Object.getPrototypeOf(""), StringPrototype);
Test.expect(String.prototype, StringPrototype);
Test.expect(String.prototype.includes, "123".includes)
Test.expect(String.prototype.includes, StringPrototype.includes)

// Object prototype
let ObjectPrototype = Object.getPrototypeOf({});
Test.expect(ObjectPrototype, Object.getPrototypeOf(StringPrototype));
Test.expect(Object.prototype, ObjectPrototype)