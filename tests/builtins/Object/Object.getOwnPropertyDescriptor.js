Test.expectEqual({ value: 1, writable: true, enumerable: true, configurable: true }, Object.getOwnPropertyDescriptor({ a: 1 }, 'a'));
Test.expectEqual({ value: 'l', writable: false, enumerable: true, configurable: false }, Object.getOwnPropertyDescriptor('hello', '3'));
Test.expectEqual({ value: 5, writable: false, enumerable: false, configurable: false }, Object.getOwnPropertyDescriptor('hello', 'length'));
// FIXME: Test.expectEqual({ value: 2, writable: true, enumerable: false, configurable: false }, Object.getOwnPropertyDescriptor([1, 2], 'length'));
Test.expectEqual({ value: 1, writable: true, enumerable: true, configurable: true }, Object.getOwnPropertyDescriptor([1, 2], '0'));