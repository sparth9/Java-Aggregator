package acceptancetest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.*;

import net.sf.jagg.AggregateValue;
import net.sf.jagg.Aggregations;
import net.sf.jagg.Aggregator;
import net.sf.jagg.CorrelationAggregator;
import net.sf.jagg.test.model.Record;

/**
 * Tests the <code>CorrelationAggregator</code>.
 * 
 * @author Randy Gettman
 * @since 0.3.0
 */
public class CorrelationAggregatorTest {
	/**
	 * Test the correlation functionality.
	 */
	@Test
	public void testByProperty() {
		List<Record> records = TestUtility.getTestData();

		List<String> properties = Arrays.asList("category1", "category2");
		Aggregator agg1 = new CorrelationAggregator("value1", "value2");
		List<Aggregator> aggs = Arrays.asList(agg1);

		List<AggregateValue<Record>> recordAggValues = Aggregations.groupBy(
				records, properties, aggs);

		assertEquals(7, recordAggValues.size());

		List<String> cats1 = Arrays.asList("AAA", "AAA", "BBB", "BBB", "CCC",
				"CCC", "CCC");
		List<Integer> cats2 = Arrays.asList(1, 2, 1, 2, 1, 2, 3);
		List<Double> values1 = Arrays.asList(-0.07878821931660755002010901302,
				-0.75042334968552565549220809439,
				-0.91344698255628325938251191916,
				0.48137473524148809684337584852,
				0.99705448550158156811268976761,
				0.93786498011429302643958326748,
				0.75121484155356449077936979086);

		for (int i = 0; i < recordAggValues.size(); i++) {
			AggregateValue<Record> aggValue = recordAggValues.get(i);
			assertEquals(cats1.get(i), aggValue.getPropertyValue(0));
			assertEquals(cats2.get(i), aggValue.getPropertyValue(1));
			assertEquals(values1.get(i),
					((Number) aggValue.getAggregateValue(agg1)).doubleValue(),
					Math.abs(TestUtility.DELTA * values1.get(i)));
		}
	}

	/**
	 * Ensure that if the variance of the first variable is zero, then the
	 * covariance is <code>NaN</code>.
	 */
	@Test
	public void testVarOneIsZero() {
		List<Record> records = new ArrayList<Record>();
		records.add(new Record("a", 1, "a", "a", 1, 1));
		records.add(new Record("a", 1, "a", "a", 1, 2));

		List<String> properties = Arrays.asList("category1", "category2");
		Aggregator agg1 = new CorrelationAggregator("value1", "value2");
		List<Aggregator> aggs = Arrays.asList(agg1);

		List<AggregateValue<Record>> recordAggValues = Aggregations.groupBy(
				records, properties, aggs);

		assertEquals(1, recordAggValues.size());

		AggregateValue<Record> aggValue = recordAggValues.get(0);
		assertEquals(Double.NaN,
				((Number) aggValue.getAggregateValue(0)).doubleValue(),
				TestUtility.DELTA);
	}

	/**
	 * Ensure that if the variance of the second variable is zero, then the
	 * covariance is <code>NaN</code>.
	 */
	@Test
	public void testVarTwoIsZero() {
		List<Record> records = new ArrayList<Record>();
		records.add(new Record("a", 1, "a", "a", 1, 1));
		records.add(new Record("a", 1, "a", "a", 2, 1));

		List<String> properties = Arrays.asList("category1", "category2");
		Aggregator agg1 = new CorrelationAggregator("value1", "value2");
		List<Aggregator> aggs = Arrays.asList(agg1);

		List<AggregateValue<Record>> recordAggValues = Aggregations.groupBy(
				records, properties, aggs);

		assertEquals(1, recordAggValues.size());

		AggregateValue<Record> aggValue = recordAggValues.get(0);
		assertEquals(Double.NaN,
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
		Aggregator agg = new CorrelationAggregator(".");
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
	public void testReplica() {

		List<Integer> records = Arrays.asList();

		List<String> properties = new ArrayList<String>(0);
		Aggregator agg = new CorrelationAggregator(".");
		Aggregator replicaAggregator = agg.replicate();
		List<Aggregator> aggs = Arrays.asList(replicaAggregator);

		List<AggregateValue<Integer>> recordAggValues = Aggregations.groupBy(
				records, properties, aggs);

		assertEquals(1, recordAggValues.size());
		assertEquals(Double.NaN, recordAggValues.get(0).getAggregateValue(0));
	}

}