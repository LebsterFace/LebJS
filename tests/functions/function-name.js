function xyz() {}
Test.expect("xyz", xyz.name);
Test.expect("function xyz() {\n}", xyz.toString());
Test.expect("slice", String.prototype.slice.name);
Test.expect("expect", Test.expect.name);