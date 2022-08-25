const array = [1, 2, 3, 4, 5, 6];

const result = array.group((num, index, array) => {
  return num % 2 === 0 ? 'even': 'odd';
});

Test.equals(result.odd, [1, 3, 5])
Test.equals(result.even, [2, 4, 6])