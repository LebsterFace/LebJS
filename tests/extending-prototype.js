let myString = "Hello world";
expect(false, String.prototype.hasOwnProperty("firstCharacter"));
expect(false, myString.hasProperty("firstCharacter"));

String.prototype.firstCharacter = function() {
	return this.charAt(0);
};

expect(true, String.prototype.hasOwnProperty("firstCharacter"));
expect(true, myString.hasProperty("firstCharacter"));
expect(String.prototype.firstCharacter, myString.firstCharacter);
expect("H", myString.firstCharacter());