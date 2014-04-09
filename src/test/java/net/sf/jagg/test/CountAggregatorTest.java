package net.sf.jagg.test;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import static org.junit.Assert.*;
import net.sf.jagg.AggregateValue;
import net.sf.jagg.Aggregations;
import net.sf.jagg.Aggregator;
import net.sf.jagg.CountAggregator;
import net.sf.jagg.test.model.Record;

/**
 * Tests the <code>CountAggregator</code>.
 * 
 * @author Randy Gettman
 * @since 0.3.0
 */
public class CountAggregatorTest {
	/**
	 * Test the counting functionality.
	 */
	@Test
	public void testByProperty() {
		List<Record> records = TestUtility.getTestData();

		List<String> properties = Arrays.asList("category1", "category2");

		Aggregator agg = new CountAggregator("value1");
		List<Aggregator> aggs = Arrays.asList(agg);

		List<AggregateValue<Record>> recordAggValues = Aggregations.groupBy(
				records, properties, aggs);

		assertEquals(7, recordAggValues.size());

		List<String> cats1 = Arrays.asList("AAA", "AAA", "BBB", "BBB", "CCC",
				"CCC", "CCC");
		List<Integer> cats2 = Arrays.asList(1, 2, 1, 2, 1, 2, 3);
		List<Integer> values = Arrays.asList(4, 4, 4, 5, 4, 6, 5);

		for (int i = 0; i < recordAggValues.size(); i++) {
			AggregateValue<Record> aggValue = recordAggValues.get(i);
			assertEquals(cats1.get(i), aggValue.getPropertyValue(0));
			assertEquals(cats2.get(i), aggValue.getPropertyValue(1));
			assertEquals(values.get(i).intValue(),
					((Number) aggValue.getAggregateValue(0)).intValue());
		}
	}

	// Group by category 4 and count the records
	@Test
	public void testByCategory4() {
		List<Record> records = TestUtility.getTestData();

		List<String> properties = Arrays.asList("category4");
		Aggregator agg = new CountAggregator("value1");
		List<Aggregator> aggs = Arrays.asList(agg);

		List<AggregateValue<Record>> recordAggValues = Aggregations.groupBy(
				records, properties, aggs);

		assertEquals(3, recordAggValues.size());

		List<String> category = Arrays.asList("baseball", "basketball",
				"football");
		List<Integer> aggregatedCount = Arrays.asList(8, 9, 15);

		for (int i = 0; i < recordAggValues.size(); i++) {
			AggregateValue<Record> aggValue = recordAggValues.get(i);
			assertEquals(category.get(i), aggValue.getPropertyValue(0));
			assertEquals(aggregatedCount.get(i).intValue(),
					((Number) aggValue.getAggregateValue(0)).intValue());
		}
	}

	/**
	 * Test the "*" pseudo-property to count everything, disregarding nulls, and
	 * count only when a property is not null.
	 */
	@Test
	public void testSomeNull() {
		List<Record> records = TestUtility.getSomeNullData();

		List<String> properties = Arrays.asList("category1");

		Aggregator aggStar = new CountAggregator("*");
		Aggregator aggCat3 = new CountAggregator("category3");
		List<Aggregator> aggs = Arrays.asList(aggStar, aggCat3);

		List<AggregateValue<Record>> recordAggValues = Aggregations.groupBy(
				records, properties, aggs);

		assertEquals(3, recordAggValues.size());

		List<String> cats3 = Arrays.asList("abcd", "efgh", "wxyz");
		List<Integer> valuesStar = Arrays.asList(2, 2, 2);
		List<Integer> valuesCat3 = Arrays.asList(2, 0, 1);

		for (int i = 0; i < recordAggValues.size(); i++) {
			AggregateValue<Record> aggValue = recordAggValues.get(i);
			assertEquals(cats3.get(i), aggValue.getPropertyValue(0));
			assertEquals(valuesStar.get(i).intValue(),
					((Number) aggValue.getAggregateValue(0)).intValue());
			assertEquals(valuesCat3.get(i).intValue(),
					((Number) aggValue.getAggregateValue(1)).intValue());
		}
	}

	/**
	 * Tests the merge functionality.
	 */
	@Test
	public void testMerge() {
		CountAggregator agg1 = new CountAggregator(".");
		CountAggregator agg2 = new CountAggregator(".");
		agg1.init();
		agg2.init();

		agg1.iterate(5);
		agg1.iterate(18);
		agg1.iterate(100000000);

		agg2.iterate(-323);
		agg2.iterate(12345);

		agg1.merge(agg2);

		long result = agg1.terminate();
		assertEquals(5, result);
	}

	// Test parallelism
	@Test
	public void testParallelism() {
		List<Record> records = TestUtility.getTestData();

		List<String> properties = Arrays.asList("category1", "category2");
		Aggregator agg = new CountAggregator("value1");
		List<Aggregator> aggs = Arrays.asList(agg);

		int parallelism = 2;
		List<AggregateValue<Record>> recordAggValues = Aggregations.groupBy(
				records, properties, aggs, parallelism);

		assertEquals(7, recordAggValues.size());

		List<String> cats1 = Arrays.asList("AAA", "AAA", "BBB", "BBB", "CCC",
				"CCC", "CCC");
		List<Integer> cats2 = Arrays.asList(1, 2, 1, 2, 1, 2, 3);
		List<Integer> values = Arrays.asList(4, 4, 4, 5, 4, 6, 5);

		for (int i = 0; i < recordAggValues.size(); i++) {
			AggregateValue<Record> aggValue = recordAggValues.get(i);
			assertEquals(cats1.get(i), aggValue.getPropertyValue(0));
			assertEquals(cats2.get(i), aggValue.getPropertyValue(1));
			assertEquals(values.get(i).intValue(),
					((Number) aggValue.getAggregateValue(0)).intValue());
		}
	}

	// Test Multi set Discriminator
	@Test
	public void testMSD() {
		List<Record> records = TestUtility.getTestData();

		List<String> properties = Arrays.asList("category1", "category2");
		Aggregator agg = new CountAggregator("value1");
		List<Aggregator> aggs = Arrays.asList(agg);

		boolean msd = true;
		int parallelism = 2;
		List<AggregateValue<Record>> recordAggValues = Aggregations.groupBy(
				records, properties, aggs, parallelism, msd);

		assertEquals(7, recordAggValues.size());

		List<String> cats1 = Arrays.asList("AAA", "AAA", "BBB", "BBB", "CCC",
				"CCC", "CCC");
		List<Integer> cats2 = Arrays.asList(1, 2, 1, 2, 1, 2, 3);
		List<Integer> values = Arrays.asList(4, 4, 4, 5, 4, 6, 5);

		for (int i = 0; i < recordAggValues.size(); i++) {
			AggregateValue<Record> aggValue = recordAggValues.get(i);
			assertEquals(cats1.get(i), aggValue.getPropertyValue(0));
			assertEquals(cats2.get(i), aggValue.getPropertyValue(1));
			assertEquals(values.get(i).intValue(),
					((Number) aggValue.getAggregateValue(0)).intValue());
		}
	}
	
	
	/**
	 * Test the replicate functionality
	 */
	@Test
	public void testReplicate() {
		List<Record> records = TestUtility.getTestData();

		List<String> properties = Arrays.asList("category4");
		Aggregator agg = new CountAggregator("value1");
		Aggregator aggregatorReplica = agg.replicate();
		List<Aggregator> aggs = Arrays.asList(aggregatorReplica);

		List<AggregateValue<Record>> recordAggValues = Aggregations.groupBy(
				records, properties, aggs);

		assertEquals(3, recordAggValues.size());

		List<String> category = Arrays.asList("baseball", "basketball",
				"football");
		List<Integer> aggregatedCount = Arrays.asList(8, 9, 15);

		for (int i = 0; i < recordAggValues.size(); i++) {
			AggregateValue<Record> aggValue = recordAggValues.get(i);
			assertEquals(category.get(i), aggValue.getPropertyValue(0));
			assertEquals(aggregatedCount.get(i).intValue(),
					((Number) aggValue.getAggregateValue(0)).intValue());
		}
	}
}
