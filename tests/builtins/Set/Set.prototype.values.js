// length
Test.expect(0, Set.prototype.values.length);

// basic functionality
{
    const a = new Set([1, 2, 3]);
    const it = a.values();
    Test.equals({ value: 1, done: false }, it.next());
    Test.equals({ value: 2, done: false }, it.next());
    Test.equals({ value: 3, done: false }, it.next());
    Test.equals({ value: undefined, done: true }, it.next());
    Test.equals({ value: undefined, done: true }, it.next());
    Test.equals({ value: undefined, done: true }, it.next());
}

// aliases
Test.expect(Set.prototype.values, Set.prototype.keys);
Test.expect(Set.prototype.values, Set.prototype[Symbol.iterator]);

// basic functionality
{
    const a = new Set([1, 2, 3]);
    const keys_it = a.keys();
    const it = a[Symbol.iterator]();
    Test.equals({ value: 1, done: false }, keys_it.next());
    Test.equals({ value: 1, done: false }, it.next());
    Test.equals({ value: 2, done: false }, keys_it.next());
    Test.equals({ value: 2, done: false }, it.next());
    Test.equals({ value: 3, done: false }, keys_it.next());
    Test.equals({ value: 3, done: false }, it.next());
    Test.equals({ value: undefined, done: true }, it.next());
    Test.equals({ value: undefined, done: true }, keys_it.next());
    Test.equals({ value: undefined, done: true }, it.next());
    Test.equals({ value: undefined, done: true }, keys_it.next());
    Test.equals({ value: undefined, done: true }, it.next());
    Test.equals({ value: undefined, done: true }, keys_it.next());
}

// elements added after iteration start are still visited
// element added after iterator
{
    const set = new Set();
    const iterator = set.values();
    set.add(1);
    Test.equals({ done: false, value: 1 }, iterator.next());
    Test.equals({ done: true, value: undefined }, iterator.next());
    Test.equals({ done: true, value: undefined }, iterator.next());
}

// elements (re)added after deleting
{
    const set = new Set();
    const iterator1 = set.values();
    set.add(1);
    set.add(2);
    set.clear();
    const iterator2 = set.values();
    set.add(1);
    Test.equals({ done: false, value: 1 }, iterator1.next());
    Test.equals({ done: true, value: undefined }, iterator1.next());
    Test.equals({ done: true, value: undefined }, iterator1.next());

    Test.equals({ done: false, value: 1 }, iterator2.next());
    Test.equals({ done: true, value: undefined }, iterator2.next());
    Test.equals({ done: true, value: undefined }, iterator2.next());
}