class Example {
    constructor(name) {
        this.name = name;
        this.property = 123;
    }
}

const example = new Example("lorem");
Test.expect(true, example instanceof Example);

Test.expect(Example.prototype, Object.getPrototypeOf(example));
Test.expect(Example, Example.prototype.constructor);
Test.expect(Example, example.constructor);

Test.expect("lorem", example.name);
Test.expect(123, example.property);