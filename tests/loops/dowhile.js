let number = 0;
do {
    number++;
} while (number < 9);
Test.expect(9, number);

number = 0;
do number++;
while (number < 3);
Test.expect(3, number);

try {
    do {} while (foo);
} catch (e) {
    Test.expect("ReferenceError", e.name);
    Test.expect("foo is not defined", e.message);
}