const arr = [3, 4, 5];

// basic
Test.expect(3, arr["0"]);
Test.expect(4, arr["1"]);
Test.expect(5, arr["2"]);

// above length
Test.expect(undefined, arr["3"]);

// strings with extra characters
// whitespace
Test.expect(undefined, arr[" 0"]);
Test.expect(undefined, arr["0 "]);
Test.expect(undefined, arr["  0"]);
Test.expect(undefined, arr["  0  "]);
Test.expect(undefined, arr["  3  "]);

// leading 0
Test.expect(undefined, arr["00"]);
Test.expect(undefined, arr["01"]);
Test.expect(undefined, arr["02"]);
Test.expect(undefined, arr["03"]);

// leading +/-
Test.expect(undefined, arr['+0']);
Test.expect(undefined, arr['+1']);
Test.expect(undefined, arr['+-0']);
Test.expect(undefined, arr['++0']);
Test.expect(undefined, arr['-0']);
Test.expect(undefined, arr['-1']);
Test.expect(undefined, arr['--0']);
Test.expect(undefined, arr['-+0']);

// combined
Test.expect(undefined, arr["+00"]);
Test.expect(undefined, arr[" +0"]);
Test.expect(undefined, arr["  +0 "]);
Test.expect(undefined, arr["  00 "]);