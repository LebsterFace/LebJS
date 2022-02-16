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

arr.prop = 200;
expect(200, arr.prop)

arr.push(5);
expect(5, arr.length);
expect(5, arr[4])
expect(5, arr[arr.length - 1])

expect("1,2,3,4,5", arr.join())
expect("1|2|3|4|5", arr.join("|"))

expect("2,4,6,8,10", arr.map(n => n * 2).toString())
expect("2,4", arr.filter(n => n % 2 === 0).toString())