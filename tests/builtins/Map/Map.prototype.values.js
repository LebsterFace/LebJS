// length
Test.expect(0, Map.prototype.values.length);

// basic functionality
{
    const original = [
        ["a", 0],
        ["b", 1],
        ["c", 2],
    ];

    const a = new Map(original);
    const it = a.values();
    Test.equals({ value: 0, done: false }, it.next());
    Test.equals({ value: 1, done: false }, it.next());
    Test.equals({ value: 2, done: false }, it.next());
    Test.equals({ value: undefined, done: true }, it.next());
    Test.equals({ value: undefined, done: true }, it.next());
    Test.equals({ value: undefined, done: true }, it.next());
}

// empty maps give no values
// always empty
{
	const map = new Map();
	const iterator = map.values();

	Test.equals({ done: true, value: undefined }, iterator.next());
	Test.equals({ done: true, value: undefined }, iterator.next());
}

// just emptied map
{
	const map = new Map([
		[1, 2],
		[3, 4],
	]);

	const iterator = map.values();

	Test.expect(true, map.delete(1));
	Test.expect(true, map.delete(3));

	Test.equals({ done: true, value: undefined }, iterator.next());
	Test.equals({ done: true, value: undefined }, iterator.next());
}

// cleared map
{
	const map = new Map([
		[1, 2],
		[3, 4],
	]);

	const iterator = map.values();

	map.clear();

	Test.equals({ done: true, value: undefined }, iterator.next());
	Test.equals({ done: true, value: undefined }, iterator.next());
}

// added and then removed elements
{
	const map = new Map([[1, 2]]);

	const iterator = map.values();

	map.set(3, 4);

	map.delete(3);
	map.set(5, 6);
	map.delete(1);
	map.set(1, 4);
	map.delete(5);
	map.delete(1);

	Test.expect(0, map.size);

	Test.equals({ done: true, value: undefined }, iterator.next());
	Test.equals({ done: true, value: undefined }, iterator.next());
}