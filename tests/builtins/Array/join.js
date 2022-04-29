const arr = [1, 2, 3, 4, 5, 6, 7, 8, 9];
Test.expect("1,2,3,4,5,6,7,8,9", arr.join())
Test.expect("1|2|3|4|5|6|7|8|9", arr.join("|"))
Test.expect("", [].join())