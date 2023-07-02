const array = [1, 2, 3, 4, 5];
const odd = { odd: true };
const even = { even: true };
const result = Map.groupBy(array, (num, index) => num % 2 === 0 ? even: odd);
Test.equals(result.get(odd), [1, 3, 5]);
Test.equals(result.get(even), [2, 4]);
Test.expect(2, result.size);