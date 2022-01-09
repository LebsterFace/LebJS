// prototype method
expect("321", "123".reverse());

// String prototype
let sproto = Object.getPrototypeOf("123");
expect(Object.getPrototypeOf(""), sproto);

// Object prototype
let oproto = Object.getPrototypeOf({});
expect(oproto, Object.getPrototypeOf(sproto));