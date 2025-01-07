const rangeToArray = function(a, b, c = 1) {
	const it = Number.range(a, b, c);
	const result = [];
	while (true) {
		const next = it.next();
		if (next.done) break;
		result.push(next.value)
	}
	return result;
}

Test.expectEqual([0], rangeToArray(0, 1))
Test.expectEqual([0, 1, 2, 3, 4, 5, 6], rangeToArray(0, 7))
Test.expectEqual([0, 2, 4, 6], rangeToArray(0, 7, 2))
Test.expectEqual([0, -1, -2, -3, -4, -5], rangeToArray(0, -6, -1))
Test.expectEqual([], rangeToArray(0, -6))
Test.expectEqual([], rangeToArray(1, 10, -1))
