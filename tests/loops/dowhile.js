let number = 0;
do {
    number++;
} while (number < 9);
Test.expect(9, number);

number = 0;
do number++;
while (
number < 3
);
Test.expect(3, number);

Test.expectError("ReferenceError", "foo is not defined", () => {
    do
    {} while (foo);
});