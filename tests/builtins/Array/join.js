const arr = [1, 2, 3, 4, 5, 6, 7, 8, 9];
expect("1,2,3,4,5,6,7,8,9", arr.join())
expect("1|2|3|4|5|6|7|8|9", arr.join("|"))
expect("", [].join())