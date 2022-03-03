expect("", String());
expect("", new String().valueOf());
expect("foo", String("foo"));
expect("foo", new String("foo").valueOf());
expect("123", String(123));
expect("123", new String(123).valueOf());
expect("123", String(123));
expect("123", new String(123).valueOf());