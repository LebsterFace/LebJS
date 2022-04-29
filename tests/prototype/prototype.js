// prototype method
Test.expect("321", "123".reverse());

// String prototype
let StringPrototype = Object.getPrototypeOf("123");
Test.expect(Object.getPrototypeOf(""), StringPrototype);
Test.expect(String.prototype, StringPrototype);
Test.expect(String.prototype.reverse, "123".reverse)
Test.expect(String.prototype.reverse, StringPrototype.reverse)

// Object prototype
let ObjectPrototype = Object.getPrototypeOf({});
Test.expect(ObjectPrototype, Object.getPrototypeOf(StringPrototype));
Test.expect(Object.prototype, ObjectPrototype)