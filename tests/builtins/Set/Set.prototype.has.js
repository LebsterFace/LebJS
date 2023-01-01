Test.expect(1, Set.prototype.has.length);
// basic functionality
{
    const set = new Set(["hello", "there", 1, 2, false]);
    Test.expect(false, new Set().has());
    Test.expect(true, new Set([undefined]).has());
    Test.expect(true, set.has("hello"));
    Test.expect(true, set.has(1));
    Test.expect(false, set.has("world"));
}

// NaN differentiation
{
    const set = new Set();
    set.add(NaN);
    Test.expect(true, set.has(0 / 0));
    Test.expect(true, set.has(0 * Infinity));
    Test.expect(true, set.has(Infinity - Infinity));
}
