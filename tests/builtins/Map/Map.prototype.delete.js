// basic functionality
Test.expect(1, Map.prototype.delete.length);
{
    const map = new Map([
        ["a", 0],
        ["b", 1],
        ["c", 2],
    ]);

    Test.expect(3, map.size);
    Test.expect(true, map.delete("b"));
    Test.expect(2, map.size);
    Test.expect(false, map.delete("b"));
    Test.expect(2, map.size);
}

// modification with active iterators
// deleted element is skipped
{
	const map = new Map([
		[1, 2],
		[3, 4],
		[5, 6],
	]);

	const iterator = map.entries();
	Test.expectEqual({ done: false, value: [1, 2] }, iterator.next());
	Test.expect(true, map.delete(3));
	Test.expectEqual({ done: false, value: [5, 6] }, iterator.next());
	Test.expectEqual({ done: true, value: undefined }, iterator.next());
}

// if rest of elements is deleted skip immediately to done
{
	const map = new Map([[-1, -1]]);

	for (let i = 1; i <= 25; ++i)
	    map.set(i, i);

	const iterator = map.entries();
	Test.expectEqual({ done: false, value: [-1, -1] }, iterator.next());
	for (let i = 1; i <= 25; ++i)
	    Test.expect(true, map.delete(i));

	Test.expectEqual({ done: true, value: undefined }, iterator.next());
	Test.expectEqual({ done: true, value: undefined }, iterator.next());
}

// deleting elements which were already visited has no effect
{
	const map = new Map([
		[1, 2],
		[3, 4],
		[5, 6],
	]);

	const iterator = map.entries();
	Test.expectEqual({ done: false, value: [1, 2] }, iterator.next());
	Test.expect(true, map.delete(1));
	Test.expectEqual({ done: false, value: [3, 4] }, iterator.next());
	Test.expect(true, map.delete(3));
	Test.expectEqual({ done: false, value: [5, 6] }, iterator.next());
	Test.expect(true, map.delete(5));
	Test.expect(false, map.delete(7));
	Test.expectEqual({ done: true, value: undefined }, iterator.next());
	Test.expectEqual({ done: true, value: undefined }, iterator.next());
}

// deleting the last element before the iterator visited it means you immediately get end
{
	const map = new Map([[1, 2]]);

	const iterator = map.entries();
	Test.expect(true, map.delete(1));
	Test.expectEqual({ done: true, value: undefined }, iterator.next());
	Test.expectEqual({ done: true, value: undefined }, iterator.next());
}