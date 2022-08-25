Test.expect("", String());
Test.expect("", new String().valueOf());
Test.expect("foo", String("foo"));
Test.expect("foo", new String("foo").valueOf());
Test.expect("123", String(123));
Test.expect("123", new String(123).valueOf());
Test.expect("123", String(123));
Test.expect("123", new String(123).valueOf());