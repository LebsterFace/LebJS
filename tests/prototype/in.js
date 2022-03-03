let trees = ['redwood', 'bay', 'cedar', 'oak', 'maple']
expect(true, 0 in trees);
expect(true, 3 in trees);
expect(false, 6 in trees);
expect(false, 'bay' in trees);
expect(true, 'length' in trees);

expect(true, 'PI' in Math);

let mycar = {make: 'Honda', model: 'Accord', year: 1998}
expect(true, 'make' in mycar);
expect(true, 'model' in mycar);

expect(true, 'toString' in {});

let mycar = {make: 'Honda', model: 'Accord', year: 1998}
mycar.make = undefined
expect(true, 'make' in mycar);

expect(true, 'toString' in {});
