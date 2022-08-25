let arr = [1,2,1+2,4];
Test.expect(4, arr.length);

Test.expect(1, arr[0])
Test.expect(1, arr["0"])
Test.expect(2, arr[1])
Test.expect(2, arr["1"])
Test.expect(3, arr[2])
Test.expect(3, arr["2"])
Test.expect(4, arr[3])
Test.expect(4, arr["3"])
Test.expect(false, arr.hasOwnProperty("4"))

Test.expect(false, arr.hasOwnProperty("prop"))
Test.expect(undefined, arr["prop"]);
Test.expect(undefined, arr.prop);
arr.prop = 200;
Test.expect(true, arr.hasOwnProperty("prop"))
Test.expect(200, arr["prop"]);
Test.expect(200, arr.prop);

arr.push(5);
Test.expect(5, arr.length);
Test.expect(5, arr[4])
Test.expect(5, arr[arr.length - 1])
arr.push(6, 7, 8, 9);
Test.expect(9, arr.length);
Test.expect(6, arr["5"])
Test.expect(9, arr[arr.length - 1])

// Prototype key
Array.prototype[1] = "proto"
arr = ["own"]
arr.length = 2
Test.expect("proto", arr[1])
Test.expect(false, arr.hasOwnProperty("1"))
Test.expect(true, '1' in Array.prototype)
delete Array.prototype[1]
Test.expect(undefined, arr[1])
Test.expect(undefined, Array.prototype[1])
Test.expect(false, '1' in arr)
Test.expect(false, arr.hasOwnProperty("1"))
Test.expect(false, '1' in Array.prototype)