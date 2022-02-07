const array = [ 1, 2 ]
const iterator = array[Symbol.iterator]()
expect(1, iterator.next().value)
expect(false, iterator.next().done)
expect(true, iterator.next().done)
expect(true, iterator.next().done)
