package xyz.lebster.core.parser;

import xyz.lebster.core.SpecificationURL;
import xyz.lebster.core.exception.CannotParse;
import xyz.lebster.core.exception.NotImplemented;
import xyz.lebster.core.exception.SyntaxError;
import xyz.lebster.core.node.Program;
import xyz.lebster.core.node.declaration.Declaration;
import xyz.lebster.core.node.declaration.FunctionDeclaration;
import xyz.lebster.core.node.declaration.VariableDeclaration;
import xyz.lebster.core.node.declaration.VariableDeclarator;
import xyz.lebster.core.node.expression.*;
import xyz.lebster.core.node.statement.*;
import xyz.lebster.core.node.value.*;

import java.util.*;

import static xyz.lebster.core.parser.Associativity.*;

public final class Parser {
	private static final HashMap<TokenType, Integer> precedence = new HashMap<>();
	private static final HashMap<TokenType, Associativity> associativity = new HashMap<>();

	static {
		// https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Operators/Operator_Precedence#table
		precedence.put(TokenType.LParen, 21);
		precedence.put(TokenType.RParen, 21);

		precedence.put(TokenType.LBracket, 20);
		precedence.put(TokenType.RBracket, 20);
		precedence.put(TokenType.Period, 20);

		precedence.put(TokenType.New, 20);

		// Unary Expressions:
		precedence.put(TokenType.PlusPlus, 18);
		precedence.put(TokenType.MinusMinus, 18);

		precedence.put(TokenType.Bang, 17);
		precedence.put(TokenType.Typeof, 17);

		precedence.put(TokenType.Exponent, 16);

		precedence.put(TokenType.Star, 15);
		precedence.put(TokenType.Slash, 15);
		precedence.put(TokenType.Percent, 15);

		precedence.put(TokenType.Plus, 14);
		precedence.put(TokenType.Minus, 14);

		precedence.put(TokenType.LessThan, 12);
		precedence.put(TokenType.LessThanEqual, 12);
		precedence.put(TokenType.GreaterThan, 12);
		precedence.put(TokenType.GreaterThanEqual, 12);
		precedence.put(TokenType.In, 12);
		precedence.put(TokenType.Instanceof, 12);

		precedence.put(TokenType.StrictEqual, 11);
		precedence.put(TokenType.StrictNotEqual, 11);

		precedence.put(TokenType.LogicalAnd, 7);
		precedence.put(TokenType.LogicalOr, 6);
		precedence.put(TokenType.NullishCoalescing, 5);

		precedence.put(TokenType.UnsignedRightShiftEquals, 3);
		precedence.put(TokenType.LeftShiftEquals, 3);
		precedence.put(TokenType.MinusEquals, 3);
		precedence.put(TokenType.MultiplyEquals, 3);
		precedence.put(TokenType.DivideEquals, 3);
		precedence.put(TokenType.AmpersandEquals, 3);
		precedence.put(TokenType.PercentEquals, 3);
		precedence.put(TokenType.CaretEquals, 3);
		precedence.put(TokenType.PlusEquals, 3);
		precedence.put(TokenType.PipeEquals, 3);
		precedence.put(TokenType.NullishCoalescingEquals, 3);
		precedence.put(TokenType.ExponentEquals, 3);
		precedence.put(TokenType.LogicalAndEquals, 3);
		precedence.put(TokenType.RightShiftEquals, 3);
		precedence.put(TokenType.LogicalOrEquals, 3);
		precedence.put(TokenType.Equals, 3);

		// FIXME: Switch statement method (when all operators are implemented)
		associativity.put(TokenType.LParen, NA);
		associativity.put(TokenType.RParen, NA);
		associativity.put(TokenType.New, NA);

		associativity.put(TokenType.LBracket, Left);
		associativity.put(TokenType.RBracket, Left);
		associativity.put(TokenType.Star, Left);
		associativity.put(TokenType.Slash, Left);
		associativity.put(TokenType.Plus, Left);
		associativity.put(TokenType.Minus, Left);
		associativity.put(TokenType.Period, Left);
		associativity.put(TokenType.StrictEqual, Left);
		associativity.put(TokenType.StrictNotEqual, Left);
		associativity.put(TokenType.LessThan, Left);
		associativity.put(TokenType.LessThanEqual, Left);
		associativity.put(TokenType.GreaterThan, Left);
		associativity.put(TokenType.GreaterThanEqual, Left);
		associativity.put(TokenType.In, Left);
		associativity.put(TokenType.Instanceof, Left);
		associativity.put(TokenType.LogicalOr, Left);
		associativity.put(TokenType.LogicalAnd, Left);
		associativity.put(TokenType.NullishCoalescing, Left);

		associativity.put(TokenType.Exponent, Right);

		associativity.put(TokenType.Bang, Right);
		associativity.put(TokenType.Typeof, Right);
		associativity.put(TokenType.PlusPlus, Right);
		associativity.put(TokenType.MinusMinus, Right);

		associativity.put(TokenType.UnsignedRightShiftEquals, Right);
		associativity.put(TokenType.LeftShiftEquals, Right);
		associativity.put(TokenType.MinusEquals, Right);
		associativity.put(TokenType.MultiplyEquals, Right);
		associativity.put(TokenType.DivideEquals, Right);
		associativity.put(TokenType.AmpersandEquals, Right);
		associativity.put(TokenType.PercentEquals, Right);
		associativity.put(TokenType.CaretEquals, Right);
		associativity.put(TokenType.PlusEquals, Right);
		associativity.put(TokenType.PipeEquals, Right);
		associativity.put(TokenType.NullishCoalescingEquals, Right);
		associativity.put(TokenType.ExponentEquals, Right);
		associativity.put(TokenType.LogicalAndEquals, Right);
		associativity.put(TokenType.RightShiftEquals, Right);
		associativity.put(TokenType.LogicalOrEquals, Right);
		associativity.put(TokenType.Equals, Right);
	}

	private ParserState state;
	private ParserState saved = null;

	public Parser(Token[] tokens) {
		this.state = new ParserState(tokens);
	}

	private void save() {
		this.saved = state.clone();
	}

	private void load() {
		if (this.saved == null) throw new IllegalStateException("Attempting to load invalid ParseState");
		this.state = saved;
		this.saved = null;
	}

	public Program parse() throws SyntaxError, CannotParse {
		final Program program = new Program();
		state.consume();

		while (state.index < state.tokens.length) {
			if (state.currentToken.type == TokenType.EOF) {
				break;
			} else {
				program.append(parseLine());
			}
		}

		return program;
	}

	private Statement parseLine() throws SyntaxError, CannotParse {
		state.consumeAll(TokenType.Terminator);
		final Statement result = parseAny();
		state.consumeAll(TokenType.Semicolon);
		state.consumeAll(TokenType.Terminator);
		return result;
	}

	private Statement parseAny() throws SyntaxError, CannotParse {
		if (matchDeclaration()) {
			return parseDeclaration();
		} else if (matchStatementOrExpression()) {
			return parseStatementOrExpression();
		} else {
			throw new CannotParse("Token '" + state.currentToken.type + "'");
		}
	}

	private BlockStatement parseBlockStatement() throws SyntaxError, CannotParse {
		state.require(TokenType.LBrace);
		final BlockStatement result = new BlockStatement();

		while (state.index < state.tokens.length && state.currentToken.type != TokenType.RBrace) {
			if (state.currentToken.type == TokenType.EOF) {
				break;
			} else {
				result.append(parseLine());
			}
		}

		state.require(TokenType.RBrace);
		return result;
	}

	private Statement parseStatementOrExpression() throws SyntaxError, CannotParse {
		return switch (state.currentToken.type) {
			case Function -> parseFunctionDeclaration();
			case Semicolon -> new EmptyStatement();
			case LBrace -> parseBlockStatement();
			case While -> parseWhileStatement();
			case Do -> parseDoWhileStatement();
			case For -> parseForStatement();
			case If -> parseIfStatement();
			case Try -> parseTryStatement();

			case Return -> {
				state.consume();
//					FIXME: Proper automatic semicolon insertion
				final Expression val = matchPrimaryExpression() ? parseExpression() : Undefined.instance;
				yield new ReturnStatement(val);
			}

			case Break -> {
				state.consume();
				yield new BreakStatement();
			}

			case Throw -> {
				state.consume();
				yield new ThrowStatement(parseExpression());
			}

			default -> {
				if (matchPrimaryExpression()) {
					yield new ExpressionStatement(parseExpression());
				} else {
					throw new CannotParse(state.currentToken, "Statement");
				}
			}
		};

	}

	private ForStatement parseForStatement() throws SyntaxError, CannotParse {
		state.require(TokenType.For);
		state.require(TokenType.LParen);

		Statement init = null;
		if (matchPrimaryExpression() || matchDeclaration()) init = parseAny();
		state.require(TokenType.Semicolon);

		final Expression test = matchPrimaryExpression() ? parseExpression() : null;
		state.require(TokenType.Semicolon);

		final Expression update = matchPrimaryExpression() ? parseExpression() : null;
		state.require(TokenType.RParen);

		final Statement body = parseLine();
		return new ForStatement(init, test, update, body);
	}

	private WhileStatement parseWhileStatement() throws SyntaxError, CannotParse {
		state.require(TokenType.While);
		state.require(TokenType.LParen);
		final Expression condition = parseExpression();
		state.require(TokenType.RParen);
		final Statement body = parseLine();
		return new WhileStatement(condition, body);
	}

	private DoWhileStatement parseDoWhileStatement() throws SyntaxError, CannotParse {
		state.require(TokenType.Do);
		final Statement body = parseLine();
		state.require(TokenType.While);
		state.require(TokenType.LParen);
		final Expression condition = parseExpression();
		state.require(TokenType.RParen);
		return new DoWhileStatement(body, condition);
	}

	private TryStatement parseTryStatement() throws SyntaxError, CannotParse {
		state.require(TokenType.Try);
		final BlockStatement body = parseBlockStatement();
		state.require(TokenType.Catch);
		state.require(TokenType.LParen);
		final Identifier parameter = new Identifier(state.require(TokenType.Identifier).value);
		state.require(TokenType.RParen);
		final BlockStatement catchBody = parseBlockStatement();
		return new TryStatement(body, new CatchClause(parameter, catchBody));
	}

	private IfStatement parseIfStatement() throws SyntaxError, CannotParse {
		state.require(TokenType.If);
		state.require(TokenType.LParen);
		final Expression condition = parseExpression();
		state.require(TokenType.RParen);
		final Statement consequence = parseLine();
		final Statement elseStatement = state.currentToken.type == TokenType.Else ? parseElseStatement() : null;
		return new IfStatement(condition, consequence, elseStatement);
	}

	public Statement parseElseStatement() throws SyntaxError, CannotParse {
		state.require(TokenType.Else);
		return parseLine();
	}

	private Declaration parseDeclaration() throws SyntaxError, CannotParse {
		return switch (state.currentToken.type) {
			case Let, Var, Const -> parseVariableDeclaration();
			case Function -> parseFunctionDeclaration();
			default -> throw new CannotParse(state.currentToken, "Declaration");
		};
	}

	private VariableDeclaration parseVariableDeclaration() throws SyntaxError, CannotParse {
		state.consume();
		final Token identifier = state.require(TokenType.Identifier);
		state.require(TokenType.Equals);
		final Expression value = parseExpression();
		return new VariableDeclaration(new VariableDeclarator(new Identifier(identifier.value), value));
	}

	private List<Identifier> parseFunctionArguments() throws SyntaxError {
		state.require(TokenType.LParen);
		final List<Identifier> arguments = new ArrayList<>();

		while (state.currentToken.type == TokenType.Identifier) {
			arguments.add(new Identifier(state.consume().value));
			if (state.accept(TokenType.Comma) == null) break;
		}

		state.require(TokenType.RParen);
		return arguments;
	}

	private FunctionDeclaration parseFunctionDeclaration() throws SyntaxError, CannotParse {
		state.require(TokenType.Function);
		final Identifier name = new Identifier(state.require(TokenType.Identifier).value);
		final List<Identifier> arguments = parseFunctionArguments();
		return new FunctionDeclaration(parseBlockStatement(), name, arguments.toArray(new Identifier[0]));
	}

	private FunctionExpression parseFunctionExpression() throws SyntaxError, CannotParse {
		state.require(TokenType.Function);
		final Token potentialName = state.accept(TokenType.Identifier);
		final Identifier name = potentialName == null ? null : new Identifier(potentialName.value);
		final List<Identifier> arguments = parseFunctionArguments();
		return new FunctionExpression(parseBlockStatement(), name, arguments.toArray(new Identifier[0]));
	}

	private List<Expression> parseExpressionList(boolean expectParens) throws SyntaxError, CannotParse {
		final List<Expression> result = new ArrayList<>();
		if (expectParens) state.require(TokenType.LParen);

		while (matchPrimaryExpression()) {
			result.add(parseExpression());
			if (state.accept(TokenType.Comma) == null) break;
		}

		if (expectParens) state.require(TokenType.RParen);
		return result;
	}

	private CallExpression parseCallExpression(Expression left) throws SyntaxError, CannotParse {
		final List<Expression> arguments = parseExpressionList(false);
		state.require(TokenType.RParen);
		return new CallExpression(left, arguments.toArray(new Expression[0]));
	}

	@SpecificationURL("https://tc39.es/ecma262/multipage#prod-UnaryExpression")
	private Expression parseUnaryPrefixedExpression() throws SyntaxError, CannotParse {
		final Token token = state.consume();
//		TODO: Remove when all implemented
		if (!associativity.containsKey(token.type))
			throw new NotImplemented("Associativity for token '" + token.type + "'");
		final Associativity assoc = associativity.get(token.type);
//		TODO: Remove when all implemented
		if (!precedence.containsKey(token.type)) throw new NotImplemented("Precedence for token '" + token.type + "'");
		final int minPrecedence = precedence.get(token.type);

		final UnaryExpression.UnaryOp op = switch (token.type) {
			case Minus -> UnaryExpression.UnaryOp.Negate;
			case Bang -> UnaryExpression.UnaryOp.LogicalNot;
			case Typeof -> UnaryExpression.UnaryOp.Typeof;
			case PlusPlus -> UnaryExpression.UnaryOp.PreIncrement;
			case MinusMinus -> UnaryExpression.UnaryOp.PreDecrement;
			default -> throw new CannotParse(token, "Unary Operator");
		};

		return new UnaryExpression(parseExpression(minPrecedence, assoc), op);
	}

	private Expression parseExpression() throws SyntaxError, CannotParse {
		return parseExpression(0, Left, new HashSet<>());
	}

	private Expression parseExpression(int minPrecedence, Associativity assoc) throws SyntaxError, CannotParse {
		return parseExpression(minPrecedence, assoc, new HashSet<>());
	}

	private Expression parseExpression(int minPrecedence, Associativity assoc, Set<TokenType> forbidden) throws SyntaxError, CannotParse {
		Expression latestExpr = parsePrimaryExpression();

		while (matchSecondaryExpression(forbidden)) {
//			TODO: Remove when all implemented
			if (!precedence.containsKey(state.currentToken.type))
				throw new NotImplemented("Precedence for token '" + state.currentToken.type + "'");
			final int newPrecedence = precedence.get(state.currentToken.type);

			if (newPrecedence < minPrecedence || newPrecedence == minPrecedence && assoc == Left) {
				break;
			}

//			TODO: Remove when all implemented
			if (!associativity.containsKey(state.currentToken.type))
				throw new NotImplemented("Associativity for token '" + state.currentToken.type + "'");
			final Associativity newAssoc = associativity.get(state.currentToken.type);
			latestExpr = parseSecondaryExpression(latestExpr, newPrecedence, newAssoc);
		}

		return latestExpr;
	}

	private Expression parseSecondaryExpression(Expression left, int minPrecedence, Associativity assoc) throws SyntaxError, CannotParse {
		state.consumeAll(TokenType.Terminator);
		final Token token = state.consume();
		state.consumeAll(TokenType.Terminator);

		return switch (token.type) {
			case Plus -> new BinaryExpression(left, parseExpression(minPrecedence, assoc), BinaryExpression.BinaryOp.Add);
			case Minus -> new BinaryExpression(left, parseExpression(minPrecedence, assoc), BinaryExpression.BinaryOp.Subtract);
			case Star -> new BinaryExpression(left, parseExpression(minPrecedence, assoc), BinaryExpression.BinaryOp.Multiply);
			case Slash -> new BinaryExpression(left, parseExpression(minPrecedence, assoc), BinaryExpression.BinaryOp.Divide);

			case Exponent -> new BinaryExpression(left, parseExpression(minPrecedence, assoc), BinaryExpression.BinaryOp.Exponent);
			case LParen -> parseCallExpression(left);

			case StrictEqual -> new EqualityExpression(left, parseExpression(minPrecedence, assoc), EqualityExpression.EqualityOp.StrictEquals);
			case StrictNotEqual -> new EqualityExpression(left, parseExpression(minPrecedence, assoc), EqualityExpression.EqualityOp.StrictNotEquals);

			case LogicalOr -> new LogicalExpression(left, parseExpression(minPrecedence, assoc), LogicalExpression.LogicOp.Or);
			case LogicalAnd -> new LogicalExpression(left, parseExpression(minPrecedence, assoc), LogicalExpression.LogicOp.And);
			case NullishCoalescing -> new LogicalExpression(left, parseExpression(minPrecedence, assoc), LogicalExpression.LogicOp.Coalesce);

			case PlusEquals -> new AssignmentExpression(left, parseExpression(minPrecedence, assoc), AssignmentExpression.AssignmentOp.PlusAssign);
			case MinusEquals -> new AssignmentExpression(left, parseExpression(minPrecedence, assoc), AssignmentExpression.AssignmentOp.MinusAssign);
			case MultiplyEquals -> new AssignmentExpression(left, parseExpression(minPrecedence, assoc), AssignmentExpression.AssignmentOp.MultiplyAssign);
			case DivideEquals -> new AssignmentExpression(left, parseExpression(minPrecedence, assoc), AssignmentExpression.AssignmentOp.DivideAssign);
			case ExponentEquals -> new AssignmentExpression(left, parseExpression(minPrecedence, assoc), AssignmentExpression.AssignmentOp.ExponentAssign);
			case Equals -> new AssignmentExpression(left, parseExpression(minPrecedence, assoc), AssignmentExpression.AssignmentOp.Assign);

			case MinusMinus -> new UnaryExpression(left, UnaryExpression.UnaryOp.PostDecrement);
			case PlusPlus -> new UnaryExpression(left, UnaryExpression.UnaryOp.PostIncrement);

			case LessThan -> new RelationalExpression(left, parseExpression(minPrecedence, assoc), RelationalExpression.RelationalOp.LessThan);
			case LessThanEqual -> new RelationalExpression(left, parseExpression(minPrecedence, assoc), RelationalExpression.RelationalOp.LessThanEquals);
			case GreaterThan -> new RelationalExpression(left, parseExpression(minPrecedence, assoc), RelationalExpression.RelationalOp.GreaterThan);
			case GreaterThanEqual -> new RelationalExpression(left, parseExpression(minPrecedence, assoc), RelationalExpression.RelationalOp.GreaterThanEquals);
			case In -> new RelationalExpression(left, parseExpression(minPrecedence, assoc), RelationalExpression.RelationalOp.In);
			case Instanceof -> new RelationalExpression(left, parseExpression(minPrecedence, assoc), RelationalExpression.RelationalOp.InstanceOf);

			case Period -> {
				if (!matchIdentifierName()) state.expected("IdentifierName");
				yield new MemberExpression(left, new StringLiteral(state.consume().value), false);
			}

			case LBracket -> {
				final Expression prop = parseExpression();
				state.require(TokenType.RBracket);
				yield new MemberExpression(left, prop, true);
			}

			default -> throw new CannotParse(state.currentToken, "SecondaryExpression");
		};
	}

	private Expression parsePrimaryExpression() throws SyntaxError, CannotParse {
		if (matchUnaryPrefixedExpression()) {
			return parseUnaryPrefixedExpression();
		}

		return switch (state.currentToken.type) {
			case LParen -> {
				state.consume();
				if (state.currentToken.type == TokenType.RParen || state.currentToken.type == TokenType.Identifier) {
					final FunctionExpression result = tryParseArrowFunctionExpression(true);
					if (result != null) yield result;
				}

				final Expression expression = parseExpression();
				state.require(TokenType.RParen);
				yield expression;
			}

			case Identifier -> {
				final FunctionExpression result = tryParseArrowFunctionExpression(false);
				yield result != null ? result : new Identifier(state.consume().value);
			}

			case StringLiteral -> new StringLiteral(state.consume().value);
			case NumericLiteral -> new NumericLiteral(Double.parseDouble(state.consume().value));
			case BooleanLiteral -> BooleanLiteral.of(state.consume().value.equals("true"));
			case Function -> parseFunctionExpression();
			case LBracket -> parseArrayExpression();
			case LBrace -> parseObjectExpression();

			case This -> {
				state.consume();
				yield new ThisKeyword();
			}

			case Null -> {
				state.consume();
				yield Null.instance;
			}

			case Infinity -> {
				state.consume();
				yield new NumericLiteral(Double.POSITIVE_INFINITY);
			}

			case NaN -> {
				state.consume();
				yield new NumericLiteral(Double.NaN);
			}

			case New -> {
				final TokenType n = state.consume().type;
//				https://tc39.es/ecma262/multipage#sec-new-operator
				final Expression constructExpr = parseExpression(precedence.get(n), associativity.get(n), Collections.singleton(TokenType.LParen));
				final List<Expression> arguments = state.currentToken.type == TokenType.LParen ? parseExpressionList(true) : Collections.emptyList();
				yield new NewExpression(constructExpr, arguments.toArray(new Expression[0]));
			}

			case Undefined -> {
				state.consume();
				yield Undefined.instance;
			}

			default -> throw new CannotParse(state.currentToken, "PrimaryExpression");
		};
	}

	private FunctionExpression tryParseArrowFunctionExpression(boolean expectParens) throws SyntaxError, CannotParse {
		save();

		final Identifier[] arguments = parseArrowFunctionArguments(expectParens);
		if (arguments == null) {
			load();
			return null;
		}

		if (state.accept(TokenType.Arrow) == null) {
			load();
			return null;
		}

		final BlockStatement body = parseArrowFunctionBody();
		if (body == null) {
			load();
			return null;
		}

		return new FunctionExpression(body, null, arguments);
	}

	private BlockStatement parseArrowFunctionBody() throws CannotParse, SyntaxError {
		if (state.currentToken.type == TokenType.LBrace) {
			return parseBlockStatement();
		} else if (matchPrimaryExpression()) {
			final BlockStatement body = new BlockStatement();
			body.append(new ReturnStatement(parseExpression()));
			return body;
		} else {
			return null;
		}
	}

	private Identifier[] parseArrowFunctionArguments(boolean expectParens) {
		if (expectParens) {
			final List<Identifier> result = new ArrayList<>();
			while (state.currentToken.type == TokenType.Identifier) {
				result.add(new Identifier(state.consume().value));
				if (state.accept(TokenType.Comma) == null) break;
			}

			if (state.accept(TokenType.RParen) == null) return null;
			return result.toArray(new Identifier[0]);
		} else {
			final Token t = state.accept(TokenType.Identifier);
			if (t == null) return null;
			return new Identifier[] { new Identifier(t.value) };
		}
	}

	private ObjectExpression parseObjectExpression() throws SyntaxError, CannotParse {
		state.require(TokenType.LBrace);
		state.consumeAll(TokenType.Terminator);
		final ObjectExpression result = new ObjectExpression();

		while (state.currentToken.type == TokenType.StringLiteral || state.currentToken.type == TokenType.Identifier) {
			state.consumeAll(TokenType.Terminator);
			final StringLiteral key = new StringLiteral(state.consume().value);
			state.consumeAll(TokenType.Terminator);
			state.require(TokenType.Colon);
			state.consumeAll(TokenType.Terminator);
			result.entries().put(key, parseExpression());
			state.consumeAll(TokenType.Terminator);
			if (state.accept(TokenType.Comma) == null) break;
			state.consumeAll(TokenType.Terminator);
		}

		state.consumeAll(TokenType.Terminator);
		state.require(TokenType.RBrace);
		return result;
	}

	private ArrayExpression parseArrayExpression() throws SyntaxError, CannotParse {
		state.require(TokenType.LBracket);
		final List<Expression> elements = parseExpressionList(false);
		state.require(TokenType.RBracket);
		return new ArrayExpression(elements);
	}

	private boolean matchIdentifierName() {
		final TokenType t = state.currentToken.type;
		return t == TokenType.Let ||
			   t == TokenType.Break ||
			   t == TokenType.Case ||
			   t == TokenType.Catch ||
			   t == TokenType.Class ||
			   t == TokenType.Const ||
			   t == TokenType.Continue ||
			   t == TokenType.Debugger ||
			   t == TokenType.Default ||
			   t == TokenType.Delete ||
			   t == TokenType.Do ||
			   t == TokenType.Else ||
			   t == TokenType.Export ||
			   t == TokenType.Extends ||
			   t == TokenType.Finally ||
			   t == TokenType.For ||
			   t == TokenType.Function ||
			   t == TokenType.If ||
			   t == TokenType.Import ||
			   t == TokenType.In ||
			   t == TokenType.Instanceof ||
			   t == TokenType.New ||
			   t == TokenType.Null ||
			   t == TokenType.Return ||
			   t == TokenType.Super ||
			   t == TokenType.Switch ||
			   t == TokenType.This ||
			   t == TokenType.Throw ||
			   t == TokenType.Try ||
			   t == TokenType.Typeof ||
			   t == TokenType.Var ||
			   t == TokenType.Void ||
			   t == TokenType.While ||
			   t == TokenType.With ||
			   t == TokenType.Yield ||
			   t == TokenType.Identifier;
	}

	private boolean matchDeclaration() {
		final TokenType t = state.currentToken.type;
		return t == TokenType.Function ||
			   t == TokenType.Let ||
			   t == TokenType.Var ||
			   t == TokenType.Const;
	}

	private boolean matchStatementOrExpression() {
		final TokenType t = state.currentToken.type;
		return matchPrimaryExpression() ||
			   t == TokenType.Return ||
			   t == TokenType.Yield ||
			   t == TokenType.Do ||
			   t == TokenType.If ||
			   t == TokenType.Throw ||
			   t == TokenType.Try ||
			   t == TokenType.While ||
			   t == TokenType.With ||
			   t == TokenType.For ||
			   t == TokenType.LBrace ||
			   t == TokenType.Switch ||
			   t == TokenType.Break ||
			   t == TokenType.Continue ||
			   t == TokenType.Var ||
			   t == TokenType.Debugger ||
			   t == TokenType.Semicolon;

	}

	private boolean matchPrimaryExpression() {
		final TokenType t = state.currentToken.type;
		return t == TokenType.LParen ||
			   t == TokenType.Identifier ||
			   t == TokenType.StringLiteral ||
			   t == TokenType.NumericLiteral ||
			   t == TokenType.BooleanLiteral ||
			   t == TokenType.Function ||
			   t == TokenType.LBracket ||
			   t == TokenType.LBrace ||
			   t == TokenType.This ||
			   t == TokenType.Null ||
			   t == TokenType.New ||
			   t == TokenType.Infinity ||
			   t == TokenType.NaN ||
			   t == TokenType.Undefined ||
			   matchUnaryPrefixedExpression();
	}

	private boolean matchSecondaryExpression(Set<TokenType> forbidden) {
		final TokenType t = state.currentToken.type;
		if (forbidden.contains(t)) return false;
		return t == TokenType.Plus ||
			   t == TokenType.Minus ||
			   t == TokenType.Star ||
			   t == TokenType.Slash ||
			   t == TokenType.Exponent ||
			   t == TokenType.StrictEqual ||
			   t == TokenType.StrictNotEqual ||
			   t == TokenType.LogicalOr ||
			   t == TokenType.LogicalAnd ||
			   t == TokenType.Period ||
			   t == TokenType.LessThan ||
			   t == TokenType.LessThanEqual ||
			   t == TokenType.GreaterThan ||
			   t == TokenType.GreaterThanEqual ||
			   t == TokenType.PlusEquals ||
			   t == TokenType.MinusEquals ||
			   t == TokenType.DivideEquals ||
			   t == TokenType.MultiplyEquals ||
			   t == TokenType.LeftShiftEquals ||
			   t == TokenType.RightShiftEquals ||
			   t == TokenType.NullishCoalescing ||
			   t == TokenType.In ||
			   t == TokenType.Instanceof ||
			   t == TokenType.LBracket ||
			   t == TokenType.LParen ||
			   t == TokenType.Equals ||
			   t == TokenType.PlusPlus ||
			   t == TokenType.MinusMinus;
	}

	private boolean matchUnaryPrefixedExpression() {
		final TokenType t = state.currentToken.type;
		return t == TokenType.PlusPlus ||
			   t == TokenType.MinusMinus ||
			   t == TokenType.Bang ||
			   t == TokenType.Tilde ||
			   t == TokenType.Plus ||
			   t == TokenType.Minus ||
			   t == TokenType.Typeof ||
			   t == TokenType.Void ||
			   t == TokenType.Delete;
	}
}