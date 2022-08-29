// Basic functionality
{
    let foo = (param = 'hello') => param

    Test.expect('hello', foo())
    Test.expect('hello', foo(undefined))
    Test.expect(1234, foo(1234))
}

// Evaluated at call-time
{
    function append(value, array = []) {
      array.push(value)
      return array
    }

    Test.equals([1], append(1))
    Test.equals([2], append(2))
}

// Earlier parameters are available to later default parameters
{
    function greet(name, greeting, message = `${greeting} ${name}`) {
      return [name, greeting, message]
    }

    Test.equals(["David", "Hi", "Hi David"], greet('David', 'Hi'))
    Test.equals(["David", "Hi", "Happy Birthday!"], greet('David', 'Hi', 'Happy Birthday!'))
}

// Scope Effects
{
    function f(a = go()) {
      function go() { return ':P' }
    }

    Test.expectError("ReferenceError", "go is not defined", f);
    Test.expectError("ReferenceError", "go is not defined", () => go());
    Test.expectError("ReferenceError", "a is not defined", () => a);
}

// Parameters without defaults after default parameters
{
    function f(x = 1, y) {
      return [x, y]
    }

    Test.equals([1, undefined], f())
    Test.equals([2, undefined], f(2))
}

// Destructured parameter with default value assignment
{
    function preFilledArray([x = 1, y = 2] = []) {
      return x + y;
    }

    Test.expect(3, preFilledArray());
    Test.expect(3, preFilledArray([]));
    Test.expect(4, preFilledArray([2]));
    Test.expect(5, preFilledArray([2, 3]));

    // Works the same for objects:
    function preFilledObject({z = 3} = {}) {
      return z;
    }

    Test.expect(3, preFilledObject());
    Test.expect(3, preFilledObject({}));
    Test.expect(2, preFilledObject({ z: 2 }));
}