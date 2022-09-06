// TODO: More comprehensive test
let x = { a: 1 }

let { value, writable, enumerable, configurable } = Object.getOwnPropertyDescriptor(x, 'a');
Test.equals(1, value);
Test.equals(true, writable);
Test.equals(true, enumerable);
Test.equals(true, configurable);