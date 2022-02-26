let evens = [ 2, 4, 6, 8 ]
let odds = [ 1, 3, 5, 7 ]

const joinEight = (one, two, three, four, five, six, seven, eight) => {
    return "" + one + two + three + four + five + six + seven + eight;
}

const joinNine = (one, two, three, four, five, six, seven, eight, nine) => {
    return "" + one + two + three + four + five + six + seven + eight + nine;
}

expect("13572468", joinEight(...odds, ...evens))
expect("24681357", joinEight(...evens, ...odds))
expect("135752468", joinNine(...odds, 5, ...evens))
expect("513572468", joinNine(5, ...odds, ...evens))
