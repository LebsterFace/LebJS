class Animal {
    constructor(name) {
        this.name = name;
    }

    greet() {
        return "My name is " + this.name;
    }
}

class Cat extends Animal {
    constructor(name, age) {
        super(name);
        this.age = age;
    }

    birthYear() {
        return 2022 - this.age;
    }
}

let myCat = new Cat("Name", 100);
Test.expect(true, myCat instanceof Cat);
Test.expect(true, myCat instanceof Animal);

Test.expect(Cat.prototype, Object.getPrototypeOf(myCat));
Test.expect("My name is Name", myCat.greet());
Test.expect(1922, myCat.birthYear());
