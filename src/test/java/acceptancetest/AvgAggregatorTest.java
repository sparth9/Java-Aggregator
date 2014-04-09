package acceptancetest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import static org.junit.Assert.*;
import net.sf.jagg.AggregateValue;
import net.sf.jagg.Aggregations;
import net.sf.jagg.Aggregator;
import net.sf.jagg.AvgAggregator;
import net.sf.jagg.test.model.Record;

/**
 * Tests the <code>AvgAggregator</code>.
 * 
 * @author Randy Gettman
 * @since 0.3.0
 */
public class AvgAggregatorTest {
	/**
	 * Test the average functionality.
	 */
	@Test
	public void testByProperty() {
		List<Record> records = TestUtility.getTestData();

		List<String> properties = Arrays.asList("category1", "category2");
		Aggregator agg1 = new AvgAggregator("value1");
		Aggregator agg2 = new AvgAggregator("value2");
		List<Aggregator> aggs = Arrays.asList(agg1, agg2);

		List<AggregateValue<Record>> recordAggValues = Aggregations.groupBy(
				records, properties, aggs);

		assertEquals(7, recordAggValues.size());

		List<Double> values1 = Arrays.asList(12.75, 259.25, 569.75, 310.0, 2.5,
				3.5, 3.0);
		List<Double> values2 = Arrays.asList(3.4244675, 91.1425, 17.25, 74.25,
				6.0, 9.1666666666666666666666666667, 13.8);

		for (int i = 0; i < recordAggValues.size(); i++) {
			AggregateValue<Record> aggValue = recordAggValues.get(i);
			assertEquals(values1.get(i),
					((Number) aggValue.getAggregateValue(agg1)).doubleValue(),
					Math.abs(TestUtility.DELTA * values1.get(i)));
			assertEquals(values2.get(i),
					((Number) aggValue.getAggregateValue(agg2)).doubleValue(),
					Math.abs(TestUtility.DELTA * values2.get(i)));
		}
	}

	/**
	 * Test the replicate functionality
	 */
	@Test
	public void testReplicate() {
		List<Record> records = TestUtility.getTestData();

		List<String> properties = Arrays.asList("category1", "category2");
		Aggregator agg1 = new AvgAggregator("value1");
		Aggregator agg2 = new AvgAggregator("value2");

		Aggregator aggregator1Replica = agg1.replicate();
		Aggregator aggregator2Replica = agg2.replicate();

		List<Aggregator> aggs = Arrays.asList(aggregator1Replica,
				aggregator2Replica);

		List<AggregateValue<Record>> recordAggValues = Aggregations.groupBy(
				records, properties, aggs);

		assertEquals(7, recordAggValues.size());

		List<Double> values1 = Arrays.asList(12.75, 259.25, 569.75, 310.0, 2.5,
				3.5, 3.0);
		List<Double> values2 = Arrays.asList(3.4244675, 91.1425, 17.25, 74.25,
				6.0, 9.1666666666666666666666666667, 13.8);

		for (int i = 0; i < recordAggValues.size(); i++) {
			AggregateValue<Record> aggValue = recordAggValues.get(i);
			assertEquals(values1.get(i),
					((Number) aggValue.getAggregateValue(agg1)).doubleValue(),
					Math.abs(TestUtility.DELTA * values1.get(i)));
			assertEquals(values2.get(i),
					((Number) aggValue.getAggregateValue(agg2)).doubleValue(),
					Math.abs(TestUtility.DELTA * values2.get(i)));
		}
	}

	/**
	 * Tests the merge functionality.
	 */
	@Test
	public void testMerge() {
		AvgAggregator agg1 = new AvgAggregator(".");
		AvgAggregator agg2 = new AvgAggregator(".");
		agg1.init();
		agg2.init();

		agg1.iterate(5);
		agg1.iterate(18);
		agg1.iterate(10);

		agg2.iterate(-323);
		agg2.iterate(123);

		agg1.merge(agg2);

		double result = agg1.terminate();
		double avg = (5 + 18 + 10 - 323 + 123) / 5.0;
		assertEquals(avg, result, Math.abs(TestUtility.DELTA) * avg);
	}

	/**
	 * Aggregate the input integer objects
	 */
	@Test
	public void test_integer_input() {
		List<Integer> records = Arrays.asList(2, 8, 4, 10);
		Aggregator agg1 = new AvgAggregator(".");
		List<String> properties = new ArrayList<String>(0);
		List<Aggregator> aggs = Arrays.asList(agg1);

		List<AggregateValue<Integer>> recordAggValues = Aggregations.groupBy(
				records, properties, aggs);

		double variance = 24.0 / 4.0;

		assertEquals(1, recordAggValues.size());
		assertEquals(variance, recordAggValues.get(0).getAggregateValue(0));

	}

	/**
	 * Test by giving a specific degree of parallelism
	 */
	@Test
	public void testParallelism() {
		List<Record> records = TestUtility.getTestData();

		List<String> properties = Arrays.asList("category1", "category2");
		Aggregator agg1 = new AvgAggregator("value1");
		Aggregator agg2 = new AvgAggregator("value2");
		List<Aggregator> aggs = Arrays.asList(agg1, agg2);

		int parallelism = 3;
		List<AggregateValue<Record>> recordAggValues = Aggregations.groupBy(
				records, properties, aggs, parallelism);

		assertEquals(7, recordAggValues.size());

		List<Double> values1 = Arrays.asList(12.75, 259.25, 569.75, 310.0, 2.5,
				3.5, 3.0);
		List<Double> values2 = Arrays.asList(3.4244675, 91.1425, 17.25, 74.25,
				6.0, 9.1666666666666666666666666667, 13.8);

		for (int i = 0; i < recordAggValues.size(); i++) {
			AggregateValue<Record> aggValue = recordAggValues.get(i);
			assertEquals(values1.get(i),
					((Number) aggValue.getAggregateValue(agg1)).doubleValue(),
					Math.abs(TestUtility.DELTA * values1.get(i)));
			assertEquals(values2.get(i),
					((Number) aggValue.getAggregateValue(agg2)).doubleValue(),
					Math.abs(TestUtility.DELTA * values2.get(i)));
		}
	}

	/**
	 * Test using the Multiset Discriminator
	 */
	@Test
	public void testMultiSetDiscriminator() {
		List<Record> records = TestUtility.getTestData();

		List<String> properties = Arrays.asList("category1", "category2");
		Aggregator agg1 = new AvgAggregator("value1");

		List<Aggregator> aggs = Arrays.asList(agg1);

		int parallelism = 2;
		boolean msd = true;
		List<AggregateValue<Record>> recordAggValues = Aggregations.groupBy(
				records, properties, aggs, parallelism, msd);

		assertEquals(7, recordAggValues.size());

		// Set containing elements which are the values that is expected to be
		// obtained when the
		// input record is aggregated
		List<Double> outputValues = Arrays.asList(12.75, 259.25, 569.75, 310.0,
				2.5, 3.5, 3.0);
		Set<Double> expected = new HashSet<Double>(outputValues);

		for (int i = 0; i < recordAggValues.size(); i++) {
			AggregateValue<Record> aggValue = recordAggValues.get(i);

			Number num = ((Number) aggValue.getAggregateValue(agg1))
					.doubleValue();
			if (!expected.contains(num.doubleValue())) {
				fail("the aggregation is not correct");
			}
		}
	}
}
