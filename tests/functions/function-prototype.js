expect(false, xyz.hasOwnProperty(toString));
expect(Function.prototype.toString, xyz.toString);
expect(Function.prototype.toString, String.prototype.slice.toString);
expect(Function.prototype.toString, Function.prototype.toString.toString);
expect(Function.prototype.toString, Function.toString);
expect(Function.prototype.toString, Function.toString.toString);