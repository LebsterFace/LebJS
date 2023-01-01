Test.expect(1, Set.prototype.intersection.length);

const set1 = new Set(["a", "b", "c"]);
const set2 = new Set(["b", "c", "d", "e"]);

const intersection1to2 = set1.intersection(set2);
Test.expect(2, intersection1to2.size);
["b", "c"].forEach(value => {
    Test.expect(true, intersection1to2.has(value));
});

const intersection2to1 = set2.intersection(set1);
Test.expect(2, intersection2to1.size);
["b", "c"].forEach(value => {
    Test.expect(true, intersection2to1.has(value));
});