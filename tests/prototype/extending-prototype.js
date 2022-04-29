let myString = "Hello world";
Test.expect(false, String.prototype.hasOwnProperty("firstCharacter"));
Test.expect(false, myString.hasProperty("firstCharacter"));

String.prototype.firstCharacter = function() {
	return this.charAt(0);
};

Test.expect(true, String.prototype.hasOwnProperty("firstCharacter"));
Test.expect(true, myString.hasProperty("firstCharacter"));
Test.expect(String.prototype.firstCharacter, myString.firstCharacter);
Test.expect("H", myString.firstCharacter());

String.prototype.append = function(value) {
	return this + value;
};

Test.expect("hello", "hell".append("o"));

Number.prototype.multiply = function(x) { return x * this };
Test.expect(15, (3).multiply(5));