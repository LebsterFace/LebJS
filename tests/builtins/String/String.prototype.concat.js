Test.expect("", "".concat())
Test.expect("a", "".concat("a"))
Test.expect("ab", "".concat("a", "b"))
Test.expect("ab", "a".concat("b"))
Test.expect("a1b2", "a".concat(1, "b").concat(2))
Test.expect("1", "".concat(1));
Test.expect("321", "".concat(3, 2, 1));
Test.expect("hello world", "hello".concat(" ", "world"));
Test.expect("null", "".concat(null));
Test.expect("false", "".concat(false));
Test.expect("true", "".concat(true));
Test.expect("", "".concat([]));
Test.expect("1,2,3,hello", "".concat([1, 2, 3, "hello"]));
Test.expect("true", "".concat(true, []));
Test.expect("truefalse", "".concat(true, false));
Test.expect("[object Object]", "".concat({}));
Test.expect("1[object Object]", "".concat(1, {}));
Test.expect("1[object Object]false", "".concat(1, {}, false));
Test.expect("😀", "😀".concat());
Test.expect("😀a", "😀".concat("a"));
Test.expect("😀a4", "😀".concat("a", 4));
Test.expect("😀a😀", "😀".concat("a", "😀"));