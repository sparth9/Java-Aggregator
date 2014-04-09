package acceptancetest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import static org.junit.Assert.*;
import net.sf.jagg.AggregateValue;
import net.sf.jagg.Aggregations;
import net.sf.jagg.Aggregator;
import net.sf.jagg.ProductAggregator;
import net.sf.jagg.test.model.Record;

/**
 * Tests the <code>ProductAggregator</code>.
 * 
 * @author Randy Gettman
 * @since 0.3.0
 */
public class ProductAggregatorTest {
	/**
	 * Test the product functionality.
	 */
	@Test
	public void testByProperty() {
		List<Record> records = TestUtility.getTestData();

		List<String> properties = Arrays.asList("category1", "category2");
		Aggregator agg1 = new ProductAggregator("value1");
		Aggregator agg2 = new ProductAggregator("value2");
		List<Aggregator> aggs = Arrays.asList(agg1, agg2);

		List<AggregateValue<Record>> recordAggValues = Aggregations.groupBy(
				records, properties, aggs);

		assertEquals(7, recordAggValues.size());

		List<String> cats1 = Arrays.asList("AAA", "AAA", "BBB", "BBB", "CCC",
				"CCC", "CCC");
		List<Integer> cats2 = Arrays.asList(1, 2, 1, 2, 1, 2, 3);
		List<Double> values1 = Arrays.asList(17710.0, 687990.0, 7486731252.0,
				8954550000.0, 24.0, 720.0, 120.0);
		List<Double> values2 = Arrays.asList(74.2699558434444, 2868930.0241875,
				46899.31640625, 1708491805.8837890625, 700.0, 53130.0, 7776.0);

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
	 * Tests the merge functionality.
	 */
	@Test
	public void testMerge() {
		ProductAggregator agg1 = new ProductAggregator(".");
		ProductAggregator agg2 = new ProductAggregator(".");
		agg1.init();
		agg2.init();

		agg1.iterate(2);
		agg2.iterate(3);
		agg1.iterate(11);
		agg2.iterate(5);

		agg1.merge(agg2);
		double product = 330;
		assertEquals(product, agg1.terminate(),
				Math.abs(TestUtility.DELTA * product));
	}

	/**
	 * Aggregate the input integer objects
	 */
	@Test
	public void test_integer_input() {
		List<Integer> records = Arrays.asList(5, 4, 5);

		List<String> properties = new ArrayList<String>(0);
		Aggregator agg = new ProductAggregator(".");
		List<Aggregator> aggs = Arrays.asList(agg);

		List<AggregateValue<Integer>> recordAggValues = Aggregations.groupBy(
				records, properties, aggs);

		assertEquals(1, recordAggValues.size());
		assertEquals(100.0, recordAggValues.get(0).getAggregateValue(0));
	}


	 /**
	 * Test the replicate functionality
	 */
	@Test
	public void testReplicate() {
		List<Integer> records = Arrays.asList(5, 4, 5);

		List<String> properties = new ArrayList<String>(0);
		Aggregator agg = new ProductAggregator(".");
		Aggregator aggregatorReplica = agg.replicate();
		List<Aggregator> aggs = Arrays.asList(aggregatorReplica);

		List<AggregateValue<Integer>> recordAggValues = Aggregations.groupBy(
				records, properties, aggs);

		assertEquals(1, recordAggValues.size());
		assertEquals(100.0, recordAggValues.get(0).getAggregateValue(0));
	}


	 /**
	 * Test when input is empty
	 */
	@Test
	public void test_empty_input() {
		List<Integer> records = Arrays.asList();

		List<String> properties = new ArrayList<String>(0);
		Aggregator agg = new ProductAggregator(".");
		List<Aggregator> aggs = Arrays.asList(agg);

		List<AggregateValue<Integer>> recordAggValues = Aggregations.groupBy(
				records, properties, aggs);

		assertEquals(1, recordAggValues.size());
		assertEquals(1.0, recordAggValues.get(0).getAggregateValue(0));
	}

}