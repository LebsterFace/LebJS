let trees = ['redwood', 'bay', 'cedar', 'oak', 'maple']
Test.expect(true, 0 in trees);
Test.expect(true, 3 in trees);
Test.expect(false, 6 in trees);
Test.expect(false, 'bay' in trees);
Test.expect(true, 'length' in trees);

Test.expect(true, 'PI' in Math);

let mycar = {make: 'Honda', model: 'Accord', year: 1998}
Test.expect(true, 'make' in mycar);
Test.expect(true, 'model' in mycar);

Test.expect(true, 'toString' in {});

mycar = {make: 'Honda', model: 'Accord', year: 1998}
mycar.make = undefined
Test.expect(true, 'make' in mycar);

Test.expect(true, 'toString' in {});
