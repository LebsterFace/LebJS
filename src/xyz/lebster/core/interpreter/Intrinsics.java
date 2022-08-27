package xyz.lebster.core.interpreter;

import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.array.ArrayConstructor;
import xyz.lebster.core.value.array.ArrayPrototype;
import xyz.lebster.core.value.boolean_.BooleanConstructor;
import xyz.lebster.core.value.boolean_.BooleanPrototype;
import xyz.lebster.core.value.error.ErrorConstructor;
import xyz.lebster.core.value.error.ErrorPrototype;
import xyz.lebster.core.value.function.Executable;
import xyz.lebster.core.value.function.FunctionConstructor;
import xyz.lebster.core.value.function.FunctionPrototype;
import xyz.lebster.core.value.globals.ConsoleObject;
import xyz.lebster.core.value.globals.MathObject;
import xyz.lebster.core.value.globals.TestObject;
import xyz.lebster.core.value.number.NumberConstructor;
import xyz.lebster.core.value.number.NumberPrototype;
import xyz.lebster.core.value.object.ObjectConstructor;
import xyz.lebster.core.value.object.ObjectPrototype;
import xyz.lebster.core.value.object.ObjectValue;
import xyz.lebster.core.value.regexp.RegExpConstructor;
import xyz.lebster.core.value.regexp.RegExpPrototype;
import xyz.lebster.core.value.shadowrealm.ShadowRealmConstructor;
import xyz.lebster.core.value.shadowrealm.ShadowRealmPrototype;
import xyz.lebster.core.value.string.StringConstructor;
import xyz.lebster.core.value.string.StringPrototype;
import xyz.lebster.core.value.symbol.SymbolConstructor;
import xyz.lebster.core.value.symbol.SymbolPrototype;

public final class Intrinsics {
	public final ArrayConstructor arrayConstructor;
	public final ArrayPrototype arrayPrototype;
	public final BooleanConstructor booleanConstructor;
	public final BooleanPrototype booleanPrototype;
	public final FunctionConstructor functionConstructor;
	public final FunctionPrototype functionPrototype;
	public final NumberConstructor numberConstructor;
	public final NumberPrototype numberPrototype;
	public final ObjectConstructor objectConstructor;
	public final ObjectPrototype objectPrototype;
	public final RegExpConstructor regExpConstructor;
	public final RegExpPrototype regExpPrototype;
	public final ShadowRealmConstructor shadowRealmConstructor;
	public final ShadowRealmPrototype shadowRealmPrototype;
	public final StringConstructor stringConstructor;
	public final StringPrototype stringPrototype;
	public final SymbolConstructor symbolConstructor;
	public final SymbolPrototype symbolPrototype;

	public final TestObject testObject;
	public final ConsoleObject consoleObject;
	public final MathObject mathObject;

	public final ErrorConstructor errorConstructor;
	public final ErrorPrototype errorPrototype;

	Intrinsics() {
		objectPrototype = new ObjectPrototype();
		functionPrototype = new FunctionPrototype(objectPrototype);
		linkPrototypeAndConstructor(
			functionConstructor = new FunctionConstructor(objectPrototype, functionPrototype),
			functionPrototype
		);

		objectPrototype.populateMethods(functionPrototype);
		objectConstructor = new ObjectConstructor(objectPrototype, functionPrototype);
		linkPrototypeAndConstructor(objectConstructor, objectPrototype);

		linkPrototypeAndConstructor(
			arrayConstructor = new ArrayConstructor(objectPrototype, functionPrototype),
			arrayPrototype = new ArrayPrototype(objectPrototype, functionPrototype)
		);

		linkPrototypeAndConstructor(
			booleanConstructor = new BooleanConstructor(functionPrototype),
			booleanPrototype = new BooleanPrototype(objectPrototype, functionPrototype)
		);

		linkPrototypeAndConstructor(
			numberConstructor = new NumberConstructor(functionPrototype),
			numberPrototype = new NumberPrototype(objectPrototype, functionPrototype)
		);

		linkPrototypeAndConstructor(
			stringConstructor = new StringConstructor(functionPrototype),
			stringPrototype = new StringPrototype(objectPrototype, functionPrototype)
		);

		linkPrototypeAndConstructor(
			shadowRealmConstructor = new ShadowRealmConstructor(objectPrototype, functionPrototype),
			shadowRealmPrototype = new ShadowRealmPrototype(objectPrototype, functionPrototype)
		);

		linkPrototypeAndConstructor(
			symbolConstructor = new SymbolConstructor(functionPrototype),
			symbolPrototype = new SymbolPrototype(objectPrototype, functionPrototype)
		);

		linkPrototypeAndConstructor(
			errorConstructor = new ErrorConstructor(objectPrototype, functionPrototype),
			errorPrototype = new ErrorPrototype(objectPrototype)
		);

		linkPrototypeAndConstructor(
			regExpConstructor = new RegExpConstructor(objectPrototype, functionPrototype),
			regExpPrototype = new RegExpPrototype(objectPrototype)
		);

		mathObject = new MathObject(objectPrototype, functionPrototype);
		testObject = new TestObject(functionPrototype);
		consoleObject = new ConsoleObject(functionPrototype);
	}

	private static void linkPrototypeAndConstructor(Executable constructor, ObjectValue prototype) {
		constructor.putFrozen(Names.prototype, prototype);
		prototype.put(Names.constructor, constructor);
	}
}
