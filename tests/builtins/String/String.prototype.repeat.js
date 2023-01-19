// basic functionality
Test.expect(1, String.prototype.repeat.length);
Test.expect("", "foo".repeat(0));
Test.expect("foo", "foo".repeat(1));
Test.expect("foofoo", "foo".repeat(2));
Test.expect("foofoofoo", "foo".repeat(3));
Test.expect("foofoofoo", "foo".repeat(3.1));
Test.expect("foofoofoo", "foo".repeat(3.5));
Test.expect("foofoofoo", "foo".repeat(3.9));
Test.expect("", "foo".repeat(null));
Test.expect("", "foo".repeat(undefined));
Test.expect("", "foo".repeat([]));
Test.expect("", "foo".repeat(""));

// throws correct range errors
Test.expectError("RangeError", "Invalid count value: -1", () => "foo".repeat(-1));
Test.expectError("RangeError", "Invalid count value: Infinity", () => "foo".repeat(Infinity));

// UTF-16
Test.expect("", "😀".repeat(0));
Test.expect("😀", "😀".repeat(1));
Test.expect("😀😀😀😀😀😀😀😀😀😀", "😀".repeat(10));
