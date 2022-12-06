class Example {
    constructor(name) {
        this.name = name;
        this.property = 123;
    }

    multiply(x) {
        return this.property * x;
    }

    ['a' + 'dd'](x) {
        return this.property + x;
    }
}

const example = new Example("lorem");
Test.expect(true, example instanceof Example);

Test.expect(Example.prototype, Object.getPrototypeOf(example));
Test.expect(Example, Example.prototype.constructor);
Test.expect(Example, example.constructor);

Test.expect("lorem", example.name);
Test.expect(123, example.property);

Test.expect(1, example.multiply(1 / 123));
Test.expect(124, example.add(1));