Test.expect(1, String.prototype.at.length);

const string = "abc";
Test.expect("a", string.at(0));
Test.expect("b", string.at(1));
Test.expect("c", string.at(2));
Test.expect(undefined, string.at(3));
Test.expect(undefined, string.at(Infinity));
Test.expect("c", string.at(-1));
Test.expect("b", string.at(-2));
Test.expect("a", string.at(-3));
Test.expect(undefined, string.at(-4));
Test.expect(undefined, string.at(-Infinity));
let s = "😀";
Test.expect(2, s.length);
Test.expect("\ud83d", s.at(0));
Test.expect("\ude00", s.at(1));
Test.expect(undefined, s.at(2));
