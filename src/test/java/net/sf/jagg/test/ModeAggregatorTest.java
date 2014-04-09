package net.sf.jagg.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import static org.junit.Assert.*;
import net.sf.jagg.AggregateValue;
import net.sf.jagg.Aggregations;
import net.sf.jagg.Aggregator;
import net.sf.jagg.ModeAggregator;
import net.sf.jagg.test.model.Record;

/**
 * Tests the <code>ModeAggregator</code>.
 * 
 * @author Randy Gettman
 * @since 0.6.0
 */
public class ModeAggregatorTest {
	/**
	 * Test the collection functionality.
	 */
	@Test
	public void testByProperty() {
		List<String> records = Arrays.asList("", "aggregator", "mode", "test",
				"mode", "list", "mode", "test", "test", "mode", "aggregator");

		List<String> properties = new ArrayList<String>(0);
		Aggregator agg = new ModeAggregator(".");
		List<Aggregator> aggs = Arrays.asList(agg);

		List<AggregateValue<String>> recordAggValues = Aggregations.groupBy(
				records, properties, aggs);

		assertEquals(1, recordAggValues.size());
		assertEquals("mode", recordAggValues.get(0).getAggregateValue(0));
	}

	/**
	 * Aggregate the input integer objects
	 */
	@Test
	public void test_integer_input() {
		List<Integer> records = Arrays.asList(5, 4, 5, 5, 5, 5, 6, 6, 6, 2, 1,
				1, 6, 6);

		List<String> properties = new ArrayList<String>(0);
		Aggregator agg = new ModeAggregator(".");
		List<Aggregator> aggs = Arrays.asList(agg);

		List<AggregateValue<Integer>> recordAggValues = Aggregations.groupBy(
				records, properties, aggs);

		assertEquals(1, recordAggValues.size());
		assertEquals(5, recordAggValues.get(0).getAggregateValue(0));
	}

	/**
	 * test when objects are given as input
	 */
	@Test
	public void test_complex_input() {
		List<Record> records = TestUtility.getTestData();

		List<String> properties = Arrays.asList("category1");
		Aggregator agg = new ModeAggregator("category4");
		List<Aggregator> aggs = Arrays.asList(agg);

		List<AggregateValue<Record>> recordAggValues = Aggregations.groupBy(
				records, properties, aggs);

		assertEquals(3, recordAggValues.size());

		List<String> category1 = Arrays.asList("AAA", "BBB", "CCC");
		List<String> aggregated_mode_values = Arrays.asList("baseball",
				"basketball", "football");

		for (int i = 0; i < recordAggValues.size(); i++) {
			AggregateValue<Record> aggValue = recordAggValues.get(i);
			assertEquals(category1.get(i), aggValue.getPropertyValue(0));
			assertEquals(aggregated_mode_values.get(i),
					aggValue.getAggregateValue(0));
		}
	}

	/**
	 * test when input is double
	 */
	@Test
	public void test_double_input() {
		List<Double> records = Arrays.asList(5.0, 4.0, 5.0, 5.0, 5.0, 5.0, 6.0,
				6.0, 6.0, 2.0, 1.0, 1.0, 6.0, 6.0);

		List<String> properties = new ArrayList<String>(0);
		Aggregator agg = new ModeAggregator(".");
		List<Aggregator> aggs = Arrays.asList(agg);

		List<AggregateValue<Double>> recordAggValues = Aggregations.groupBy(
				records, properties, aggs);

		assertEquals(1, recordAggValues.size());
		assertEquals(5.0, recordAggValues.get(0).getAggregateValue(0));
	}

	/**
	 * test when there is a tie in frequencies of input values
	 */
	@Test
	public void testMultipleModes() {
		List<String> records = Arrays.asList("", "mode", "mode", "aggregator",
				"mode", "list", "mode", "aggregator", "", "aggregator",
				"aggregator");

		List<String> properties = new ArrayList<String>(0);
		Aggregator agg = new ModeAggregator(".");
		List<Aggregator> aggs = Arrays.asList(agg);

		List<AggregateValue<String>> recordAggValues = Aggregations.groupBy(
				records, properties, aggs);

		assertEquals(1, recordAggValues.size());
		assertEquals("aggregator", recordAggValues.get(0).getAggregateValue(0));
	}

	/**
	 * Tests when no items are iterated.
	 */
	@Test
	public void testNulls() {
		List<String> records = new ArrayList<String>(3);
		records.add(null);
		records.add(null);
		records.add(null);

		List<String> properties = new ArrayList<String>(0);
		Aggregator agg = new ModeAggregator(".");
		List<Aggregator> aggs = Arrays.asList(agg);

		List<AggregateValue<String>> recordAggValues = Aggregations.groupBy(
				records, properties, aggs);

		assertEquals(1, recordAggValues.size());
		assertNull(recordAggValues.get(0).getAggregateValue(0));
	}

	/**
	 * Tests the merge functionality.
	 */
	@Test
	public void testMerge() {
		ModeAggregator agg1 = new ModeAggregator(".");
		ModeAggregator agg2 = new ModeAggregator(".");
		agg1.init();
		agg2.init();

		// 1: 1, 2, 3, 2, 1, 2 (2 1s, 3 2s, 1 3) (2)
		// 2: 1, 4, 3, 4, 1, 4 (2 1s, 1 3, 3 4s) (4)
		// m: (4 1s, 3 2s, 2 3s, 3 4s) (1)
		agg1.iterate("1");
		agg2.iterate("2");
		agg1.iterate("4");
		agg2.iterate("3");
		agg1.iterate("3");
		agg2.iterate("1");
		agg1.iterate("4");
		agg2.iterate("2");
		agg1.iterate("1");
		agg2.iterate("1");
		agg1.iterate("2");
		agg2.iterate("4");

		agg1.merge(agg2);

		Comparable value = agg1.terminate();
		assertTrue(value instanceof String);
		assertEquals("1", value);
	}

	/**
	 * Test the replicate functionality
	 */
	@Test
	public void testReplicate() {
		List<String> records = Arrays.asList("", "aggregator", "mode", "test",
				"mode", "list", "mode", "test", "test", "mode", "aggregator");

		List<String> properties = new ArrayList<String>(0);
		Aggregator agg = new ModeAggregator(".");
		Aggregator replicatedAggregator = agg.replicate();
		List<Aggregator> aggs = Arrays.asList(replicatedAggregator);

		List<AggregateValue<String>> recordAggValues = Aggregations.groupBy(
				records, properties, aggs);

		assertEquals(1, recordAggValues.size());
		assertEquals("mode", recordAggValues.get(0).getAggregateValue(0));
	}

	/**
	 * Test by giving a specific degree of parallelism
	 */

	@Test
	public void testParallelism() {
		List<String> records = Arrays.asList("", "aggregator", "mode", "test",
				"mode", "list", "mode", "test", "test", "mode", "aggregator");

		List<String> properties = new ArrayList<String>(0);
		Aggregator agg = new ModeAggregator(".");
		List<Aggregator> aggs = Arrays.asList(agg);
		int parallelism = 2;

		List<AggregateValue<String>> recordAggValues = Aggregations.groupBy(
				records, properties, aggs, parallelism);

		assertEquals(1, recordAggValues.size());
		assertEquals("mode", recordAggValues.get(0).getAggregateValue(0));
	}

	/**
	 * Test using the Multiset Discriminator
	 */

	@Test
	public void testMultiSetDiscriminator() {
		List<Record> records = TestUtility.getTestData();

		List<String> properties = Arrays.asList("category1");
		Aggregator agg = new ModeAggregator("category4");
		List<Aggregator> aggs = Arrays.asList(agg);
		boolean msd = true;

		List<AggregateValue<Record>> recordAggValues = Aggregations.groupBy(
				records, properties, aggs, msd);

		assertEquals(3, recordAggValues.size());

		List<String> category1 = Arrays.asList("AAA", "BBB", "CCC");
		List<String> aggregated_mode_values = Arrays.asList("baseball",
				"basketball", "football");

		for (int i = 0; i < recordAggValues.size(); i++) {
			AggregateValue<Record> aggValue = recordAggValues.get(i);

			String categoryValue = (String) aggValue.getPropertyValue(0);
			String aggregateValue = (String) aggValue.getAggregateValue(0);

			if (categoryValue.equals(category1.get(i))
					&& aggregateValue.equals(aggregated_mode_values.get(i))) {
				assert (true);
			} else {
				assert (false);
			}
		}
	}
}
