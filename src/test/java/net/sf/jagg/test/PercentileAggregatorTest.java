package net.sf.jagg.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import static org.junit.Assert.*;
import net.sf.jagg.AggregateValue;
import net.sf.jagg.Aggregations;
import net.sf.jagg.Aggregator;
import net.sf.jagg.PercentileAggregator;
import net.sf.jagg.test.model.Record;

/**
 * Tests the <code>PercentileAggregator</code>.
 * 
 * @author Randy Gettman
 * @since 0.3.0
 */
public class PercentileAggregatorTest {
	/**
	 * Test the percentile functionality.
	 */
	@Test
	public void testByProperty() {
		List<Record> records = TestUtility.getTestData();

		List<String> properties = Arrays.asList("category1", "category2");
		Aggregator[] aggsArray = new Aggregator[] {
				new PercentileAggregator(0.0, "value1"),
				new PercentileAggregator(0.1, "value1"),
				new PercentileAggregator(0.2, "value1"),
				new PercentileAggregator(0.3, "value1"),
				new PercentileAggregator(0.4, "value1"),
				new PercentileAggregator(0.5, "value1"),
				new PercentileAggregator(0.6, "value1"),
				new PercentileAggregator(0.7, "value1"),
				new PercentileAggregator(0.8, "value1"),
				new PercentileAggregator(0.9, "value1"),
				new PercentileAggregator(1.0, "value1"),
				new PercentileAggregator(0.0, "value2"),
				new PercentileAggregator(1.0 / 7, "value2"),
				new PercentileAggregator(2.0 / 7, "value2"),
				new PercentileAggregator(3.0 / 7, "value2"),
				new PercentileAggregator(4.0 / 7, "value2"),
				new PercentileAggregator(5.0 / 7, "value2"),
				new PercentileAggregator(6.0 / 7, "value2"),
				new PercentileAggregator(1.0, "value2") };
		List<Aggregator> aggs = Arrays.asList(aggsArray);

		List<AggregateValue<Record>> recordAggValues = Aggregations.groupBy(
				records, properties, aggs);

		assertEquals(7, recordAggValues.size());

		List<String> cats1 = Arrays.asList("AAA", "AAA", "BBB", "BBB", "CCC",
				"CCC", "CCC");
		List<Integer> cats2 = Arrays.asList(1, 2, 1, 2, 1, 2, 3);
		double[][] values = new double[][] {
				new double[] { 7.0, 7.9, 8.8, 9.7, 10.2, 10.5, 10.8, 12.2,
						15.8, 19.4, 23.0, 1.338,
						1.92954857142857142857142857143,
						2.52109714285714285714285714286,
						2.83922571428571428571428571429,
						3.02064428571428571428571428571,
						3.62136285714285714285714285714,
						5.06068142857142857142857142857, 6.5 },
				new double[] { 1.0, 1.6, 2.2, 2.8, 67.0, 163.0, 259.0, 361.7,
						477.8, 593.9, 710.0, 2.25,
						30.71571428571428571428571428571,
						59.18142857142857142857142857143,
						75.37857142857142857142857142857,
						85.44142857142857142857142857143,
						107.77142857142857142857142857143,
						154.63571428571428571428571428571, 201.5 },
				new double[] { 68.0, 77.3, 86.6, 95.9, 279.4, 550.0, 820.6,
						1012.0, 1045.0, 1078.0, 1111.0, 6.25,
						8.82142857142857142857142857143,
						11.39285714285714285714285714286,
						14.53571428571428571428571428571,
						17.96428571428571428571428571429,
						21.67857142857142857142857142857,
						25.96428571428571428571428571429, 30.25 },
				new double[] { 5.0, 25.0, 45.0, 60.0, 70.0, 80.0, 210.0, 340.0,
						525.0, 765.0, 1005.0, 42.25, 50.25,
						58.53571428571428571428571428571,
						67.67857142857142857142857142857,
						77.39285714285714285714285714286,
						87.67857142857142857142857142857,
						98.82142857142857142857142857143, 110.25 },
				new double[] { 1.0, 1.3, 1.6, 1.9, 2.2, 2.5, 2.8, 3.1, 3.4,
						3.7, 4.0, 2.0, 3.28571428571428571428571428571,
						4.57142857142857142857142857143,
						5.57142857142857142857142857143,
						6.42857142857142857142857142857,
						7.42857142857142857142857142857,
						8.71428571428571428571428571429, 10.0 },
				new double[] { 1.0, 1.5, 2.0, 2.5, 3.0, 3.5, 4.0, 4.5, 5.0,
						5.5, 6.0, 1.0, 2.42857142857142857142857142857,
						4.71428571428571428571428571429,
						7.42857142857142857142857142857,
						9.57142857142857142857142857143,
						10.57142857142857142857142857143,
						14.42857142857142857142857142857, 23.0 },
				new double[] { 1.0, 1.4, 1.8, 2.2, 2.6, 3.0, 3.4, 3.8, 4.2,
						4.6, 5.0, 2.0, 2.57142857142857142857142857143,
						3.14285714285714285714285714286,
						3.71428571428571428571428571429,
						4.57142857142857142857142857143,
						5.71428571428571428571428571429,
						26.57142857142857142857142857143, 54.0 } };

		for (int i = 0; i < recordAggValues.size(); i++) {
			AggregateValue<Record> aggValue = recordAggValues.get(i);
			assertEquals(cats1.get(i), aggValue.getPropertyValue(0));
			assertEquals(cats2.get(i), aggValue.getPropertyValue(1));
			for (int j = 0; j < aggs.size(); j++) {
				assertEquals(values[i][j],
						((Number) aggValue.getAggregateValue(j)).doubleValue(),
						Math.abs(TestUtility.DELTA * values[i][j]));
			}
		}
	}

	/**
	 * Ensure an <code>IllegalArgumentException</code> is thrown given a
	 * percentile greater than one.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testPercentileTooHigh() {
		new PercentileAggregator(1.01, "value");
	}

	/**
	 * Ensure an <code>IllegalArgumentException</code> is thrown given a
	 * percentile less than zero.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testPercentileTooLow() {
		new PercentileAggregator(-0.01, "value");
	}

	/**
	 * Ensure an <code>IllegalArgumentException</code> is thrown given a
	 * <code>null</code> percentile.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testNullPercentile() {
		new PercentileAggregator(",value");
	}

	/**
	 * test when input is null
	 */
	@Test
	public void test_null_input() {
		List<Integer> records = Arrays.asList();

		List<String> properties = new ArrayList<String>(0);
		Aggregator agg = new PercentileAggregator("0.4,.");
		List<Aggregator> aggs = Arrays.asList(agg);

		List<AggregateValue<Integer>> recordAggValues = Aggregations.groupBy(
				records, properties, aggs);

		assertEquals(1, recordAggValues.size());
		assertEquals(Double.NaN, ((Number) recordAggValues.get(0)
				.getAggregateValue(0)).doubleValue(),
				Math.abs(TestUtility.DELTA));
	}

	/**
	 * Aggregate the input integer objects
	 */
	@Test
	public void test_integer_input() {
		List<Integer> records = Arrays.asList(10, 14, 5, 70);

		List<String> properties = new ArrayList<String>(0);
		Aggregator agg = new PercentileAggregator("0.4,.");
		List<Aggregator> aggs = Arrays.asList(agg);

		List<AggregateValue<Integer>> recordAggValues = Aggregations.groupBy(
				records, properties, aggs);

		double expected = 10.8;

		assertEquals(1, recordAggValues.size());
		assertEquals(expected, ((Number) recordAggValues.get(0)
				.getAggregateValue(0)).doubleValue(),
				Math.abs(TestUtility.DELTA * expected));
	}

	/**
	 * Tests the merge functionality.
	 */
	@Test
	public void testMerge() {
		PercentileAggregator agg1 = new PercentileAggregator("0.4,.");
		PercentileAggregator agg2 = new PercentileAggregator("0.4,.");
		agg1.init();
		agg2.init();

		agg1.iterate(10);
		agg2.iterate(14);
		agg1.iterate(5);
		agg2.iterate(70);

		agg1.merge(agg2);

		double fortieth = 10.8;
		assertEquals(fortieth, agg1.terminate(),
				Math.abs(TestUtility.DELTA * fortieth));
	}
}