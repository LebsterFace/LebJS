Test.expect(1, String.prototype.padEnd.length);

let s = "foo";
Test.expect("foo", s.padEnd(-1));
Test.expect("foo", s.padEnd(0));
Test.expect("foo", s.padEnd(3));
Test.expect("foo  ", s.padEnd(5));
Test.expect("foo       ", s.padEnd(10));
Test.expect("foo  ", s.padEnd("5"));
Test.expect("foo  ", s.padEnd([[["5"]]]));
Test.expect("foo", s.padEnd(2, "+"));
Test.expect("foo++", s.padEnd(5, "+"));
Test.expect("foo11", s.padEnd(5, 1));
Test.expect("foonullnul", s.padEnd(10, null));
Test.expect("foobarbarb", s.padEnd(10, "bar"));
Test.expect("foo1234567", s.padEnd(10, "123456789"));

s = "😀";
Test.expect(2, s.length);
Test.expect("😀", s.padEnd(-1));
Test.expect("😀", s.padEnd(0));
Test.expect("😀", s.padEnd(1));
Test.expect("😀", s.padEnd(2));
Test.expect("😀 ", s.padEnd(3));
Test.expect("😀        ", s.padEnd(10));

Test.expect("😀", s.padEnd(2, "😀"));
Test.expect("😀\ud83d", s.padEnd(3, "😀"));
Test.expect("😀😀", s.padEnd(4, "😀"));
Test.expect("😀😀\ud83d", s.padEnd(5, "😀"));