Test.expect(1, Set.prototype.isDisjointFrom.length);

const set1 = new Set(["a", "b"]);
const set2 = new Set(["c"]);
Test.expect(false, set1.isDisjointFrom(set1));
Test.expect(true, set1.isDisjointFrom(set2));
Test.expect(true, set2.isDisjointFrom(set1));