let evens = [ 2, 4, 6, 8 ]
let odds = [ 1, 3, 5, 7 ]

function test(array, string) {
    let joined = "";
    for (const element of array) joined += element.toString();
    Test.expect(joined, string);
}

let oddsThenEvens = [ ...odds, ...evens ]
let evensThenOdds = [ ...evens, ...odds ]
let oddsFiveEvens = [ ...odds, 5, ...evens ]
let fiveOddsEvens = [ 5, ...odds, ...evens ]

test(oddsThenEvens, "13572468")
test(evensThenOdds, "24681357")
test(oddsFiveEvens, "135752468")
test(fiveOddsEvens, "513572468")
