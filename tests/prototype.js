// prototype method
expect("321", "123".reverse());

// String prototype
let sproto = __proto__("123");
expect(__proto__(""), sproto);

// Object prototype
let oproto = __proto__(createObject());
expect(oproto, __proto__(sproto));