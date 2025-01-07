// length is 1
Test.expect(1, Array.prototype.reduceRight.length);

// callback must be a function
Test.expectError("TypeError", "undefined is not a function", () => [].reduceRight(undefined));

// reduce of empty array with no initial value
Test.expectError("TypeError", "Reduce of empty array with no initial value", () => [].reduceRight((a, x) => x));

// reduce of array with only empty slots and no initial value
Test.expectError("TypeError", "Reduce of empty array with no initial value", () => [, , ].reduceRight((a, x) => x));

// basic functionality
// FIXME: [1, 2].reduceRight(function () { Test.expect(globalThis, this); });

[1, 2].reduceRight(function() {
    "use strict";
    Test.expect(undefined, this);
});

const callbackCalled = 0;
const callback = () => {
    callbackCalled++;
    return true;
};

Test.expect(1, [1].reduceRight(callback));
Test.expect(0, callbackCalled);

Test.expect(1, [1].reduceRight(callback));
Test.expect(0, callbackCalled);

callbackCalled = 0;
Test.expect(true, [1, 2, 3].reduceRight(callback));
Test.expect(2, callbackCalled);

callbackCalled = 0;
Test.expect(true, [1, 2, 3, , ].reduceRight(callback));
Test.expect(2, callbackCalled);

callbackCalled = 0;
Test.expect(true, [, , , 1, , , 10, , 100, , , ].reduceRight(callback));
Test.expect(2, callbackCalled);

const constantlySad = () => ":^(";
const result = [].reduceRight(constantlySad, ":^)");
Test.expect(":^)", result);

result = [":^0"].reduceRight(constantlySad, ":^)");
Test.expect(":^(", result);

result = [":^0"].reduceRight(constantlySad);
Test.expect(":^0", result);

result = [5, 4, 3, 2, 1].reduceRight((accum, elem) => "" + accum + elem);
Test.expect("12345", result);

result = [1, 2, 3, 4, 5, 6].reduceRight((accum, elem) => {
    return "" + accum + elem;
}, 100);
Test.expect("100654321", result);

result = [6, 5, 4, 3, 2, 1].reduceRight((accum, elem) => "" + accum + elem, 100);
Test.expect("100123456", result);

const indices = [];
result = ["foo", 1, true].reduceRight((a, v, i) => {
    indices.push(i);
});
Test.expect(undefined, result);
Test.expect(2, indices.length);
Test.expect(1, indices[0]);
Test.expect(0, indices[1]);

indices = [];
result = ["foo", 1, true].reduceRight((a, v, i) => {
    indices.push(i);
}, "foo");
Test.expect(undefined, result);
Test.expectEqual([2, 1, 0], indices);

const mutable = {
    prop: 0
};
result = ["foo", 1, true].reduceRight((a, v) => {
    a.prop = v;
    return a;
}, mutable);
Test.expect(mutable, result);
Test.expect("foo", result.prop);

const a1 = [1, 2];
const a2 = null;
a1.reduceRight((a, v, i, t) => {
    a2 = t;
});
Test.expect(a2, a1);