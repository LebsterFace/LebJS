Test.expect(1, Set.prototype.difference.length);

const set1 = new Set(["a", "b", "c"]);
const set2 = new Set(["b", "c", "d", "e"]);
const difference1to2 = set1.difference(set2);
Test.expect(1, difference1to2.size);
Test.expect(true, difference1to2.has("a"));
const difference2to1 = set2.difference(set1);
Test.expect(2, difference2to1.size);
for (const value of ["d", "e"]) {
    Test.expect(true, difference2to1.has(value));
}