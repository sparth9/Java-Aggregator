package net.sf.jagg.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import static org.junit.Assert.*;
import net.sf.jagg.AggregateValue;
import net.sf.jagg.Aggregations;
import net.sf.jagg.Aggregator;
import net.sf.jagg.StdDevAggregator;
import net.sf.jagg.test.model.Record;

/**
 * Tests the <code>StdDevAggregator</code>.
 * 
 * @author Randy Gettman
 * @since 0.3.0
 */
public class StdDevAggregatorTest {
	/**
	 * Test the (sample) standard deviation functionality.
	 */
	@Test
	public void testByProperty() {
		List<Record> records = TestUtility.getTestData();

		List<String> properties = Arrays.asList("category1", "category2");
		Aggregator agg1 = new StdDevAggregator("value1");
		Aggregator agg2 = new StdDevAggregator("value2");
		List<Aggregator> aggs = Arrays.asList(agg1, agg2);

		List<AggregateValue<Record>> recordAggValues = Aggregations.groupBy(
				records, properties, aggs);

		assertEquals(7, recordAggValues.size());

		List<String> cats1 = Arrays.asList("AAA", "AAA", "BBB", "BBB", "CCC",
				"CCC", "CCC");
		List<Integer> cats2 = Arrays.asList(1, 2, 1, 2, 1, 2, 3);
		List<Double> values1 = Arrays.asList(7.04154339142586931011584876734,
				336.45046688430477888633943028251,
				563.40830368984327445275768624851,
				419.22547632509167440758705612499,
				1.29099444873580562839308846659,
				1.87082869338697069279187436619,
				1.58113883008418966599944677222);
		List<Double> values2 = Arrays.asList(2.19020297782914784043305145352,
				82.83856323194731347243298454039,
				10.39230484541326376116467804904,
				26.94438717061495908017012482177,
				3.36650164612069265112112863902,
				7.80811543630514383461873513129,
				22.52110121641479590994765556213);

		for (int i = 0; i < recordAggValues.size(); i++) {
			AggregateValue<Record> aggValue = recordAggValues.get(i);
			assertEquals(cats1.get(i), aggValue.getPropertyValue(0));
			assertEquals(cats2.get(i), aggValue.getPropertyValue(1));
			assertEquals(values1.get(i),
					((Number) aggValue.getAggregateValue(agg1)).doubleValue(),
					Math.abs(TestUtility.DELTA * values1.get(i)));
			assertEquals(values2.get(i),
					((Number) aggValue.getAggregateValue(agg2)).doubleValue(),
					Math.abs(TestUtility.DELTA * values2.get(i)));
		}
	}

	/**
	 * Tests when an iterated aggregator is merged into a non-iterated
	 * aggregator.
	 * 
	 * @since 0.7.2
	 */
	@Test
	public void testZeroMerge() {
		StdDevAggregator sda1 = new StdDevAggregator(".");
		StdDevAggregator sda2 = new StdDevAggregator(".");

		sda2.iterate(5);
		sda2.iterate(1005);

		sda1.merge(sda2);

		double stdDev = sda1.terminate();
		assertEquals(Math.sqrt(500000), stdDev, Math.ulp(Math.sqrt(500000)));
	}

	/**
	 * Aggregate the input integer objects
	 */
	@Test
	public void test_integer_input() {
		List<Integer> records = Arrays.asList(5, 1005);

		List<String> properties = new ArrayList<String>(0);
		Aggregator agg = new StdDevAggregator(".");
		List<Aggregator> aggs = Arrays.asList(agg);

		List<AggregateValue<Integer>> recordAggValues = Aggregations.groupBy(
				records, properties, aggs);

		assertEquals(1, recordAggValues.size());
		assertEquals(Math.sqrt(500000), recordAggValues.get(0)
				.getAggregateValue(0));
	}

	/**
	 * Test when input is empty
	 */
	@Test
	public void test_empty_input() {
		List<Integer> records = Arrays.asList();

		List<String> properties = new ArrayList<String>(0);
		Aggregator agg = new StdDevAggregator(".");
		List<Aggregator> aggs = Arrays.asList(agg);

		List<AggregateValue<Integer>> recordAggValues = Aggregations.groupBy(
				records, properties, aggs);

		assertEquals(1, recordAggValues.size());
		assertEquals(Double.NaN, recordAggValues.get(0).getAggregateValue(0));
	}

	/**
	 * Test when there is only one value in the input
	 */
	@Test
	public void test_single_input() {
		List<Integer> records = Arrays.asList(9);

		List<String> properties = new ArrayList<String>(0);
		Aggregator agg = new StdDevAggregator(".");
		List<Aggregator> aggs = Arrays.asList(agg);

		List<AggregateValue<Integer>> recordAggValues = Aggregations.groupBy(
				records, properties, aggs);

		assertEquals(1, recordAggValues.size());
		assertEquals(0.0, recordAggValues.get(0).getAggregateValue(0));
	}
}