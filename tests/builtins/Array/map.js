const arr = [1, 2, 3, 4, 5, 6, 7, 8, 9];
Test.expect("2,4,6,8,10,12,14,16,18", arr.map(n => n * 2).toString())
Test.expect("2,4,6,8", arr.filter(n => n % 2 === 0).toString())