Test.expect(NaN, {} - 10);
Test.expect(90, { toString: () => "100" } - 10);
Test.expect(990, { valueOf: () => "1000" } - 10);
Test.expect(0, { toString: () => "50", valueOf: () => "10" } - 10);
Test.expect(true, new Boolean(true).valueOf());
Test.expect(false, new Boolean(false).valueOf());
Test.expect("hello", new String("hello").valueOf());