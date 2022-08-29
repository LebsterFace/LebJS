const array = ["a", "b", "c"];

Test.expect("a", array.at(0));
Test.expect("b", array.at(1));
Test.expect("c", array.at(2));
Test.expect(undefined, array.at(3));
Test.expect(undefined, array.at(Infinity));
Test.expect("c", array.at(-1));
Test.expect("b", array.at(-2));
Test.expect("a", array.at(-3));
Test.expect(undefined, array.at(-4));
Test.expect(undefined, array.at(-Infinity));