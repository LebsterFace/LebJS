Test.expect(0, Set.prototype.clear.length);
const set = new Set(["a", "b", "c"]);
Test.expect(3, set.size);
set.clear();
Test.expect(0, set.size);