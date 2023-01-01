Test.expect(1, Set.prototype.add.length);

const set = new Set(["a", "b", "c"]);
Test.expect(3, set.size);
Test.expect(set, set.add("d"));
Test.expect(4, set.size);
Test.expect(set, set.add("a"));
Test.expect(4, set.size);

// TODO: elements added after iteration start are still visited