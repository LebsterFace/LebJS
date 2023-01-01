Test.expect(1, Set.prototype.union.length);

const set1 = new Set(["a", "b", "c"]);
const set2 = new Set(["b", "c", "d"]);

const union1to2 = set1.union(set2);
Test.expect(4, union1to2.size);
["a", "b", "c", "d"].forEach(value => {
    Test.expect(true, union1to2.has(value));
});

const union2to1 = set2.union(set1);
Test.expect(4, union2to1.size);
["a", "b", "c", "d"].forEach(value => {
    Test.expect(true, union2to1.has(value));
});
