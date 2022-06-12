const array = [ 1, 2 ]
const iterator = array[Symbol.iterator]()
Test.expect(1, iterator.next().value)
Test.expect(false, iterator.next().done)
Test.expect(true, iterator.next().done)
Test.expect(true, iterator.next().done)
