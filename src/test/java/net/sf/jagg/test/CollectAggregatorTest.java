package net.sf.jagg.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import static org.junit.Assert.*;
import net.sf.jagg.AggregateValue;
import net.sf.jagg.Aggregations;
import net.sf.jagg.Aggregator;
import net.sf.jagg.CollectAggregator;
import net.sf.jagg.test.model.Record;

/**
 * Tests the <code>CollectAggregator</code>.
 * 
 * @author Randy Gettman
 * @since 0.6.0
 */
public class CollectAggregatorTest {
	/**
	 * Test the collection functionality.
	 */
	@Test
	public void testByProperty() {
		List<Record> records = TestUtility.getTestData();

		List<String> properties = Arrays.asList("category1", "category2");
		Aggregator aggCat4 = new CollectAggregator("category4");
		Aggregator aggVal1 = new CollectAggregator("value1");
		Aggregator aggVal2 = new CollectAggregator("value2");
		List<Aggregator> aggs = Arrays.asList(aggCat4, aggVal1, aggVal2);

		List<AggregateValue<Record>> recordAggValues = Aggregations.groupBy(
				records, properties, aggs);

		assertEquals(7, recordAggValues.size());

		List<String> cats1 = Arrays.asList("AAA", "AAA", "BBB", "BBB", "CCC",
				"CCC", "CCC");
		List<Integer> cats2 = Arrays.asList(1, 2, 1, 2, 1, 2, 3);
		List<List<Object>> cats4 = new ArrayList<List<Object>>();
		cats4.add(Arrays.<Object> asList("baseball", "basketball", "baseball",
				"basketball"));
		cats4.add(Arrays.<Object> asList("baseball", "basketball", "baseball",
				"basketball"));
		cats4.add(Arrays.<Object> asList("baseball", "basketball", "baseball",
				"basketball"));
		cats4.add(Arrays.<Object> asList("baseball", "basketball", "baseball",
				"basketball", "basketball"));
		cats4.add(Arrays.<Object> asList("football", "football", "football",
				"football"));
		cats4.add(Arrays.<Object> asList("football", "football", "football",
				"football", "football", "football"));
		cats4.add(Arrays.<Object> asList("football", "football", "football",
				"football", "football"));
		List<List<Object>> values1 = new ArrayList<List<Object>>();
		values1.add(Arrays.<Object> asList(10, 11, 23, 7));
		values1.add(Arrays.<Object> asList(1, 3, 323, 710));
		values1.add(Arrays.<Object> asList(1111, 1001, 99, 68));
		values1.add(Arrays.<Object> asList(405, 80, 55, 5, 1005));
		values1.add(Arrays.<Object> asList(1, 2, 3, 4));
		values1.add(Arrays.<Object> asList(1, 2, 3, 4, 5, 6));
		values1.add(Arrays.<Object> asList(1, 2, 3, 4, 5));
		List<List<Object>> values2 = new ArrayList<List<Object>>();
		values2.add(Arrays.<Object> asList(6.5, 1.338, 3.14159, 2.71828));
		values2.add(Arrays.<Object> asList(201.5, 68.67, 92.15, 2.25));
		values2.add(Arrays.<Object> asList(6.25, 12.25, 20.25, 30.25));
		values2.add(Arrays.<Object> asList(42.25, 56.25, 72.25, 90.25, 110.25));
		values2.add(Arrays.<Object> asList(2.0, 5.0, 7.0, 10.0));
		values2.add(Arrays.<Object> asList(1.0, 3.0, 7.0, 10.0, 11.0, 23.0));
		values2.add(Arrays.<Object> asList(2.0, 3.0, 4.0, 6.0, 54.0));

		for (int i = 0; i < recordAggValues.size(); i++) {
			AggregateValue<Record> aggValue = recordAggValues.get(i);
			assertEquals(cats1.get(i), aggValue.getPropertyValue(0));
			assertEquals(cats2.get(i), aggValue.getPropertyValue(1));

			List av0 = (List) aggValue.getAggregateValue(0);
			assertEquals(cats4.get(i).size(), av0.size());
			for (int j = 0; j < cats4.get(i).size(); j++)
				assertEquals(cats4.get(i).get(j), av0.get(j));

			List av1 = (List) aggValue.getAggregateValue(1);
			assertEquals(values1.get(i).size(), av1.size());
			for (int j = 0; j < values1.get(i).size(); j++)
				assertEquals(values1.get(i).get(j), av1.get(j));

			List av2 = (List) aggValue.getAggregateValue(2);
			assertEquals(values2.get(i).size(), av2.size());
			for (int j = 0; j < values2.get(i).size(); j++)
				assertEquals(values2.get(i).get(j), av2.get(j));
		}
	}

	// Group by category1 and collect aggregator based on category4
	@Test
	public void groupByCategory1() {
		List<Record> records = TestUtility.getTestData();

		List<String> properties = Arrays.asList("category1");
		Aggregator aggCat = new CollectAggregator("category4");
		List<Aggregator> aggs = Arrays.asList(aggCat);

		List<AggregateValue<Record>> recordAggValues = Aggregations.groupBy(
				records, properties, aggs);

		assertEquals(3, recordAggValues.size());

		List<String> cats1 = Arrays.asList("AAA", "BBB", "CCC");
		List<List<Object>> cats4 = new ArrayList<List<Object>>();
		cats4.add(Arrays.<Object> asList("baseball", "basketball", "baseball",
				"basketball", "baseball", "basketball", "baseball",
				"basketball"));
		cats4.add(Arrays.<Object> asList("baseball", "basketball", "baseball",
				"basketball", "baseball", "basketball", "baseball",
				"basketball", "basketball"));
		cats4.add(Arrays.<Object> asList("football", "football", "football",
				"football", "football", "football", "football", "football",
				"football", "football", "football", "football", "football",
				"football", "football"));

		for (int i = 0; i < recordAggValues.size(); i++) {
			AggregateValue<Record> aggValue = recordAggValues.get(i);
			assertEquals(cats1.get(i), aggValue.getPropertyValue(0));

			List av0 = (List) aggValue.getAggregateValue(0);

			assertEquals(cats4.get(i).size(), av0.size());
			for (int j = 0; j < cats4.get(i).size(); j++)
				assertEquals(cats4.get(i).get(j), av0.get(j));
		}
	}

	/**
	 * Test the replicate functionality
	 */
	@Test
	public void testReplicate() {
		List<Record> records = TestUtility.getTestData();

		List<String> properties = Arrays.asList("category1");
		Aggregator aggCat = new CollectAggregator("category4");
		Aggregator replicaAggregator = aggCat.replicate();
		List<Aggregator> aggs = Arrays.asList(replicaAggregator);

		List<AggregateValue<Record>> recordAggValues = Aggregations.groupBy(
				records, properties, aggs);

		assertEquals(3, recordAggValues.size());

		List<String> cats1 = Arrays.asList("AAA", "BBB", "CCC");
		List<List<Object>> cats4 = new ArrayList<List<Object>>();
		cats4.add(Arrays.<Object> asList("baseball", "basketball", "baseball",
				"basketball", "baseball", "basketball", "baseball",
				"basketball"));
		cats4.add(Arrays.<Object> asList("baseball", "basketball", "baseball",
				"basketball", "baseball", "basketball", "baseball",
				"basketball", "basketball"));
		cats4.add(Arrays.<Object> asList("football", "football", "football",
				"football", "football", "football", "football", "football",
				"football", "football", "football", "football", "football",
				"football", "football"));

		for (int i = 0; i < recordAggValues.size(); i++) {
			AggregateValue<Record> aggValue = recordAggValues.get(i);
			assertEquals(cats1.get(i), aggValue.getPropertyValue(0));

			List av0 = (List) aggValue.getAggregateValue(0);

			assertEquals(cats4.get(i).size(), av0.size());
			for (int j = 0; j < cats4.get(i).size(); j++)
				assertEquals(cats4.get(i).get(j), av0.get(j));
		}
	}

	/**
	 * Aggregate the input integer objects
	 */
	@Test
	public void test_integer_input() {
		List<Integer> records = Arrays.asList(5, 4, 5);

		List<String> properties = new ArrayList<String>(0);
		Aggregator agg = new CollectAggregator(".");
		List<Aggregator> aggs = Arrays.asList(agg);

		List<AggregateValue<Integer>> recordAggValues = Aggregations.groupBy(
				records, properties, aggs);

		List<Integer> expected = Arrays.asList(5, 4, 5);

		for (int i = 0; i < recordAggValues.size(); i++) {
			AggregateValue<Integer> aggValue = recordAggValues.get(i);
			List av0 = (List) aggValue.getAggregateValue(0);

			for (int j = 0; j < expected.size(); j++)
				assertEquals(expected.get(j), av0.get(j));
		}
	}

	/**
	 * Test when input is empty
	 */
	@Test
	public void test_empty_input() {
		List<Integer> records = Arrays.asList();

		List<String> properties = new ArrayList<String>(0);
		Aggregator agg = new CollectAggregator(".");
		List<Aggregator> aggs = Arrays.asList(agg);

		List<AggregateValue<Integer>> recordAggValues = Aggregations.groupBy(
				records, properties, aggs);

		List<Integer> expected = Arrays.asList();

		assertEquals(1, recordAggValues.size());
		List av0 = (List) recordAggValues.get(0).getAggregateValue(0);

		if (!(av0.size() == 0 && av0.size() == expected.size())) {
			assert (false);
		}
	}

	/**
	 * Tests the merge functionality.
	 */
	@Test
	public void testMerge() {
		CollectAggregator agg1 = new CollectAggregator(".");
		CollectAggregator agg2 = new CollectAggregator(".");
		agg1.init();
		agg2.init();

		agg1.iterate("1");
		agg2.iterate("2");
		agg1.iterate(1);
		agg2.iterate(2);
		agg1.iterate(1.0);
		agg2.iterate(2.0);
		agg1.iterate(agg1);
		agg2.iterate(agg2);

		List<Object> expected = Arrays.asList("1", 1, 1.0, agg1, "2", 2, 2.0,
				agg2);

		agg1.merge(agg2);

		List<Object> values = agg1.terminate();
		assertEquals(8, values.size());
		for (int i = 0; i < 8; i++) {
			assertEquals(expected.get(i), values.get(i));
		}
	}

	/**
	 * Test by giving a specific degree of parallelism
	 */
	@Test
	public void testByParallelism() {
		List<Record> records = TestUtility.getTestData();

		List<String> properties = Arrays.asList("category1", "category2");
		Aggregator aggCat4 = new CollectAggregator("category4");
		Aggregator aggVal1 = new CollectAggregator("value1");
		Aggregator aggVal2 = new CollectAggregator("value2");
		List<Aggregator> aggs = Arrays.asList(aggCat4, aggVal1, aggVal2);

		int parallelism = 2;

		List<AggregateValue<Record>> recordAggValues = Aggregations.groupBy(
				records, properties, aggs, parallelism);

		assertEquals(7, recordAggValues.size());

		List<String> cats1 = Arrays.asList("AAA", "AAA", "BBB", "BBB", "CCC",
				"CCC", "CCC");
		List<Integer> cats2 = Arrays.asList(1, 2, 1, 2, 1, 2, 3);
		List<List<Object>> cats4 = new ArrayList<List<Object>>();
		cats4.add(Arrays.<Object> asList("baseball", "basketball", "baseball",
				"basketball"));
		cats4.add(Arrays.<Object> asList("baseball", "basketball", "baseball",
				"basketball"));
		cats4.add(Arrays.<Object> asList("baseball", "basketball", "baseball",
				"basketball"));
		cats4.add(Arrays.<Object> asList("baseball", "basketball", "baseball",
				"basketball", "basketball"));
		cats4.add(Arrays.<Object> asList("football", "football", "football",
				"football"));
		cats4.add(Arrays.<Object> asList("football", "football", "football",
				"football", "football", "football"));
		cats4.add(Arrays.<Object> asList("football", "football", "football",
				"football", "football"));
		List<List<Object>> values1 = new ArrayList<List<Object>>();
		values1.add(Arrays.<Object> asList(10, 11, 23, 7));
		values1.add(Arrays.<Object> asList(1, 3, 323, 710));
		values1.add(Arrays.<Object> asList(1111, 1001, 99, 68));
		values1.add(Arrays.<Object> asList(405, 80, 55, 5, 1005));
		values1.add(Arrays.<Object> asList(1, 2, 3, 4));
		values1.add(Arrays.<Object> asList(1, 2, 3, 4, 5, 6));
		values1.add(Arrays.<Object> asList(1, 2, 3, 4, 5));
		List<List<Object>> values2 = new ArrayList<List<Object>>();
		values2.add(Arrays.<Object> asList(6.5, 1.338, 3.14159, 2.71828));
		values2.add(Arrays.<Object> asList(201.5, 68.67, 92.15, 2.25));
		values2.add(Arrays.<Object> asList(6.25, 12.25, 20.25, 30.25));
		values2.add(Arrays.<Object> asList(42.25, 56.25, 72.25, 90.25, 110.25));
		values2.add(Arrays.<Object> asList(2.0, 5.0, 7.0, 10.0));
		values2.add(Arrays.<Object> asList(1.0, 3.0, 7.0, 10.0, 11.0, 23.0));
		values2.add(Arrays.<Object> asList(2.0, 3.0, 4.0, 6.0, 54.0));

		for (int i = 0; i < recordAggValues.size(); i++) {
			AggregateValue<Record> aggValue = recordAggValues.get(i);
			assertEquals(cats1.get(i), aggValue.getPropertyValue(0));
			assertEquals(cats2.get(i), aggValue.getPropertyValue(1));

			List av0 = (List) aggValue.getAggregateValue(0);
			assertEquals(cats4.get(i).size(), av0.size());
			for (int j = 0; j < cats4.get(i).size(); j++)
				assertEquals(cats4.get(i).get(j), av0.get(j));

			List av1 = (List) aggValue.getAggregateValue(1);
			assertEquals(values1.get(i).size(), av1.size());
			for (int j = 0; j < values1.get(i).size(); j++)
				assertEquals(values1.get(i).get(j), av1.get(j));

			List av2 = (List) aggValue.getAggregateValue(2);
			assertEquals(values2.get(i).size(), av2.size());
			for (int j = 0; j < values2.get(i).size(); j++)
				assertEquals(values2.get(i).get(j), av2.get(j));
		}
	}

}
