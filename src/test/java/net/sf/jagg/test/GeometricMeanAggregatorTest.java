package net.sf.jagg.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import static org.junit.Assert.*;
import net.sf.jagg.AggregateValue;
import net.sf.jagg.Aggregations;
import net.sf.jagg.Aggregator;
import net.sf.jagg.GeometricMeanAggregator;
import net.sf.jagg.test.model.Record;

/**
 * Tests the <code>GemoetricMeanAggregator</code>.
 *
 * @author Randy Gettman
 * @since 0.3.0
 */
public class GeometricMeanAggregatorTest
{
   /**
    * Test the geometric mean functionality.
    */
   @Test
   public void testByProperty()
   {
      List<Record> records = TestUtility.getTestData();

      List<String> properties = Arrays.asList("category1", "category2");
      Aggregator agg1 = new GeometricMeanAggregator("value1");
      Aggregator agg2 = new GeometricMeanAggregator("value2");
      List<Aggregator> aggs = Arrays.asList(agg1, agg2);

      List<AggregateValue<Record>> recordAggValues = Aggregations.groupBy(records,
         properties, aggs);

      assertEquals(7, recordAggValues.size());

      List<String> cats1 = Arrays.asList("AAA", "AAA", "BBB", "BBB", "CCC", "CCC", "CCC");
      List<Integer> cats2 = Arrays.asList(1, 2, 1, 2, 1, 2, 3);
      List<Double> values1 = Arrays.asList( 11.53598387443243469279073714816,
                                            28.80020184088867366921435089786,
                                           294.15285025551352265865021885517,
                                            97.81574187821186180011707469216,
                                             2.21336383940064318481758054688,
                                             2.99379516552390895491016056788,
                                             2.60517108469735189232576692392);
      List<Double> values2 = Arrays.asList(  2.93564334473654956332456139138,
                                            41.15569962708831053069515369530,
                                            14.71606265276143760139450284189,
                                            70.22998570911722742445695238656,
                                             5.14368672361040140212710692185,
                                             6.13135745462921634087769283723,
                                             6.0);

      for (int i = 0; i < recordAggValues.size(); i++)
      {
         AggregateValue<Record> aggValue = recordAggValues.get(i);
         assertEquals(cats1.get(i), aggValue.getPropertyValue(0));
         assertEquals(cats2.get(i), aggValue.getPropertyValue(1));
         assertEquals(values1.get(i), ((Number) aggValue.getAggregateValue(agg1)).doubleValue(), Math.abs(TestUtility.DELTA * values1.get(i)));
         assertEquals(values2.get(i), ((Number) aggValue.getAggregateValue(agg2)).doubleValue(), Math.abs(TestUtility.DELTA * values2.get(i)));
      }
   }
   

	 /**
	 * Test the replicate functionality
	 */
	@Test
	public void testReplicate() {
		List<Integer> records = Arrays.asList(5,5,5);

		List<String> properties = new ArrayList<String>(0);
		Aggregator agg = new GeometricMeanAggregator(".");
		Aggregator replicaAggregator = agg.replicate();
		
		List<Aggregator> aggs = Arrays.asList(replicaAggregator);

		List<AggregateValue<Integer>> recordAggValues = Aggregations.groupBy(
				records, properties, aggs);

		assertEquals(1, recordAggValues.size());
		assertEquals(5.0, recordAggValues.get(0).getAggregateValue(0));
	}
   
   
	/**
	 * Aggregate the input integer objects
	 */

	@Test
	public void test_integer_input() {
		List<Integer> records = Arrays.asList(5,5,5);

		List<String> properties = new ArrayList<String>(0);
		Aggregator agg = new GeometricMeanAggregator(".");
		List<Aggregator> aggs = Arrays.asList(agg);

		List<AggregateValue<Integer>> recordAggValues = Aggregations.groupBy(
				records, properties, aggs);

		assertEquals(1, recordAggValues.size());
		assertEquals(5.0, recordAggValues.get(0).getAggregateValue(0));
	}
	

	 /**
	 * Test when input is empty
	 */
	@Test
	public void test_empty_input() {
		List<Integer> records = Arrays.asList();

		List<String> properties = new ArrayList<String>(0);
		Aggregator agg = new GeometricMeanAggregator(".");
		List<Aggregator> aggs = Arrays.asList(agg);

		List<AggregateValue<Integer>> recordAggValues = Aggregations.groupBy(
				records, properties, aggs);

		assertEquals(1, recordAggValues.size());
		assertEquals(Double.NaN, recordAggValues.get(0).getAggregateValue(0));
	}
   

   /**
    * Tests the merge functionality.
    */
   @Test
   public void testMerge()
   {
      GeometricMeanAggregator agg1 = new GeometricMeanAggregator(".");
      GeometricMeanAggregator agg2 = new GeometricMeanAggregator(".");
      agg1.init();
      agg2.init();

      agg1.iterate(10); // 2 * 5
      agg2.iterate(35); // 5 * 7
      agg1.iterate(70); // 7 * 10
      agg2.iterate(1280); // 2^8 * 5
      agg1.merge(agg2);

      // Fourth root of 2^10 * 5^4 * 7^2 is Math.sqrt(2^5 * 5^2 * 7) =
      // Math.sqrt(5600);
      double geoMean = Math.sqrt(5600);
      double result = agg1.terminate();
      assertEquals(geoMean, result, Math.abs(TestUtility.DELTA * geoMean));
   }
}