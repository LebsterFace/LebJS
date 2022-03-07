const arr = [1, 2, 3, 4, 5, 6, 7, 8, 9];
expect(45, arr.reduce((a, b) => a + b));
expect(45, arr.reduce((a, b) => a + b, 0));
expect(0, arr.reduce((a, b) => a + b, -45));
expect("123456789", arr.reduce((a, b) => a + b, ""));

const append = (accumulator, currentValue, index, array) => {
    const zero = array[index] - currentValue;
    accumulator.push(zero + index);
    return accumulator;
};

expect('012345678', arr.reduce(append, []).reduce((a, b) => a + b, ""));