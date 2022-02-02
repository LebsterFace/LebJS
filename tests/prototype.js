// prototype method
expect("321", "123".reverse());

// String prototype
let StringPrototype = Object.getPrototypeOf("123");
expect(Object.getPrototypeOf(""), StringPrototype);
expect(String.prototype, StringPrototype);
expect(String.prototype.reverse, "123".reverse)
expect(String.prototype.reverse, StringPrototype.reverse)

// Object prototype
let ObjectPrototype = Object.getPrototypeOf({});
expect(ObjectPrototype, Object.getPrototypeOf(StringPrototype));
expect(Object.prototype, ObjectPrototype)