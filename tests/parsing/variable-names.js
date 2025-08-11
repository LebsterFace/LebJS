const errors = [];
const successes = "";

const NOT_ALLOWED = (X, expression_start, expects_after, loop) => {
	errors.push([`Unexpected token '${X}'`, `let ${X};`]);
	errors.push([`Unexpected token '${X}'`, `let { ${X} } = {};`]);

	errors.push([`Unexpected token '${X}'`, `try {} catch (${X}) {};`]);

	// TODO: Better errors like "Function parameter name cannot be a reserved word"?
	errors.push([`Unexpected token '${expression_start ? (expects_after ? ")" : "=>") : X}'`, `(${X}) => {};`]);

	if (loop) {
		errors.push([`Illegal \`${X}\` statement`, `${X} => {};`]);
		errors.push([`Unexpected token '${X}'`, `for (;;) { (${X}) => {}; };`]);
		errors.push([`Unexpected token '=>'`, `for (;;) { ${X} => {}; };`]);
	} else {
		errors.push([`Unexpected token '${expression_start ? "=>" : (expects_after ? "=>" : X)}'`, `${X} => {};`]);
	}

	errors.push(["Function name cannot be a reserved word", `function ${X}() {};`]);
	errors.push(["Function name cannot be a reserved word", `(function ${X}() {});`]);

	errors.push(["Class name cannot be a reserved word", `class ${X} { constructor() {} };`]);
	errors.push(["Class name cannot be a reserved word", `(class ${X} { constructor() {} });`]);

	successes += `let { ${X}: __${X}__ } = { ${X}: 123 };
Test.expect(123, __${X}__);
({ ${X}: __${X}__ } = { ${X}: 456 });
Test.expect(456, __${X}__);
`;
};

const ALLOWED = X => {
	Test.parse(`let ${X};`);
	Test.parse(`let { ${X} } = {};`);
	Test.parse(`try {} catch (${X}) {}`);
	Test.parse(`function ${X}() {};`);
	Test.parse(`class ${X} { constructor() {} };`);
	Test.parse(`(function ${X}() {});`);
	Test.parse(`(class ${X} { constructor() {} });`);
	Test.parse(`(${X}) => {};`);
	Test.parse(`${X} => {};`);
};

// TODO: "async"
// TODO: "await"
// TODO: "debugger"
// TODO: "export"
// TODO: "import"
// TODO: "super"
// TODO: "yield"

// Context-specific statements
NOT_ALLOWED("case", false, false);
NOT_ALLOWED("catch", false, false);
NOT_ALLOWED("default", false, false);
NOT_ALLOWED("else", false, false);
NOT_ALLOWED("enum", false, false);
NOT_ALLOWED("extends", false, false);
NOT_ALLOWED("finally", false, false);
NOT_ALLOWED("in", false, false);
NOT_ALLOWED("instanceof", false, false);
NOT_ALLOWED("static", false, false);
NOT_ALLOWED("break", false, true, true);
NOT_ALLOWED("continue", false, true, true);

// General statements
NOT_ALLOWED("const", false, true);
NOT_ALLOWED("do", false, true);
NOT_ALLOWED("for", false, true);
NOT_ALLOWED("if", false, true);
NOT_ALLOWED("let", false, true);
NOT_ALLOWED("return", false, true);
NOT_ALLOWED("switch", false, true);
NOT_ALLOWED("throw", false, true);
NOT_ALLOWED("try", false, true);
NOT_ALLOWED("var", false, true);
NOT_ALLOWED("while", false, true);

// Simple expressions
NOT_ALLOWED("false", true, false);
NOT_ALLOWED("null", true, false);
NOT_ALLOWED("this", true, false);
NOT_ALLOWED("true", true, false);

// Complex expressions
NOT_ALLOWED("class", true, true);
NOT_ALLOWED("delete", true, true);
NOT_ALLOWED("function", true, true);
NOT_ALLOWED("new", true, true);
NOT_ALLOWED("typeof", true, true);
NOT_ALLOWED("void", true, true);

// TODO: Potentially migrate away from these being global variables and make them literals?
NOT_ALLOWED("undefined", true, false);
NOT_ALLOWED("Infinity", true, false);
NOT_ALLOWED("NaN", true, false);

// Valid Identifiers
ALLOWED("identifier");
ALLOWED("of");
ALLOWED("set");
ALLOWED("get");

for (const [message, code] of errors) {
	Test.expectError("SyntaxError", message, () => Test.parse(code));
}

eval(successes);