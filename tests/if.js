let result = null;
if (10 * 2 === 20) {
    result = true;
} else {
    result = false;
}

expect(true, result);
if (true) expect(0,0);
else expect(false, true);

let a = 0;
if (a === 0) {
    expect(0, a);
} else if (a === 1) {
   expect(1, a);
} else {
    expect(2, a);
}

a = a + 1
if (a === 0) {
    expect(0, a);
} else if (a === 1) {
    expect(1, a);
} else {
    expect(2, a);
}

a = a + 1
 if (a === 0) {
     expect(0, a);
 } else if (a === 1) {
     expect(1, a);
 } else {
     expect(2, a);
 }