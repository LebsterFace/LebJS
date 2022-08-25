const arr = [1, 2, 3, 4, 5, 6, 7, 8, 9];
Test.expect(45, arr.reduce((a, b) => a + b));
Test.expect(45, arr.reduce((a, b) => a + b, 0));
Test.expect(0, arr.reduce((a, b) => a + b, -45));
Test.expect("123456789", arr.reduce((a, b) => a + b, ""));

const append = (accumulator, currentValue, index, array) => {
    const zero = array[index] - currentValue;
    accumulator.push(zero + index);
    return accumulator;
};

Test.expect('012345678', arr.reduce(append, []).reduce((a, b) => a + b, ""));