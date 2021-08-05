function myClass() {
    this.property = 42;
}

let obj = new myClass();
expect(42, obj.property)