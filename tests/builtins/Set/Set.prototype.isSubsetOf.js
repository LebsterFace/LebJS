Test.expect(1, Set.prototype.isSubsetOf.length);
const set1 = new Set(["a", "b", "c"]);
const set2 = new Set(["b", "c"]);
Test.expect(false, set1.isSubsetOf(set2));
Test.expect(true, set2.isSubsetOf(set1));