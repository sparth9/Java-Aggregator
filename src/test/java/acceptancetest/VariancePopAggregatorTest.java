package acceptancetest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import static org.junit.Assert.*;
import net.sf.jagg.AggregateValue;
import net.sf.jagg.Aggregations;
import net.sf.jagg.Aggregator;
import net.sf.jagg.VariancePopAggregator;
import static net.sf.jagg.test.TestUtility.*;
import net.sf.jagg.test.model.Record;

/**
 * Tests the <code>VariancePopAggregator</code>.
 * 
 * @author Randy Gettman
 * @since 0.3.0
 */
public class VariancePopAggregatorTest {
	/**
	 * Test the population variance functionality.
	 */
	@Test
	public void testByProperty() {
		List<Record> records = getTestData();

		List<String> properties = Arrays.asList("category1", "category2");
		Aggregator agg1 = new VariancePopAggregator("value1");
		Aggregator agg2 = new VariancePopAggregator("value2");
		List<Aggregator> aggs = Arrays.asList(agg1, agg2);

		List<AggregateValue<Record>> recordAggValues = Aggregations.groupBy(
				records, properties, aggs);

		assertEquals(7, recordAggValues.size());

		List<String> cats1 = Arrays.asList("AAA", "AAA", "BBB", "BBB", "CCC",
				"CCC", "CCC");
		List<Integer> cats2 = Arrays.asList(1, 2, 1, 2, 1, 2, 3);
		List<Double> values1 = Arrays.asList(37.1875, 84899.1875, 238071.6875,
				140600.0, 1.25, 2.91666666666666666666666666667, 2.0);
		List<Double> values2 = Arrays.asList(3.59774181306875, 5146.67066875,
				81.0, 580.8, 8.5, 50.80555555555555555555555555556, 405.76);

		for (int i = 0; i < recordAggValues.size(); i++) {
			AggregateValue<Record> aggValue = recordAggValues.get(i);
			assertEquals(cats1.get(i), aggValue.getPropertyValue(0));
			assertEquals(cats2.get(i), aggValue.getPropertyValue(1));
			assertEquals(values1.get(i),
					((Number) aggValue.getAggregateValue(agg1)).doubleValue(),
					Math.abs(DELTA * values1.get(i)));
			assertEquals(values2.get(i),
					((Number) aggValue.getAggregateValue(agg2)).doubleValue(),
					Math.abs(DELTA * values2.get(i)));
		}
	}

	/**
	 * Tests the merge functionality.
	 */
	@Test
	public void testMerge() {
		Aggregator agg1 = new VariancePopAggregator(".");
		Aggregator agg2 = new VariancePopAggregator(".");
		agg1.init();
		agg2.init();

		agg1.iterate(2);
		agg1.iterate(8);
		agg1.iterate(4);
		agg2.iterate(10);

		agg1.merge(agg2);

		Object temp_result = agg1.terminate();
		Double result = null;
		if (temp_result instanceof Double) {
			result = (Double) temp_result;
		}

		double variance = 40.0 / 4.0;
		assertEquals(variance, result, Math.abs(DELTA * variance));
	}

	/**
	 * Aggregate the input integer objects
	 */

	@Test
	public void test_integer_input() {
		List<Integer> records = Arrays.asList(2, 8, 4, 10);
		Aggregator agg1 = new VariancePopAggregator(".");
		List<String> properties = new ArrayList<String>(0);
		List<Aggregator> aggs = Arrays.asList(agg1);

		List<AggregateValue<Integer>> recordAggValues = Aggregations.groupBy(
				records, properties, aggs);

		double variance = 40.0 / 4.0;

		assertEquals(1, recordAggValues.size());
		assertEquals(variance, recordAggValues.get(0).getAggregateValue(0));
	}

	/**
	 * Test when input is empty
	 */
	@Test
	public void test_empty_input() {
		List<Integer> records = Arrays.asList();
		Aggregator agg1 = new VariancePopAggregator(".");
		List<String> properties = new ArrayList<String>(0);
		List<Aggregator> aggs = Arrays.asList(agg1);

		List<AggregateValue<Integer>> recordAggValues = Aggregations.groupBy(
				records, properties, aggs);
		assertEquals(1, recordAggValues.size());
		assertEquals(Double.NaN, recordAggValues.get(0).getAggregateValue(0));
	}

	/**
	 * Test when there is single value in the input
	 */
	@Test
	public void test_single_input() {
		List<Integer> records = Arrays.asList(9);
		Aggregator agg1 = new VariancePopAggregator(".");
		List<String> properties = new ArrayList<String>(0);
		List<Aggregator> aggs = Arrays.asList(agg1);

		List<AggregateValue<Integer>> recordAggValues = Aggregations.groupBy(
				records, properties, aggs);
		assertEquals(1, recordAggValues.size());
		assertEquals(0.0, recordAggValues.get(0).getAggregateValue(0));
	}

	/**
	 * test the replica functionality
	 */
	@Test
	public void testReplica() {
		List<Integer> records = Arrays.asList(9);
		List<String> properties = new ArrayList<String>(0);

		Aggregator agg1 = new VariancePopAggregator(".");
		Aggregator replicaAggregator = agg1.replicate();
		List<Aggregator> aggs = Arrays.asList(replicaAggregator);

		List<AggregateValue<Integer>> recordAggValues = Aggregations.groupBy(
				records, properties, aggs);
		assertEquals(1, recordAggValues.size());
		assertEquals(0.0, recordAggValues.get(0).getAggregateValue(0));
	}
}