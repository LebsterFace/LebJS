Test.expect(true, "".endsWith(""))
Test.expect(false, "".endsWith("a"))
Test.expect(true, "a".endsWith("a"))
Test.expect(true, "ab".endsWith("b"))
Test.expect(true, "ab".endsWith("ab"))
Test.expect(false, "ab".endsWith("c"))
Test.expect(true, "ab".endsWith(""))
Test.expect(true, "ab1".endsWith(1))
Test.expect(false, "ab2".endsWith(1))
Test.expect(false, "ab".endsWith())

let s = "foobar";
Test.expect(true, s.endsWith("r"));
Test.expect(true, s.endsWith("ar"));
Test.expect(true, s.endsWith("bar"));
Test.expect(true, s.endsWith("obar"));
Test.expect(true, s.endsWith("oobar"));
Test.expect(true, s.endsWith("foobar"));
Test.expect(false, s.endsWith("1foobar"));
Test.expect(true, s.endsWith("r", 6));
Test.expect(true, s.endsWith("ar", 6));
Test.expect(true, s.endsWith("bar", 6));
Test.expect(true, s.endsWith("obar", 6));
Test.expect(true, s.endsWith("oobar", 6));
Test.expect(true, s.endsWith("foobar", 6));
Test.expect(false, s.endsWith("1foobar", 6));
Test.expect(false, s.endsWith("bar", []));
Test.expect(false, s.endsWith("bar", null));
Test.expect(false, s.endsWith("bar", false));
Test.expect(false, s.endsWith("bar", true));
Test.expect(true, s.endsWith("f", true));
Test.expect(false, s.endsWith("bar", -1));
Test.expect(true, s.endsWith("bar", 42));
Test.expect(true, s.endsWith("foo", 3));
Test.expect(true, s.endsWith("foo", "3"));
Test.expect(false, s.endsWith("foo1", 3));
Test.expect(true, s.endsWith("foo", 3.7));
Test.expect(false, s.endsWith());
Test.expect(true, s.endsWith(""));
Test.expect(true, s.endsWith("", 0));
Test.expect(true, s.endsWith("", 1));
Test.expect(true, s.endsWith("", -1));
Test.expect(true, s.endsWith("", 42));
Test.expect(true, "12undefined".endsWith());
Test.expect(true, s.endsWith("bar", undefined));
s = "😀";
Test.expect(true, s.endsWith("😀"));
Test.expect(false, s.endsWith("\ud83d"));
Test.expect(true, s.endsWith("\ude00"));
Test.expect(false, s.endsWith("a"));