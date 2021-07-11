// prototype method
expect("321", "123".reverse());
// String prototype
expect(__proto__(""), __proto__("123"));
// Object prototype
expect(__proto__(createObject()), __proto__(__proto__("123")));