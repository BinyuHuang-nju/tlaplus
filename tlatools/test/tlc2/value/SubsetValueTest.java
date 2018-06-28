/*******************************************************************************
 * Copyright (c) 2018 Microsoft Research. All rights reserved.
 *
 * The MIT License (MIT)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN
 * AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * Contributors:
 *   Markus Alexander Kuppe - initial API and implementation
 ******************************************************************************/
package tlc2.value;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.junit.BeforeClass;
import org.junit.Test;

import tlc2.util.FP64;
import tlc2.value.SubsetValue.CoinTossingSubsetEnumerator;
import tlc2.value.SubsetValue.KElementEnumerator;
import tlc2.value.SubsetValue.SubsetEnumerator;
import util.Assert;

public class SubsetValueTest {

	private static final Value[] getValue(String... strs) {
		final List<Value> values = new ArrayList<>(strs.length);
		for (int i = 0; i < strs.length; i++) {
			values.add(new StringValue(strs[i]));
		}
		Collections.shuffle(values);
		return values.toArray(new Value[values.size()]);
	}

	@BeforeClass
	public static void setup() {
		// Make test repeatable by setting random seed always to same value. 
		EnumerableValue.setRandom(15041980L);
		// Needed to insert elements into java.util.Set (because of hashcode) later to
		// detect duplicates.
		FP64.Init();
	}

	private void doTest(final int expectedSize, final EnumerableValue innerSet) {
		doTest(expectedSize, innerSet, expectedSize);
	}

	private void doTest(final int expectedSize, final EnumerableValue innerSet,
			int expectedElements) {
		final SubsetValue subsetValue = new SubsetValue(innerSet);
		assertEquals(expectedSize, subsetValue.size());

		final Set<Value> s = new TreeSet<>(new Comparator<Value>() {
			@Override
			public int compare(Value o1, Value o2) {
				// o1.normalize();
				// ((SetEnumValue) o1).elems.sort(true);
				//
				// o2.normalize();
				// ((SetEnumValue) o2).elems.sort(true);

				return o1.compareTo(o2);
			}
		});
		
		final ValueEnumeration elements = subsetValue.elements(expectedElements);
		assertTrue(elements instanceof SubsetEnumerator);
		
		SetEnumValue next = null;
		while ((next = (SetEnumValue) elements.nextElement()) != null) {
			final int size = next.elems.size();
			assertTrue(0 <= size && size <= innerSet.size());
			s.add(next);
		}
		assertEquals(expectedElements, s.size());
	}

	@Test
	public void testRandomSubsetE7F1() {
		final SetEnumValue innerSet = new SetEnumValue(getValue("a", "b", "c", "d", "e", "f", "g"), true);
		doTest(1 << innerSet.size(), innerSet);
	}

	@Test
	public void testRandomSubsetE7F05() {
		final SetEnumValue innerSet = new SetEnumValue(getValue("a", "b", "c", "d", "e", "f", "g"), true);
		doTest(1 << innerSet.size(), innerSet, 64);
	}

	@Test
	public void testRandomSubsetE6F1() {
		final SetEnumValue innerSet = new SetEnumValue(getValue("a", "b", "c", "d", "e", "f"), true);
		doTest(1 << innerSet.size(), innerSet);
	}

	@Test
	public void testRandomSubsetE5F01() {
		final SetEnumValue innerSet = new SetEnumValue(getValue("a", "b", "c", "d", "e"), true);
		doTest(1 << innerSet.size(), innerSet, 4);
	}

	@Test
	public void testRandomSubsetE5F025() {
		final SetEnumValue innerSet = new SetEnumValue(getValue("a", "b", "c", "d", "e"), true);
		doTest(1 << innerSet.size(), innerSet, 8);
	}

	@Test
	public void testRandomSubsetE5F05() {
		final SetEnumValue innerSet = new SetEnumValue(getValue("a", "b", "c", "d", "e"), true);
		doTest(1 << innerSet.size(), innerSet, 16);
	}

	@Test
	public void testRandomSubsetE5F075() {
		final SetEnumValue innerSet = new SetEnumValue(getValue("a", "b", "c", "d", "e"), true);
		doTest(1 << innerSet.size(), innerSet, 24);
	}

	@Test
	public void testRandomSubsetE5F1() {
		final SetEnumValue innerSet = new SetEnumValue(getValue("a", "b", "c", "d", "e"), true);
		doTest(1 << innerSet.size(), innerSet);
	}

	@Test
	public void testRandomSubsetE32F1ENeg6() {
		final IntervalValue innerSet = new IntervalValue(1, 32);
		final SubsetValue subsetValue = new SubsetValue(innerSet);

		ValueEnumeration elements = subsetValue.elements(2342);
		assertTrue(elements instanceof CoinTossingSubsetEnumerator);

		final Set<Value> s = new HashSet<>();
		SetEnumValue next = null;
		while ((next = (SetEnumValue) elements.nextElement()) != null) {
			final int size = next.elems.size();
			assertTrue(0 <= size && size <= innerSet.size());
			s.add(next);
		}

		CoinTossingSubsetEnumerator tossingEnumerator = (CoinTossingSubsetEnumerator) elements;
		assertTrue(tossingEnumerator.getNumOfPicks() - 100 <= s.size() && s.size() <= tossingEnumerator.getNumOfPicks());
	}

	@Test
	public void testRandomSubsetE17F1ENeg3() {
		final IntervalValue innerSet = new IntervalValue(1, 17);
		final SubsetValue subsetValue = new SubsetValue(innerSet);

		final ValueEnumeration elements = subsetValue.elements(4223);
		assertTrue(elements instanceof SubsetEnumerator);

		final Set<Value> s = new HashSet<>();
		SetEnumValue next = null;
		while ((next = (SetEnumValue) elements.nextElement()) != null) {
			final int size = next.elems.size();
			assertTrue(0 <= size && size <= innerSet.size());
			s.add(next);
		}

		final SubsetEnumerator enumerator = (SubsetEnumerator) elements;
		assertEquals(enumerator.k, s.size());
	}

	@Test
	public void testRandomSubsetSubset16() {
		final SetEnumValue innerSet = new SetEnumValue(getValue("a", "b"), true);
		final SubsetValue innerSubset = new SubsetValue(innerSet);
		final SubsetValue subsetValue = new SubsetValue(innerSubset);

		final int expectedSize = 1 << innerSubset.size();
		assertEquals(expectedSize, subsetValue.size());

		// No duplicates
		final Set<Value> s = new HashSet<>(expectedSize);
		final ValueEnumeration elements = subsetValue.elements(expectedSize);
		Value next = null;
		while ((next = elements.nextElement()) != null) {
			s.add(next);
		}
		assertEquals(expectedSize, s.size());
	}

	@Test
	public void testRandomSubsetSubset256() {
		final SetEnumValue innerSet = new SetEnumValue(getValue("a", "b", "c"), true);
		final SubsetValue innerSubset = new SubsetValue(innerSet);
		final SubsetValue subsetValue = new SubsetValue(innerSubset);

		final int expectedSize = 1 << innerSubset.size();
		assertEquals(expectedSize, subsetValue.size());

		// No duplicates
		final Set<Value> s = new HashSet<>(expectedSize);
		final ValueEnumeration elements = subsetValue.elements(expectedSize);
		Value next = null;
		while ((next = elements.nextElement()) != null) {
			s.add(next);
		}
		assertEquals(expectedSize, s.size());
	}

	@Test
	public void testRandomSubsetSubset65536() {
		final SetEnumValue innerSet = new SetEnumValue(getValue("a", "b", "c", "d"), true);
		final SubsetValue innerSubset = new SubsetValue(innerSet);
		final SubsetValue subsetValue = new SubsetValue(innerSubset);

		final int expectedSize = 1 << innerSubset.size();
		assertEquals(expectedSize, subsetValue.size());

		// No duplicates
		final Set<Value> s = new HashSet<>(expectedSize);
		final ValueEnumeration elements = subsetValue.elements(expectedSize);
		Value next = null;
		while ((next = elements.nextElement()) != null) {
			s.add(next);
		}
		assertEquals(expectedSize, s.size());
	}

	@Test
	public void testRandomSubsetSubsetNoOverflow() {
		final SetEnumValue innerSet = new SetEnumValue(getValue("a", "b", "c", "d", "e"), true);
		final SubsetValue innerSubset = new SubsetValue(innerSet);
		final SubsetValue subsetValue = new SubsetValue(innerSubset);

		try {
			subsetValue.size();
		} catch (Assert.TLCRuntimeException e) {
			final Set<Value> s = new HashSet<>();

			final ValueEnumeration elements = subsetValue.elements(2148);
			assertTrue(elements instanceof CoinTossingSubsetEnumerator);
			Value next = null;
			while ((next = elements.nextElement()) != null) {
				s.add(next);
			}
			// No duplicates
			assertEquals(2148, s.size());
		}
	}
	
	@Test
	public void testKSubsetEnumerator() {
		final SetEnumValue innerSet = new SetEnumValue(getValue("a", "b", "c", "d"), true);
		final SubsetValue subset = new SubsetValue(innerSet);
		
		assertEquals(1, subset.numberOfKElements(0));
		assertEquals(4, subset.numberOfKElements(1));
		assertEquals(6, subset.numberOfKElements(2));
		assertEquals(4, subset.numberOfKElements(3));
		assertEquals(1, subset.numberOfKElements(4));

		ValueEnumeration enumerator = subset.kElements(0);
		assertEquals(new SetEnumValue(), enumerator.nextElement());
		assertNull(enumerator.nextElement());
		
		// Need to sort KElementEnumerator to be able to predict the order in which
		// elements get returned.
		enumerator = ((KElementEnumerator) subset.kElements(1)).sort();
		assertEquals(new SetEnumValue(getValue("a"), false), enumerator.nextElement());
		assertEquals(new SetEnumValue(getValue("b"), false), enumerator.nextElement());
		assertEquals(new SetEnumValue(getValue("c"), false), enumerator.nextElement());
		assertEquals(new SetEnumValue(getValue("d"), false), enumerator.nextElement());
		assertNull(enumerator.nextElement());
		
		enumerator = ((KElementEnumerator) subset.kElements(2)).sort();
		assertEquals(new SetEnumValue(getValue("a", "b"), false), enumerator.nextElement());
		assertEquals(new SetEnumValue(getValue("a", "c"), false), enumerator.nextElement());
		assertEquals(new SetEnumValue(getValue("b", "c"), false), enumerator.nextElement());
		assertEquals(new SetEnumValue(getValue("a", "d"), false), enumerator.nextElement());
		assertEquals(new SetEnumValue(getValue("b", "d"), false), enumerator.nextElement());
		assertEquals(new SetEnumValue(getValue("c", "d"), false), enumerator.nextElement());
		assertNull(enumerator.nextElement());

		enumerator = ((KElementEnumerator) subset.kElements(3)).sort();
		assertEquals(new SetEnumValue(getValue("a", "b", "c"), false), enumerator.nextElement());
		assertEquals(new SetEnumValue(getValue("a", "b", "d"), false), enumerator.nextElement());
		assertEquals(new SetEnumValue(getValue("a", "c", "d"), false), enumerator.nextElement());
		assertEquals(new SetEnumValue(getValue("b", "c", "d"), false), enumerator.nextElement());
		assertNull(enumerator.nextElement());
		
		enumerator = ((KElementEnumerator) subset.kElements(4)).sort();
		assertEquals(new SetEnumValue(getValue("a", "b", "c", "d"), false), enumerator.nextElement());
		assertNull(enumerator.nextElement());
	}
	
	@Test
	public void testKSubsetEnumeratorNegative() {
		final SetEnumValue innerSet = new SetEnumValue(getValue("a", "b", "c", "d"), true);
		final SubsetValue subset = new SubsetValue(innerSet);
		try {
			subset.kElements(-1);
		} catch (IllegalArgumentException e) {
			return;
		}
		fail();
	}
	
	@Test
	public void testKSubsetEnumeratorGTCapacity() {
		final SetEnumValue innerSet = new SetEnumValue(getValue("a", "b", "c", "d"), true);
		final SubsetValue subset = new SubsetValue(innerSet);
		try {
			subset.kElements(innerSet.size() + 1);
		} catch (IllegalArgumentException e) {
			return;
		}
		fail();
	}
	
	@Test
	public void testNumKSubset() {
		final SetEnumValue innerSet = new SetEnumValue(getValue("a", "b", "c", "d", "e"), true);
		final SubsetValue subset = new SubsetValue(innerSet);

		assertEquals(1, subset.numberOfKElements(0));
		assertEquals(5, subset.numberOfKElements(1));
		assertEquals(10, subset.numberOfKElements(2));
		assertEquals(10, subset.numberOfKElements(3));
		assertEquals(5, subset.numberOfKElements(4));
		assertEquals(1, subset.numberOfKElements(5));
	}

	@Test
	public void testNumKSubset2() {
		final SetEnumValue innerSet = new SetEnumValue(getValue("a", "b", "c", "d", "e", "f", "g", "h"), true);
		final SubsetValue subset = new SubsetValue(innerSet);

		int sum = 0;
		for (int i = 0; i <= innerSet.size(); i++) {
			sum += subset.numberOfKElements(i);
		}
		assertEquals(1 << innerSet.size(), sum);
	}
	
	@Test
	public void testNumKSubsetNeg() {
		final SetEnumValue innerSet = new SetEnumValue(getValue("a", "b", "c", "d", "e"), true);
		final SubsetValue subset = new SubsetValue(innerSet);

		try {
			subset.numberOfKElements(-1);
		} catch (IllegalArgumentException e) {
			return;
		}
		fail();
	}
	
	@Test
	public void testNumKSubsetKGTN() {
		final SetEnumValue innerSet = new SetEnumValue(getValue("a", "b", "c", "d", "e"), true);
		final SubsetValue subset = new SubsetValue(innerSet);

		try {
			subset.numberOfKElements(innerSet.size() + 1);
		} catch (IllegalArgumentException e) {
			return;
		}
		fail();
	}
	
	@Test
	public void testNumKSubsetUpTo62() {
		for (int i = 1; i < 62; i++) {
			final SubsetValue subset = new SubsetValue(new IntervalValue(1, i));
			long sum = 0L;
			for (int j = 0; j <= i; j++) {
				sum += subset.numberOfKElements(j);
			}
			assertEquals(1L << i, sum);
		}
	}
	
	@Test
	public void testNumKSubsetPreventsOverflow() {
		final IntervalValue innerSet = new IntervalValue(1, 63);
		final SubsetValue subset = new SubsetValue(innerSet);
		for (int i = 0; i <= innerSet.size(); i++) {
			try {
				subset.numberOfKElements(i);
			} catch (IllegalArgumentException e) {
				continue;
			}
			fail();
		}
	}
}