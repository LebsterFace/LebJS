// Copyright 2018 Ulf Adams
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

// NOTE: This code is a modified version of https://github.com/ulfjack/ryu
//       Specifically, it comes from src/main/java/info/adams/ryu/analysis/SlowConversion.java
//       If more performance is needed in the future, src/main/java/info/adams/ryu/RyuDouble.java may be used instead.

package xyz.lebster.core;

import java.math.BigInteger;

public class Ryu {
	private static final int MANTISSA_BITS = 52;
	private static final int EXPONENT_BITS = 11;

	public static String doubleToString(double value, boolean scientificNotation) {
		final long bits = Double.doubleToLongBits(value);

		// Step 1: Decode the floating point number, and unify normalized and subnormal cases.
		// The format of all IEEE numbers is S E* M*; we obtain M by masking the lower M bits, E by
		// shifting and masking, and S also by shifting and masking.
		final int ieeeExponent = (int) (bits >>> MANTISSA_BITS & (1 << EXPONENT_BITS) - 1);
		final long ieeeMantissa = bits & (1L << MANTISSA_BITS) - 1;
		final boolean isNegative = (bits >>> MANTISSA_BITS + EXPONENT_BITS & 1) != 0;
		final boolean even = (bits & 1) == 0;

		// Exit early if it's NaN, Infinity, or 0.
		if (ieeeExponent == (1 << EXPONENT_BITS) - 1) {
			// Handle the special cases where the exponent is all 1s indicating NaN or Infinity:
			// if the mantissa is non-zero, it's a NaN, otherwise it's +/-infinity.
			if (ieeeMantissa != 0) return "NaN";
			return isNegative ? "-Infinity" : "Infinity";
		} else if (ieeeExponent == 0 && ieeeMantissa == 0) {
			// If the mantissa is 0, the code below would end up with a lower bound that is less than 0,
			// which throws off the char-by-char comparison. Instead, we exit here with the correct string.
			// NOTE: If JavaScript did not collapse both -0 and 0 to '0', the following line would be: isNegative ? "-0" : "0";
			return "0";
		}

		final int offset = (1 << EXPONENT_BITS - 1) - 1; // Compute the offset used by the IEEE format.

		// Unify normalized and subnormal cases.
		int e2;
		final long m2;
		if (ieeeExponent == 0) {
			e2 = 1 - offset - MANTISSA_BITS;
			m2 = ieeeMantissa;
		} else {
			e2 = ieeeExponent - offset - MANTISSA_BITS;
			m2 = ieeeMantissa | 1L << MANTISSA_BITS;
		}

		// Step 2: Determine the interval of legal decimal representations.
		final long mv = 4 * m2;
		final long mp = 4 * m2 + 2;
		final long mm = 4 * m2 - (m2 != 1L << MANTISSA_BITS || ieeeExponent == 1 ? 2 : 1);
		e2 -= 2;

		// Step 3: Convert to a decimal power base using arbitrary-precision arithmetic.
		BigInteger vr;
		BigInteger vp;
		BigInteger vm;
		int e10;
		if (e2 >= 0) {
			vr = BigInteger.valueOf(mv).shiftLeft(e2);
			vp = BigInteger.valueOf(mp).shiftLeft(e2);
			vm = BigInteger.valueOf(mm).shiftLeft(e2);
			e10 = 0;
		} else {
			BigInteger factor = BigInteger.valueOf(5).pow(-e2);
			vr = BigInteger.valueOf(mv).multiply(factor);
			vp = BigInteger.valueOf(mp).multiply(factor);
			vm = BigInteger.valueOf(mm).multiply(factor);
			e10 = e2;
		}

		// Step 4: Find the shortest decimal representation in the interval of legal representations.
		// We do some extra work here in order to follow Float/Double.toString semantics. In particular,
		// that requires printing in scientific format if and only if the exponent is between -3 and 7,
		// and it requires printing at least two decimal digits.
		// Above, we moved the decimal dot all the way to the right, so now we need to count digits to
		// figure out the correct exponent for scientific notation.
		final int vpLength = vp.toString().length();
		e10 += vpLength - 1;

		if (!even) vp = vp.subtract(BigInteger.ONE);
		boolean vmIsTrailingZeros = true;
		// Track if vr is tailing zeroes _after_ lastRemovedDigit.
		boolean vrIsTrailingZeros = true;
		int removed = 0;
		int lastRemovedDigit = 0;
		while (!vp.divide(BigInteger.TEN).equals(vm.divide(BigInteger.TEN))) {
			if (scientificNotation && vp.compareTo(BigInteger.valueOf(100)) < 0) {
				// Float/Double.toString semantics requires printing at least two digits.
				break;
			}

			vmIsTrailingZeros &= vm.mod(BigInteger.TEN).intValueExact() == 0;
			vrIsTrailingZeros &= lastRemovedDigit == 0;
			lastRemovedDigit = vr.mod(BigInteger.TEN).intValueExact();
			vp = vp.divide(BigInteger.TEN);
			vr = vr.divide(BigInteger.TEN);
			vm = vm.divide(BigInteger.TEN);
			removed++;
		}

		if (vmIsTrailingZeros && even) {
			while (vm.mod(BigInteger.TEN).intValueExact() == 0) {
				if (scientificNotation && vp.compareTo(BigInteger.valueOf(100)) < 0) {
					// Float/Double.toString semantics requires printing at least two digits.
					break;
				}

				vrIsTrailingZeros &= lastRemovedDigit == 0;
				lastRemovedDigit = vr.mod(BigInteger.TEN).intValueExact();
				vp = vp.divide(BigInteger.TEN);
				vr = vr.divide(BigInteger.TEN);
				vm = vm.divide(BigInteger.TEN);
				removed++;
			}
		}

		// Round down not up if the number ends in X50000 and the number is even.
		if (vrIsTrailingZeros && lastRemovedDigit == 5 && vr.mod(BigInteger.TWO).intValueExact() == 0)
			lastRemovedDigit = 4;

		final String output = (vr.compareTo(vm) > 0 ? lastRemovedDigit >= 5 ? vr.add(BigInteger.ONE) : vr : vp).toString();
		final int olength = vpLength - removed;

		// Step 5: Print the decimal representation.
		// We follow Float/Double.toString semantics here.
		final StringBuilder result = new StringBuilder();
		// Add the minus sign if the number is negative.
		if (isNegative) result.append('-');

		if (scientificNotation) {
			result.append(output.charAt(0));
			result.append('.');
			for (int i = 1; i < olength; i++) {
				result.append(output.charAt(i));
			}

			if (olength == 1) {
				result.append('0');
			}

			removeTrailingZeroes(result);

			result.append('e');
			if (e10 >= 0) {
				result.append('+');
			}

			result.append(e10);
		} else {
			// Print leading 0s and '.' if applicable.
			for (int i = 0; i > e10; i--) {
				result.append('0');
				if (i == 0) {
					result.append(".");
				}
			}
			// Print number and '.' if applicable.
			for (int i = 0; i < olength; i++) {
				result.append(output.charAt(i));
				if (e10 == 0) {
					result.append('.');
				}
				e10--;
			}
			// Print trailing 0s and '.' if applicable.
			for (; e10 >= -1; e10--) {
				result.append('0');
				if (e10 == 0) {
					result.append('.');
				}
			}

			removeTrailingZeroes(result);
		}

		return result.toString();
	}

	public static void removeTrailingZeroes(StringBuilder builder) {
		while (builder.charAt(builder.length() - 1) == '0') {
			builder.deleteCharAt(builder.length() - 1);
		}

		if (builder.charAt(builder.length() - 1) == '.') {
			builder.deleteCharAt(builder.length() - 1);
		}
	}
}
