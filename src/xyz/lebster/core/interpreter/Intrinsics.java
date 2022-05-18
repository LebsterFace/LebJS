package xyz.lebster.core.interpreter;

import xyz.lebster.core.runtime.value.constructor.*;
import xyz.lebster.core.runtime.value.object.ConsoleObject;
import xyz.lebster.core.runtime.value.object.MathObject;
import xyz.lebster.core.runtime.value.object.TestObject;
import xyz.lebster.core.runtime.value.prototype.*;

public final class Intrinsics {
	public final ObjectPrototype objectPrototype;
	public final FunctionPrototype functionPrototype;
	public final FunctionConstructor functionConstructor;
	public final ObjectConstructor objectConstructor;
	public final ArrayConstructor arrayConstructor;
	public final ArrayPrototype arrayPrototype;
	public final BooleanConstructor booleanConstructor;
	public final BooleanPrototype booleanPrototype;
	public final NumberConstructor numberConstructor;
	public final NumberPrototype numberPrototype;
	public final ShadowRealmConstructor shadowRealmConstructor;
	public final ShadowRealmPrototype shadowRealmPrototype;
	public final StringConstructor stringConstructor;
	public final StringPrototype stringPrototype;
	public final SymbolConstructor symbolConstructor;
	public final SymbolPrototype symbolPrototype;

	public final TestObject testObject;
	public final ConsoleObject consoleObject;
	public final MathObject mathObject;


	Intrinsics() {
		objectPrototype = new ObjectPrototype();

		functionPrototype = new FunctionPrototype(objectPrototype);
		functionConstructor = new FunctionConstructor(objectPrototype, functionPrototype);
		functionConstructor.linkToPrototype(functionPrototype);

		objectPrototype.populateMethods(functionPrototype);
		objectConstructor = new ObjectConstructor(objectPrototype, functionPrototype);
		objectConstructor.linkToPrototype(objectPrototype);

		arrayPrototype = new ArrayPrototype(objectPrototype, functionPrototype);
		arrayConstructor = new ArrayConstructor(objectPrototype, functionPrototype);
		arrayConstructor.linkToPrototype(arrayPrototype);

		booleanPrototype = new BooleanPrototype(objectPrototype);
		booleanConstructor = new BooleanConstructor(objectPrototype, functionPrototype);
		booleanConstructor.linkToPrototype(booleanPrototype);

		numberPrototype = new NumberPrototype(objectPrototype, functionPrototype);
		numberConstructor = new NumberConstructor(objectPrototype, functionPrototype);
		numberConstructor.linkToPrototype(numberPrototype);

		stringPrototype = new StringPrototype(objectPrototype, functionPrototype);
		stringConstructor = new StringConstructor(objectPrototype, functionPrototype);
		stringConstructor.linkToPrototype(stringPrototype);

		shadowRealmPrototype = new ShadowRealmPrototype(objectPrototype, functionPrototype);
		shadowRealmConstructor = new ShadowRealmConstructor(objectPrototype, functionPrototype);
		shadowRealmConstructor.linkToPrototype(shadowRealmPrototype);

		symbolPrototype = new SymbolPrototype(objectPrototype);
		symbolConstructor = new SymbolConstructor(functionPrototype);
		symbolConstructor.linkToPrototype(symbolPrototype);

		mathObject = new MathObject(objectPrototype, functionPrototype);
		testObject = new TestObject(functionPrototype);
		consoleObject = new ConsoleObject(functionPrototype);
	}
}
