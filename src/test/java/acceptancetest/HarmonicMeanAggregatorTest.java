package acceptancetest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.Assert;

import static org.junit.Assert.*;
import net.sf.jagg.AggregateValue;
import net.sf.jagg.Aggregations;
import net.sf.jagg.Aggregator;
import net.sf.jagg.HarmonicMeanAggregator;
import net.sf.jagg.test.model.Record;

/**
 * Tests the <code>HarmonicMeanAggregator</code>.
 * 
 * @author Randy Gettman
 * @since 0.3.0
 */
public class HarmonicMeanAggregatorTest {
	/**
	 * Test the harmonic mean functionality.
	 */
	@Test
	public void testByProperty() {
		List<Record> records = TestUtility.getTestData();

		List<String> properties = Arrays.asList("category1", "category2");
		Aggregator agg1 = new HarmonicMeanAggregator("value1");
		Aggregator agg2 = new HarmonicMeanAggregator("value2");
		List<Aggregator> aggs = Arrays.asList(agg1, agg2);

		List<AggregateValue<Record>> recordAggValues = Aggregations.groupBy(
				records, properties, aggs);

		assertEquals(7, recordAggValues.size());

		List<String> cats1 = Arrays.asList("AAA", "AAA", "BBB", "BBB", "CCC",
				"CCC", "CCC");
		List<Integer> cats2 = Arrays.asList(1, 2, 1, 2, 1, 2, 3);
		List<Double> values1 = Arrays.asList(10.60320311330639125879359377339,
				2.98989916548876109684828322753,
				149.77916861585051086267800612563,
				21.35419973618904960987278946545, 1.92,
				2.44897959183673469387755102041,
				2.18978102189781021897810278978);
		List<Double> values2 = Arrays.asList(2.51981177919222802206398300884,
				8.42421844520579092833765141722,
				12.34288965832034575069598365004,
				66.27970412434784527423161664637,
				4.24242424242424242424242424242,
				3.50758667737640702881727055665,
				3.94160583941605839416058394161);

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
	 * Ensures that <code>Double.NaN</code> is returned if there is a zero in
	 * the values.
	 */
	@Test
	public void testZeroValue() {
		List<Record> records = new ArrayList<Record>();
		records.add(new Record("a", 1, "a", "a", 1, 2));
		records.add(new Record("a", 1, "a", "a", 0, 2));

		List<String> properties = Arrays.asList("category1", "category2");

		Aggregator agg1 = new HarmonicMeanAggregator("value1");
		List<Aggregator> aggs = Arrays.asList(agg1);

		List<AggregateValue<Record>> recordAggValues = Aggregations.groupBy(
				records, properties, aggs);

		assertEquals(1, recordAggValues.size());

		AggregateValue<Record> aggValue = recordAggValues.get(0);
		Assert.assertEquals(Double.NaN,
				((Number) aggValue.getAggregateValue(0)).doubleValue(),
				TestUtility.DELTA);
	}

	/**
	 * Test when input is empty
	 */
	@Test
	public void test_empty_input() {
		List<Integer> records = Arrays.asList();

		List<String> properties = new ArrayList<String>(0);
		Aggregator agg = new HarmonicMeanAggregator(".");
		List<Aggregator> aggs = Arrays.asList(agg);

		List<AggregateValue<Integer>> recordAggValues = Aggregations.groupBy(
				records, properties, aggs);

		assertEquals(1, recordAggValues.size());
		assertEquals(Double.NaN, ((Number) recordAggValues.get(0)
				.getAggregateValue(0)).doubleValue(),
				Math.abs(TestUtility.DELTA));
	}

	/**
	 * test when the sum of the input is zero
	 */
	@Test
	public void input_sum_equals_zero() {
		List<Integer> records = Arrays.asList(-1, 1);

		List<String> properties = new ArrayList<String>(0);
		Aggregator agg = new HarmonicMeanAggregator(".");
		List<Aggregator> aggs = Arrays.asList(agg);

		List<AggregateValue<Integer>> recordAggValues = Aggregations.groupBy(
				records, properties, aggs);

		assertEquals(1, recordAggValues.size());
		assertEquals(Double.NaN, ((Number) recordAggValues.get(0)
				.getAggregateValue(0)).doubleValue(),
				Math.abs(TestUtility.DELTA));
	}

	/**
	 * Test when input is empty
	 */
	@Test
	public void test_integer_input() {
		List<Integer> records = Arrays.asList(5, 5, 5, 4);

		List<String> properties = new ArrayList<String>(0);
		Aggregator agg = new HarmonicMeanAggregator(".");
		List<Aggregator> aggs = Arrays.asList(agg);

		List<AggregateValue<Integer>> recordAggValues = Aggregations.groupBy(
				records, properties, aggs);

		double expected = 4.705882352941177;

		assertEquals(1, recordAggValues.size());
		assertEquals(expected, ((Number) recordAggValues.get(0)
				.getAggregateValue(0)).doubleValue(),
				Math.abs(TestUtility.DELTA * expected));
	}

	/**
	 * Test the replicate functionality
	 */

	@Test
	public void testReplicate() {
		List<Integer> records = Arrays.asList(5, 5, 5, 4);

		List<String> properties = new ArrayList<String>(0);
		Aggregator agg = new HarmonicMeanAggregator(".");
		Aggregator replicaAggregator = agg.replicate();

		List<Aggregator> aggs = Arrays.asList(replicaAggregator);

		List<AggregateValue<Integer>> recordAggValues = Aggregations.groupBy(
				records, properties, aggs);

		double expected = 4.705882352941177;

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
		HarmonicMeanAggregator agg1 = new HarmonicMeanAggregator(".");
		HarmonicMeanAggregator agg2 = new HarmonicMeanAggregator(".");
		agg1.init();
		agg2.init();

		agg1.iterate(0.5);
		agg2.iterate(1.0 / 3.0);
		agg1.iterate(0.25);
		agg2.iterate(0.2);
		agg1.merge(agg2);

		double harMean = 4.0 / 14.0;
		double result = agg1.terminate();
		assertEquals(harMean, result, Math.abs(TestUtility.DELTA * harMean));
	}
}