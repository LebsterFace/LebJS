function xyz() {}
expect("xyz", xyz.name);
expect("function xyz() {\n}", xyz.toString());
expect("slice", String.prototype.slice.name);
expect("expect", expect.name);