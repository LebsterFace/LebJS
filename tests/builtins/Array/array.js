let arr = [1,2,1+2,4];
expect(4, arr.length);

expect(1, arr[0])
expect(1, arr["0"])
expect(2, arr[1])
expect(2, arr["1"])
expect(3, arr[2])
expect(3, arr["2"])
expect(4, arr[3])
expect(4, arr["3"])
expect(false, arr.hasOwnProperty("4"))

expect(false, arr.hasOwnProperty("prop"))
expect(undefined, arr["prop"]);
expect(undefined, arr.prop);
arr.prop = 200;
expect(true, arr.hasOwnProperty("prop"))
expect(200, arr["prop"]);
expect(200, arr.prop);

arr.push(5);
expect(5, arr.length);
expect(5, arr[4])
expect(5, arr[arr.length - 1])
arr.push(6, 7, 8, 9);
expect(9, arr.length);
expect(6, arr["5"])
expect(9, arr[arr.length - 1])