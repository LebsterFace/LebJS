let result = null;
if (10 * 2 === 20) {
    result = true;
} else {
    result = false;
}

Test.expect(true, result);
if (true) Test.expect(0,0);
else Test.fail();

let a = 0;
if (a === 0) {
    Test.expect(0, a);
} else if (a === 1) {
   Test.expect(1, a);
} else {
    Test.expect(2, a);
}

a = a + 1
if (a === 0) {
    Test.expect(0, a);
} else if (a === 1) {
    Test.expect(1, a);
} else {
    Test.expect(2, a);
}

a = a + 1
 if (a === 0) {
     Test.expect(0, a);
 } else if (a === 1) {
     Test.expect(1, a);
 } else {
     Test.expect(2, a);
 }