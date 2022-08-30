package xyz.lebster.core.interpreter;

import xyz.lebster.core.value.Names;
import xyz.lebster.core.value.array.ArrayConstructor;
import xyz.lebster.core.value.array.ArrayPrototype;
import xyz.lebster.core.value.error.*;
import xyz.lebster.core.value.function.Executable;
import xyz.lebster.core.value.function.FunctionConstructor;
import xyz.lebster.core.value.function.FunctionPrototype;
import xyz.lebster.core.value.globals.ConsoleObject;
import xyz.lebster.core.value.globals.MathObject;
import xyz.lebster.core.value.globals.TestObject;
import xyz.lebster.core.value.object.ObjectConstructor;
import xyz.lebster.core.value.object.ObjectPrototype;
import xyz.lebster.core.value.object.ObjectValue;
import xyz.lebster.core.value.primitive.boolean_.BooleanConstructor;
import xyz.lebster.core.value.primitive.boolean_.BooleanPrototype;
import xyz.lebster.core.value.primitive.number.NumberConstructor;
import xyz.lebster.core.value.primitive.number.NumberPrototype;
import xyz.lebster.core.value.primitive.string.StringConstructor;
import xyz.lebster.core.value.primitive.string.StringPrototype;
import xyz.lebster.core.value.primitive.symbol.SymbolConstructor;
import xyz.lebster.core.value.primitive.symbol.SymbolPrototype;
import xyz.lebster.core.value.regexp.RegExpConstructor;
import xyz.lebster.core.value.regexp.RegExpPrototype;
import xyz.lebster.core.value.shadowrealm.ShadowRealmConstructor;
import xyz.lebster.core.value.shadowrealm.ShadowRealmPrototype;

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

	public final ErrorConstructor errorConstructor;
	public final ErrorPrototype errorPrototype;
	public final RangeErrorConstructor rangeErrorConstructor;
	public final RangeErrorPrototype rangeErrorPrototype;
	public final ReferenceErrorConstructor referenceErrorConstructor;
	public final ReferenceErrorPrototype referenceErrorPrototype;
	public final TypeErrorConstructor typeErrorConstructor;
	public final TypeErrorPrototype typeErrorPrototype;

	public final TestObject testObject;
	public final ConsoleObject consoleObject;
	public final MathObject mathObject;

	Intrinsics() {
		objectPrototype = new ObjectPrototype();
		functionPrototype = new FunctionPrototype(this);
		linkPrototypeAndConstructor(functionConstructor = new FunctionConstructor(this), functionPrototype);
		objectPrototype.populateMethods(functionPrototype);
		linkPrototypeAndConstructor(objectConstructor = new ObjectConstructor(this), objectPrototype);

		linkPrototypeAndConstructor(arrayConstructor = new ArrayConstructor(this), arrayPrototype = new ArrayPrototype(this));
		linkPrototypeAndConstructor(booleanConstructor = new BooleanConstructor(this), booleanPrototype = new BooleanPrototype(this));
		linkPrototypeAndConstructor(errorConstructor = new ErrorConstructor(this), errorPrototype = new ErrorPrototype(this));
		linkPrototypeAndConstructor(numberConstructor = new NumberConstructor(this), numberPrototype = new NumberPrototype(this));
		linkPrototypeAndConstructor(rangeErrorConstructor = new RangeErrorConstructor(this), rangeErrorPrototype = new RangeErrorPrototype(this));
		linkPrototypeAndConstructor(referenceErrorConstructor = new ReferenceErrorConstructor(this), referenceErrorPrototype = new ReferenceErrorPrototype(this));
		linkPrototypeAndConstructor(regExpConstructor = new RegExpConstructor(this), regExpPrototype = new RegExpPrototype(this));
		linkPrototypeAndConstructor(shadowRealmConstructor = new ShadowRealmConstructor(this), shadowRealmPrototype = new ShadowRealmPrototype(this));
		linkPrototypeAndConstructor(stringConstructor = new StringConstructor(this), stringPrototype = new StringPrototype(this));
		linkPrototypeAndConstructor(symbolConstructor = new SymbolConstructor(this), symbolPrototype = new SymbolPrototype(this));
		linkPrototypeAndConstructor(typeErrorConstructor = new TypeErrorConstructor(this), typeErrorPrototype = new TypeErrorPrototype(this));

		mathObject = new MathObject(this);
		testObject = new TestObject(this);
		consoleObject = new ConsoleObject(this);
	}

	private static void linkPrototypeAndConstructor(Executable constructor, ObjectValue prototype) {
		constructor.put(Names.prototype, prototype, false, false, false);
		prototype.put(Names.constructor, constructor);
	}
}
