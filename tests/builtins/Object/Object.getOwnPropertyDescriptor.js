Test.equals({ value: 1, writable: true, enumerable: true, configurable: true }, Object.getOwnPropertyDescriptor({ a: 1 }, 'a'));
Test.equals({ value: 'l', writable: false, enumerable: true, configurable: false }, Object.getOwnPropertyDescriptor('hello', '3'));
Test.equals({ value: 5, writable: false, enumerable: false, configurable: false }, Object.getOwnPropertyDescriptor('hello', 'length'));
// FIXME: Test.equals({ value: 2, writable: true, enumerable: false, configurable: false }, Object.getOwnPropertyDescriptor([1, 2], 'length'));
Test.equals({ value: 1, writable: true, enumerable: true, configurable: true }, Object.getOwnPropertyDescriptor([1, 2], '0'));