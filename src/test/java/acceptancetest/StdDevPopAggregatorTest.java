package acceptancetest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import static org.junit.Assert.*;
import net.sf.jagg.AggregateValue;
import net.sf.jagg.Aggregations;
import net.sf.jagg.Aggregator;
import net.sf.jagg.StdDevPopAggregator;
import net.sf.jagg.test.model.Record;

/**
 * Tests the <code>StdDevPopAggregator</code>.
 * 
 * @author Randy Gettman
 * @since 0.3.0
 */
public class StdDevPopAggregatorTest {
	/**
	 * Test the population standard deviation functionality.
	 */
	@Test
	public void testByProperty() {
		List<Record> records = TestUtility.getTestData();

		List<String> properties = Arrays.asList("category1", "category2");
		Aggregator agg1 = new StdDevPopAggregator("value1");
		Aggregator agg2 = new StdDevPopAggregator("value2");
		List<Aggregator> aggs = Arrays.asList(agg1, agg2);

		List<AggregateValue<Record>> recordAggValues = Aggregations.groupBy(
				records, properties, aggs);

		assertEquals(7, recordAggValues.size());

		List<String> cats1 = Arrays.asList("AAA", "AAA", "BBB", "BBB", "CCC",
				"CCC", "CCC");
		List<Integer> cats2 = Arrays.asList(1, 2, 1, 2, 1, 2, 3);
		List<Double> values1 = Arrays.asList(6.09815545882523398301655668232,
				291.37465143694294945561489307632,
				487.92590369850215608669451798119,
				374.96666518505348330864986133545,
				1.11803398874989484820458683437,
				1.70782512765993306387017311342,
				1.41421356237309504880168872421);
		List<Double> values2 = Arrays.asList(1.89677141824436768372378659365,
				71.74030017186992506625096952286, 9.0,
				24.09979253022730899210667044324,
				2.91547594742265023543707643877,
				7.12780159344769890288633355678,
				20.14348529922267448919070040601);

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
	 * Aggregate the input integer objects
	 */
	@Test
	public void test_integer_input() {
		List<Integer> records = Arrays.asList(1, 2, 3, 4, 5);

		List<String> properties = new ArrayList<String>(0);
		Aggregator agg = new StdDevPopAggregator(".");
		List<Aggregator> aggs = Arrays.asList(agg);

		List<AggregateValue<Integer>> recordAggValues = Aggregations.groupBy(
				records, properties, aggs);

		double expected = 1.4142135623730951;

		assertEquals(1, recordAggValues.size());
		assertEquals(expected, recordAggValues.get(0).getAggregateValue(0));
	}

	/**
	 * Test when input is empty
	 */
	@Test
	public void test_empty_input() {
		List<Integer> records = Arrays.asList();

		List<String> properties = new ArrayList<String>(0);
		Aggregator agg = new StdDevPopAggregator(".");
		List<Aggregator> aggs = Arrays.asList(agg);

		List<AggregateValue<Integer>> recordAggValues = Aggregations.groupBy(
				records, properties, aggs);

		assertEquals(1, recordAggValues.size());
		assertEquals(Double.NaN, recordAggValues.get(0).getAggregateValue(0));
	}

	/**
	 * Test the replicate functionality
	 */
	@Test
	public void testReplicate() {
		List<Integer> records = Arrays.asList(1, 2, 3, 4, 5);

		List<String> properties = new ArrayList<String>(0);
		Aggregator agg = new StdDevPopAggregator(".");
		Aggregator replicaAggregator = agg.replicate();
		List<Aggregator> aggs = Arrays.asList(replicaAggregator);

		List<AggregateValue<Integer>> recordAggValues = Aggregations.groupBy(
				records, properties, aggs);

		double expected = 1.4142135623730951;

		assertEquals(1, recordAggValues.size());
		assertEquals(expected, recordAggValues.get(0).getAggregateValue(0));
	}
}