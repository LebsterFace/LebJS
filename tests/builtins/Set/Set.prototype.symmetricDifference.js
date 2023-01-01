Test.expect(1, Set.prototype.symmetricDifference.length);

const set1 = new Set(["a", "b", "c"]);
const set2 = new Set(["b", "c", "d", "e"]);

const symmetricDifference1to2 = set1.symmetricDifference(set2);
Test.expect(3, symmetricDifference1to2.size);
["a", "d", "e"].forEach(value => {
    Test.expect(true, symmetricDifference1to2.has(value));
});

const symmetricDifference2to1 = set2.symmetricDifference(set1);
Test.expect(3, symmetricDifference2to1.size);
["a", "d", "e"].forEach(value => {
    Test.expect(true, symmetricDifference2to1.has(value));
});