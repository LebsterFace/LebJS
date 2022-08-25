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

Test.equals([0], rangeToArray(0, 1))
Test.equals([0, 1, 2, 3, 4, 5, 6], rangeToArray(0, 7))
Test.equals([0, 2, 4, 6], rangeToArray(0, 7, 2))
Test.equals([0, -1, -2, -3, -4, -5], rangeToArray(0, -6, -1))
Test.equals([], rangeToArray(0, -6))
Test.equals([], rangeToArray(1, 10, -1))
