Test.expect(1, Set.prototype.delete.length);
const set = new Set(["a", "b", "c"]);
Test.expect(3, set.size);
Test.expect(true, set.delete("b"));
Test.expect(2, set.size);
Test.expect(false, set.delete("b"));
Test.expect(2, set.size);