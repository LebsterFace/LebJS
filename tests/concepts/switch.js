function get(argument) {
    let external = 30;

    switch (argument) {
        case 2: return "two"

        case 4:
            let variable = "four";
            return variable;

        case "seven": {
            external = 7;
            break;
        }

        case "six": return "AH!";
default:


external = 1000; case "sixty":
            external *= 2
            break;
    }

    return external;
}

Test.expect("two", get(1 + 1))
Test.expect("four", get(1 + 2 + 3 + 4 - 3 - 2 - 1))
Test.expect(7, get("seven"))
Test.expect("AH!", get("six"))
Test.expect(60, get("sixty"))
Test.expect(2000, get("fake"))
Test.expect(2000, get({ some: null }))
