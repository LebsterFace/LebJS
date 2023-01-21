Test.expect(1, String.prototype.padStart.length);

let s = "foo";
Test.expect("foo", s.padStart(-1));
Test.expect("foo", s.padStart(0));
Test.expect("foo", s.padStart(3));
Test.expect("  foo", s.padStart(5));
Test.expect("       foo", s.padStart(10));
Test.expect("  foo", s.padStart("5"));
Test.expect("  foo", s.padStart([[["5"]]]));
Test.expect("foo", s.padStart(2, "+"));
Test.expect("++foo", s.padStart(5, "+"));
Test.expect("11foo", s.padStart(5, 1));
Test.expect("nullnulfoo", s.padStart(10, null));
Test.expect("barbarbfoo", s.padStart(10, "bar"));
Test.expect("1234567foo", s.padStart(10, "123456789"));

s = "😀";
Test.expect("😀", s.padStart(-1));
Test.expect("😀", s.padStart(0));
Test.expect("😀", s.padStart(1));
Test.expect("😀", s.padStart(2));
Test.expect(" 😀", s.padStart(3));
Test.expect("        😀", s.padStart(10));

Test.expect("😀", s.padStart(2, "😀"));
Test.expect("\ud83d😀", s.padStart(3, "😀"));
Test.expect("😀😀", s.padStart(4, "😀"));
Test.expect("😀\ud83d😀", s.padStart(5, "😀"));