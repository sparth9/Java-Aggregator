package net.sf.jagg.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import static org.junit.Assert.*;
import net.sf.jagg.AggregateValue;
import net.sf.jagg.Aggregations;
import net.sf.jagg.Aggregator;
import net.sf.jagg.CovarianceAggregator;
import net.sf.jagg.test.model.Record;

/**
 * Tests the <code>CovarianceAggregator</code>.
 * 
 * @author Randy Gettman
 * @since 0.3.0
 */
public class CovarianceAggregatorTest {
	/**
	 * Test the (sample) covariance functionality.
	 */
	@Test
	public void testByProperty() {
		List<Record> records = TestUtility.getTestData();

		List<String> properties = Arrays.asList("category1", "category2");
		Aggregator agg1 = new CovarianceAggregator("value1", "value2");
		List<Aggregator> aggs = Arrays.asList(agg1);

		List<AggregateValue<Record>> recordAggValues = Aggregations.groupBy(
				records, properties, aggs);

		assertEquals(7, recordAggValues.size());

		List<String> cats1 = Arrays.asList("AAA", "AAA", "BBB", "BBB", "CCC",
				"CCC", "CCC");
		List<Integer> cats2 = Arrays.asList(1, 2, 1, 2, 1, 2, 3);
		List<Double> values1 = Arrays.asList(-1.21510416666666666666666666667,
				-20915.10416666666666666666666666667,
				-5348.33333333333333333333333333333, 5437.5,
				4.33333333333333333333333333333, 13.7, 26.75);

		for (int i = 0; i < recordAggValues.size(); i++) {
			AggregateValue<Record> aggValue = recordAggValues.get(i);
			assertEquals(cats1.get(i), aggValue.getPropertyValue("category1"));
			assertEquals(cats2.get(i), aggValue.getPropertyValue("category2"));
			assertEquals(values1.get(i),
					((Number) aggValue.getAggregateValue(agg1)).doubleValue(),
					Math.abs(TestUtility.DELTA * values1.get(i)));
		}
	}

	 /**
	 * Test when input is empty
	 */
	@Test
	public void test_empty_input() {

		List<Integer> records = Arrays.asList();

		List<String> properties = new ArrayList<String>(0);
		Aggregator agg = new CovarianceAggregator(".");
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

		List<Integer> records = Arrays.asList();

		List<String> properties = new ArrayList<String>(0);
		Aggregator agg = new CovarianceAggregator(".");
		Aggregator replicaAggregator = agg.replicate();
		
		List<Aggregator> aggs = Arrays.asList(replicaAggregator);

		List<AggregateValue<Integer>> recordAggValues = Aggregations.groupBy(
				records, properties, aggs);

		assertEquals(1, recordAggValues.size());
		assertEquals(Double.NaN, recordAggValues.get(0).getAggregateValue(0));
	}
	
	//When there is exactly one pair of non-null values was found 
	@Test
	public void test_one_input_null() {
		CovarianceAggregator agg1 = new CovarianceAggregator("value1", "value2");
		CovarianceAggregator agg2 = new CovarianceAggregator("value1", "value2");
		agg1.init();
		agg2.init();

		agg1.iterate(new Record("A", 1, null, "C", 1, 1));

		agg1.merge(agg2);

		double result = agg1.terminate();
		
		if(!(result == 0.0))
			assert(false);
	}
	
	/**
	 * Tests the merge functionality.
	 */
	@Test
	public void testMerge() {
		CovarianceAggregator agg1 = new CovarianceAggregator("value1", "value2");
		CovarianceAggregator agg2 = new CovarianceAggregator("value1", "value2");
		agg1.init();
		agg2.init();

		agg1.iterate(new Record("A", 1, "B", "C", 1, 2));
		agg1.iterate(new Record("A", 1, "B", "C", 2, 4));
		agg1.iterate(new Record("A", 1, "B", "C", 4, 8));
		agg2.iterate(new Record("A", 1, "B", "C", 8, 10));

		agg1.merge(agg2);

		double result = agg1.terminate();
		double covar = 32 / 3.0;
		assertEquals(covar, result, Math.abs(TestUtility.DELTA * covar));
	}

	/**
	 * Tests when an iterated aggregator is merged into a non-iterated
	 * aggregator.
	 */
	@Test
	public void testZeroMerge() {
		CovarianceAggregator sda1 = new CovarianceAggregator("value1, value2");
		CovarianceAggregator sda2 = new CovarianceAggregator("value1, value2");

		sda2.iterate(new Record("blah", 0, "blah", "blah", 1, 5.0));
		sda2.iterate(new Record("blah", 0, "blah", "blah", 2, 1005.0));

		sda1.merge(sda2);

		double covariance = sda1.terminate();
		assertEquals(500, covariance, Math.ulp(500));
	}
}