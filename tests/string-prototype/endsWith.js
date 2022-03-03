expect(true, "".endsWith(""))
expect(false, "".endsWith("a"))
expect(true, "a".endsWith("a"))
expect(true, "ab".endsWith("b"))
expect(true, "ab".endsWith("ab"))
expect(false, "ab".endsWith("c"))
expect(true, "ab".endsWith(""))
expect(true, "ab1".endsWith(1))
expect(false, "ab2".endsWith(1))
expect(false, "ab".endsWith())

let s = "foobar";
expect(true, s.endsWith("r"));
expect(true, s.endsWith("ar"));
expect(true, s.endsWith("bar"));
expect(true, s.endsWith("obar"));
expect(true, s.endsWith("oobar"));
expect(true, s.endsWith("foobar"));
expect(false, s.endsWith("1foobar"));
expect(true, s.endsWith("r", 6));
expect(true, s.endsWith("ar", 6));
expect(true, s.endsWith("bar", 6));
expect(true, s.endsWith("obar", 6));
expect(true, s.endsWith("oobar", 6));
expect(true, s.endsWith("foobar", 6));
expect(false, s.endsWith("1foobar", 6));
expect(false, s.endsWith("bar", []));
expect(false, s.endsWith("bar", null));
expect(false, s.endsWith("bar", false));
expect(false, s.endsWith("bar", true));
expect(true, s.endsWith("f", true));
expect(false, s.endsWith("bar", -1));
expect(true, s.endsWith("bar", 42));
expect(true, s.endsWith("foo", 3));
expect(true, s.endsWith("foo", "3"));
expect(false, s.endsWith("foo1", 3));
expect(true, s.endsWith("foo", 3.7));
expect(false, s.endsWith());
expect(true, s.endsWith(""));
expect(true, s.endsWith("", 0));
expect(true, s.endsWith("", 1));
expect(true, s.endsWith("", -1));
expect(true, s.endsWith("", 42));
expect(true, "12undefined".endsWith());
expect(true, s.endsWith("bar", undefined));
s = "😀";
expect(true, s.endsWith("😀"));
expect(false, s.endsWith("\ud83d"));
expect(true, s.endsWith("\ude00"));
expect(false, s.endsWith("a"));