expect("", "".concat())
expect("a", "".concat("a"))
expect("ab", "".concat("a", "b"))
expect("ab", "a".concat("b"))
expect("a1b2", "a".concat(1, "b").concat(2))
expect("1", "".concat(1));
expect("321", "".concat(3, 2, 1));
expect("hello world", "hello".concat(" ", "world"));
expect("null", "".concat(null));
expect("false", "".concat(false));
expect("true", "".concat(true));
expect("", "".concat([]));
expect("1,2,3,hello", "".concat([1, 2, 3, "hello"]));
expect("true", "".concat(true, []));
expect("truefalse", "".concat(true, false));
expect("[object Object]", "".concat({}));
expect("1[object Object]", "".concat(1, {}));
expect("1[object Object]false", "".concat(1, {}, false));
expect("😀", "😀".concat());
expect("😀a", "😀".concat("a"));
expect("😀a4", "😀".concat("a", 4));
expect("😀a😀", "😀".concat("a", "😀"));