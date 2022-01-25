let myString = "Hello world";
expect(undefined, String.prototype.firstCharacter);
expect(undefined, myString.firstCharacter);

String.prototype.firstCharacter = function() {
	return this.charAt(0);
};

expect(String.prototype.firstCharacter, myString.firstCharacter);
expect("H", myString.firstCharacter());