Test.expect(1, Set.prototype.isSupersetOf.length);
const set1 = new Set(["a", "b", "c"]);
const set2 = new Set(["b", "c"]);
Test.expect(true, set1.isSupersetOf(set2));
Test.expect(false, set2.isSupersetOf(set1));