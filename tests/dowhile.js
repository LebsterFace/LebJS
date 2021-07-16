let number = 0;
do {
    number++;
} while (number < 9);
expect(9, number);

number = 0;
do number++;
while (number < 3);
expect(3, number);

try {
    do {} while (foo);
} catch (e) {
    expect("ReferenceError", e.name);
    expect("foo is not defined", e.message);
}

do {} while (false) expect(true, true)