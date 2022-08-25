let s = "foobar";
Test.expect("f", s.charAt(0));
Test.expect("o", s.charAt(1));
Test.expect("o", s.charAt(2));
Test.expect("b", s.charAt(3));
Test.expect("a", s.charAt(4));
Test.expect("r", s.charAt(5));
Test.expect("", s.charAt(6));
Test.expect("f", s.charAt());
Test.expect("f", s.charAt(NaN));
Test.expect("f", s.charAt("foo"));
Test.expect("f", s.charAt(undefined));
s = "😀";
Test.expect("\ud83d", s.charAt(0));
Test.expect("\ude00", s.charAt(1));
Test.expect("", s.charAt(2));