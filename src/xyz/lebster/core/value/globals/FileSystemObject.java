package xyz.lebster.core.value.globals;

import xyz.lebster.core.NonStandard;
import xyz.lebster.core.StringEscapeUtils;
import xyz.lebster.core.interpreter.AbruptCompletion;
import xyz.lebster.core.interpreter.Interpreter;
import xyz.lebster.core.interpreter.Intrinsics;
import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.Value;
import xyz.lebster.core.value.array.ArrayObject;
import xyz.lebster.core.value.error.type.TypeError;
import xyz.lebster.core.value.object.ObjectValue;
import xyz.lebster.core.value.primitive.string.StringValue;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.nio.file.Files;
import java.nio.file.Path;

import static xyz.lebster.core.interpreter.AbruptCompletion.error;
import static xyz.lebster.core.value.function.NativeFunction.argument;

@NonStandard
public final class FileSystemObject extends ObjectValue {
	public FileSystemObject(Intrinsics intrinsics) {
		super(Null.instance);

		putMethod(intrinsics, Names.cwd, 2, FileSystemObject::cwd);
		putMethod(intrinsics, Names.readFile, 2, FileSystemObject::readFile);
		putMethod(intrinsics, Names.readDir, 1, FileSystemObject::readDir);
	}

	private static StringValue readFile(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// readFile(path: string, encoding: Charset): string
		if (arguments.length != 2) throw error(new TypeError(interpreter, "readFile requires 2 arguments: path and charset"));
		final Value<?> pathArgument = argument(0, arguments);
		if (!(pathArgument instanceof final StringValue path))
			throw error(new TypeError(interpreter, "%s is not a string".formatted(pathArgument.toDisplayString(true))));
		final Value<?> charsetArgument = argument(1, arguments);
		if (!(charsetArgument instanceof final StringValue charsetString))
			throw error(new TypeError(interpreter, "%s is not a string".formatted(charsetArgument.toDisplayString(true))));

		Charset charset;
		try {
			charset = Charset.forName(charsetString.value);
		} catch (IllegalCharsetNameException e) {
			throw error(new TypeError(interpreter, "Illegal charset name: %s".formatted(StringEscapeUtils.quote(charsetString.value, false))));
		} catch (UnsupportedCharsetException e) {
			throw error(new TypeError(interpreter, "Unsupported charset: %s".formatted(e.getCharsetName())));
		}

		try {
			final String s = Files.readString(Path.of(path.value), charset);
			return new StringValue(s);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static ArrayObject readDir(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// readDir(path: string): string[]
		if (arguments.length != 1) throw error(new TypeError(interpreter, "readDir requires 1 argument: path"));
		final Value<?> pathArgument = argument(0, arguments);
		if (!(pathArgument instanceof final StringValue path))
			throw error(new TypeError(interpreter, "%s is not a string".formatted(pathArgument.toDisplayString(true))));
		final File directory = new File(path.value);
		if (!directory.exists())
			throw error(new TypeError(interpreter, "%s does not exist".formatted(StringEscapeUtils.quote(path.value, false))));
		if (!directory.isDirectory())
			throw error(new TypeError(interpreter, "%s is not a directory".formatted(StringEscapeUtils.quote(path.value, false))));
		final File[] files = directory.listFiles();
		if (files == null)
			throw error(new TypeError(interpreter, "An IO error occurred while trying to list " + StringEscapeUtils.quote(path.value, false)));

		StringValue[] result = new StringValue[files.length];
		for (int i = 0; i < files.length; i++)
			result[i] = new StringValue(files[i].getName());
		return new ArrayObject(interpreter, result);
	}

	private static StringValue cwd(Interpreter interpreter, Value<?>[] arguments) throws AbruptCompletion {
		// cwd(): string
		if (arguments.length != 0) {
			throw error(new TypeError(interpreter, "cwd() called with >0 arguments"));
		} else {
			return new StringValue(System.getProperty("user.dir"));
		}
	}
}
