// basic functionality
Test.expect(2, Map.prototype.set.length);

{
    const map = new Map([
        ["a", 0],
        ["b", 1],
        ["c", 2],
    ]);
    Test.expect(3, map.size);
    Test.expect(map, map.set("d", 3));
    Test.expect(4, map.size);
    Test.expect(map, map.set("a", -1));
    Test.expect(4, map.size);
}

// modification with active iterators
// added element is visited (after initial elements)
{
	const map = new Map([
		[1, 2],
		[5, 6],
	]);
	const iterator = map.entries();

	Test.equals({ done: false, value: [1, 2] }, iterator.next());

	map.set(3, 4);

	Test.equals({ done: false, value: [5, 6] }, iterator.next());

	Test.equals({ done: false, value: [3, 4] }, iterator.next());

	Test.equals({ done: true, value: undefined }, iterator.next());
	Test.equals({ done: true, value: undefined }, iterator.next());
}

// entries added after iterator is done are not visited
{
	const map = new Map([[1, 2]]);

	const iterator = map.entries();

	Test.equals({ done: false, value: [1, 2] }, iterator.next());

	Test.equals({ done: true, value: undefined }, iterator.next());

	map.set(3, 4);

	Test.equals({ done: true, value: undefined }, iterator.next());
}

// entries which are deleted and then added are visited at the end
{
	const map = new Map([
		[1, 2],
		[3, 4],
	]);

	const iterator = map.entries();

	Test.equals({ done: false, value: [1, 2] }, iterator.next());

	Test.expect(true, map.delete(1));
	map.set(1, 10);

	Test.expect(true, map.delete(3));
	map.set(3, 11);

	Test.equals({ done: false, value: [1, 10] }, iterator.next());

	Test.equals({ done: false, value: [3, 11] }, iterator.next());

	Test.equals({ done: true, value: undefined }, iterator.next());
	Test.equals({ done: true, value: undefined }, iterator.next());
}

// entries which added to empty map after iterator created are still visited
{
	const map = new Map();

	const iteratorImmediateDone = map.entries();
	Test.equals({ done: true, value: undefined }, iteratorImmediateDone.next());

	const iterator = map.entries();

	map.set(1, 2);

	Test.equals({ done: false, value: [1, 2] }, iterator.next());

	Test.expect(true, map.delete(1));

	map.set(3, 4);

	Test.equals({ done: false, value: [3, 4] }, iterator.next());

	Test.equals({ done: true, value: undefined }, iterator.next());
	Test.equals({ done: true, value: undefined }, iterator.next());
}