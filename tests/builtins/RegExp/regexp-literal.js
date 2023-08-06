// Valid JS regular expressions
Test.parse('/foo/');
Test.parse('/[]/');
Test.parse('/[^]/');
Test.parse('/<([a-z]+)(?:\\s+[a-z]+="[^"]*")*\\s*\\/?>/');
Test.parse('/^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$/');
Test.parse('/\\b(?:\\d{1,3}\\.){3}\\d{1,3}\\b/');
Test.parse('/^(?:(?:(0[1-9]|1[0-2])\\/(0[1-9]|1\\d|2\\d|3[01])\\/\\d{4})|(?:(0[1-9]|[12]\\d|3[01])-(0[1-9]|1[0-2])-\\d{4})|(\\d{4})\\/(0[1-9]|1[0-2])\\/(0[1-9]|1\\d|2\\d|3[01]))$/');
Test.parse('/^(?:\\+\\d{1,3})?\\s?\\(?\\d{3}\\)?[-.\\s]?\\d{3}[-.\\s]?\\d{4}$/');
Test.parse('/^(?:https?:\\/\\/)?(?:www\\.)?[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}(?:\\/\\S*)?$/');
Test.parse('/^#(?:[0-9a-fA-F]{3}){1,2}$/');
Test.parse('/^(?:\\d{4}-?){3}\\d{4}$/');

// Invalid JS regular expressions
Test.expectError("SyntaxError", "", () => Test.parse('/foo\nbar/'));
// TODO: Error should be "Unexpected token '>'"
Test.expectError("SyntaxError", "", () => Test.parse('/<([a-z]+)(?:\\s+[a-z]+="[^"]*")*\\s*/?>/'));
// FIXME: Test.expectError("SyntaxError", "Invalid regexp group", () => Test.parse('/^(\\((?>[^()]+|\\((?<depth>)|\\)(?<-depth>))*(?(depth)(?!))\\))$/'));
// FIXME: Test.expectError("SyntaxError", "Unterminated regexp group", () => Test.parse('/^(?:(?:(0[1-9]|1[0-2])/(0[1-9]|1\\d|2\\d|3[01])/\\d{4})|(?:(0[1-9]|[12]\\d|3[01])-(0[1-9]|1[0-2])-\\d{4})|(\\d{4})/(0[1-9]|1[0-2])/(0[1-9]|1\\d|2\\d|3[01]))$/'));
// FIXME: Test.expectError("SyntaxError", "Unterminated regexp group", () => Test.parse('/^(?:https?://)?(?:www\\.)?[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}(?:/\\S*)?$/'));

// Valid flags
Test.parse("/foo/d");
Test.parse("/foo/g");
Test.parse("/foo/i");
Test.parse("/foo/m");
Test.parse("/foo/s");
Test.parse("/foo/u");
Test.parse("/foo/v");
Test.parse("/foo/y");

// Duplicate flags
Test.expectError("SyntaxError", "Duplicate flag 'd' in regular expression literal", () => Test.parse(Test.parse("/foo/dd")));
Test.expectError("SyntaxError", "Duplicate flag 'g' in regular expression literal", () => Test.parse(Test.parse("/foo/gg")));
Test.expectError("SyntaxError", "Duplicate flag 'i' in regular expression literal", () => Test.parse(Test.parse("/foo/ii")));
Test.expectError("SyntaxError", "Duplicate flag 'm' in regular expression literal", () => Test.parse(Test.parse("/foo/mm")));
Test.expectError("SyntaxError", "Duplicate flag 's' in regular expression literal", () => Test.parse(Test.parse("/foo/ss")));
Test.expectError("SyntaxError", "Duplicate flag 'u' in regular expression literal", () => Test.parse(Test.parse("/foo/uu")));
Test.expectError("SyntaxError", "Duplicate flag 'v' in regular expression literal", () => Test.parse(Test.parse("/foo/vv")));
Test.expectError("SyntaxError", "Duplicate flag 'y' in regular expression literal", () => Test.parse(Test.parse("/foo/yy")));

// Invalid flags
Test.expectError("SyntaxError", "Invalid regular expression flag 'a'", () => Test.parse(Test.parse("/foo/ga")));
Test.expectError("SyntaxError", "Invalid regular expression flag 'D'", () => Test.parse("/foo/D"));
Test.expectError("SyntaxError", "Invalid regular expression flag 'G'", () => Test.parse("/foo/G"));
Test.expectError("SyntaxError", "Invalid regular expression flag 'I'", () => Test.parse("/foo/I"));
Test.expectError("SyntaxError", "Invalid regular expression flag 'M'", () => Test.parse("/foo/M"));
Test.expectError("SyntaxError", "Invalid regular expression flag 'S'", () => Test.parse("/foo/S"));
Test.expectError("SyntaxError", "Invalid regular expression flag 'U'", () => Test.parse("/foo/U"));
Test.expectError("SyntaxError", "Invalid regular expression flag 'V'", () => Test.parse("/foo/V"));
Test.expectError("SyntaxError", "Invalid regular expression flag 'Y'", () => Test.parse("/foo/Y"));