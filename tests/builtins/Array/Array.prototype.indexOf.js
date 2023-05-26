Test.expect(1, Array.prototype.indexOf.length);

const nums = [10, 20, 30, 40, 50];
Test.expect(0, nums.indexOf(10));
Test.expect(-1, nums.indexOf(60));
Test.expect(2, nums.indexOf(30, 2));
Test.expect(-1, nums.indexOf(30, 10));
Test.expect(0, nums.indexOf(10, -5));
Test.expect(1, nums.indexOf(20));
Test.expect(-1, [].indexOf(10));

const fruits = ["apple", "banana", "cherry"];
Test.expect(1, fruits.indexOf("banana"));
Test.expect(-1, fruits.indexOf("grape"));
Test.expect(-1, fruits.indexOf("banana", 2));
Test.expect(-1, fruits.indexOf("apple", 1));
Test.expect(2, fruits.indexOf("cherry"));
Test.expect(2, fruits.indexOf("cherry", -1));
Test.expect(-1, fruits.indexOf("kiwi"));
fruits.push("banana");
Test.expect(1, fruits.indexOf("banana"));
Test.expect(3, fruits.indexOf("banana", 2));

var array = ["hello", "world", 1, 2, false];

Test.expect(0, array.indexOf("hello"));
Test.expect(1, array.indexOf("world"));
Test.expect(4, array.indexOf(false));
Test.expect(4, array.indexOf(false, 2));
Test.expect(4, array.indexOf(false, -2));
Test.expect(2, array.indexOf(1));
Test.expect(-1, array.indexOf(1, 1000));
Test.expect(2, array.indexOf(1, -1000));
Test.expect(-1, array.indexOf("lebjs"));
Test.expect(4, array.indexOf(false, -1));
Test.expect(-1, array.indexOf(2, -1));
Test.expect(3, array.indexOf(2, -2));
Test.expect(-1, [].indexOf("lebjs"));
Test.expect(-1, [].indexOf("lebjs", 10));
Test.expect(-1, [].indexOf("lebjs", -10));
Test.expect(-1, [].indexOf());
Test.expect(0, [undefined].indexOf());