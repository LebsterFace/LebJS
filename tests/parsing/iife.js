Test.expectError("SyntaxError", "Unexpected token '('", () => Test.parse("let a = () => { return 1 }();"))
let b = (() => { return 1 })();
Test.expect(1, b);
let c = function() { return 2 }()
Test.expect(2, c);
let d = (function() { return 3 })()
Test.expect(3, d);