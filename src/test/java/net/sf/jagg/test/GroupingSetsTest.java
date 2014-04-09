package net.sf.jagg.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.*;

import net.sf.jagg.AggregateValue;
import net.sf.jagg.Aggregation;
import net.sf.jagg.Aggregator;
import net.sf.jagg.AvgAggregator;
import net.sf.jagg.SumAggregator;
import net.sf.jagg.test.model.Record;

/**
 * Tests the "grouping sets" functionality, which creates "super-aggregate"
 * data.
 *
 * @author Randy Gettman
 * @since 0.7.0
 */
public class GroupingSetsTest
{
   /**
    * Test with grouping sets that include the default grouping set, which
    * consists of all properties.
    */
   @Test
   public void testGroupingSetsWithDefault()
   {
      List<Record> records = TestUtility.getTestData();
      List<String> properties = Arrays.asList("category1", "category2");

      Aggregator agg1 = new SumAggregator("value1");
      Aggregator agg2 = new AvgAggregator("value2");
      List<Aggregator> aggs = Arrays.asList(agg1, agg2);

      List<List<Integer>> groupingSets = new ArrayList<List<Integer>>();
      groupingSets.add(Arrays.asList(0, 1));
      groupingSets.add(Arrays.asList(0));
      groupingSets.add(Arrays.asList(1));

      Aggregation aggregation = new Aggregation.Builder().setProperties(properties)
         .setAggregators(aggs).setGroupingSets(groupingSets).build();

      List<AggregateValue<Record>> aggValues = aggregation.groupBy(records);

      // 7 values for cat1 and cat2, 3 values for cat1 only, 3 values for cat2 only.
      assertEquals(13, aggValues.size());

      List<String> cats1 = Arrays.asList(
         // grouping set: {0, 1}
         "AAA", "BBB", "CCC", "AAA", "BBB", "CCC", "CCC",
         // grouping set: {0}
         "AAA", "BBB", "CCC",
         // grouping set: {1}
         null, null, null);
      List<Integer> cats2 = Arrays.asList(
         // grouping set: {0, 1}
         1, 1, 1, 2, 2, 2, 3,
         // grouping set: {0}
         null, null, null,
         // grouping set: {1}
         1, 2, 3);
      List<Double> values1 = Arrays.asList(
         // grouping set: {0, 1}
         51.0, 2279.0, 10.0, 1037.0, 1550.0, 21.0, 15.0,
         // grouping set: {0}
         1088.0, 3829.0, 46.0,
         // grouping set: {1}
         2340.0, 2608.0, 15.0);
      List<Double> values2 = Arrays.asList(
         // grouping set: {0, 1}
         3.4244675, 17.25, 6.0, 91.1425, 74.25, 9.1666666666666667, 13.8,
         // grouping set: {0}
         47.28348375, 48.9166666666666667, 9.8666666666666667,
         // grouping set: {1}
         8.89148916666666667, 52.721333333333333333, 13.8
         );

      for (int i = 0; i < aggValues.size(); i++)
      {
         AggregateValue<Record> aggValue = aggValues.get(i);
         assertEquals(cats1.get(i), aggValue.getPropertyValue(0));
         assertEquals(cats2.get(i), aggValue.getPropertyValue(1));
         assertEquals(values1.get(i), ((Number) aggValue.getAggregateValue(agg1)).doubleValue(), Math.abs(TestUtility.DELTA * values1.get(i)));
         assertEquals(values2.get(i), ((Number) aggValue.getAggregateValue(agg2)).doubleValue(), Math.abs(TestUtility.DELTA * values2.get(i)));
      }
   }

   /**
    * Test with grouping sets that do not include the default grouping set.
    * This one does include a grouping set that does not rely on the original
    * aggregate values.
    */
   @Test
   public void testGroupingSetsWithoutDefault()
   {
      List<Record> records = TestUtility.getTestData();
      List<String> properties = Arrays.asList("category1", "category2");

      Aggregator agg1 = new SumAggregator("value1");
      Aggregator agg2 = new AvgAggregator("value2");
      List<Aggregator> aggs = Arrays.asList(agg1, agg2);

      List<List<Integer>> groupingSets = new ArrayList<List<Integer>>();
      groupingSets.add(Arrays.<Integer>asList());
      groupingSets.add(Arrays.asList(0));
      groupingSets.add(Arrays.asList(1));

      Aggregation aggregation = new Aggregation.Builder().setProperties(properties)
         .setAggregators(aggs).setGroupingSets(groupingSets).build();

      List<AggregateValue<Record>> aggValues = aggregation.groupBy(records);

      // 3 values for cat1 only, 3 values for cat2 only, 1 value for neither.
      assertEquals(7, aggValues.size());

      List<String> cats1 = Arrays.asList(
         // grouping set: {0}
         "AAA", "BBB", "CCC",
         // grouping set: {1}
         null, null, null,
         // grouping set: {}
         null);
      List<Integer> cats2 = Arrays.asList(
         // grouping set: {0}
         null, null, null,
         // grouping set: {1}
         1, 2, 3,
         // grouping set: {}
         null);
      List<Double> values1 = Arrays.asList(
         // grouping set: {0}
         1088.0, 3829.0, 46.0,
         // grouping set: {1}
         2340.0, 2608.0, 15.0,
         // grouping set: {}
         4963.0);
      List<Double> values2 = Arrays.asList(
         // grouping set: {0}
         47.28348375, 48.9166666666666667, 9.8666666666666667,
         // grouping set: {1}
         8.89148916666666667, 52.721333333333333333, 13.8,
         // grouping set: {}
         30.2036834375);

      for (int i = 0; i < aggValues.size(); i++)
      {
         AggregateValue<Record> aggValue = aggValues.get(i);
         assertEquals(cats1.get(i), aggValue.getPropertyValue(0));
         assertEquals(cats2.get(i), aggValue.getPropertyValue(1));
         assertEquals(values1.get(i), ((Number) aggValue.getAggregateValue(agg1)).doubleValue(), Math.abs(TestUtility.DELTA * values1.get(i)));
         assertEquals(values2.get(i), ((Number) aggValue.getAggregateValue(agg2)).doubleValue(), Math.abs(TestUtility.DELTA * values2.get(i)));
      }
   }

   /**
    * Ensure that an <code>IllegalArgumentException</code> is thrown when a
    * grouping set is duplicated, even if a different order.
    */
   @Test(expected=IllegalArgumentException.class)
   public void testGroupingSetsDuplicate()
   {
      List<String> properties = Arrays.asList("category1", "category2");

      Aggregator agg1 = new SumAggregator("value1");
      Aggregator agg2 = new AvgAggregator("value2");
      List<Aggregator> aggs = Arrays.asList(agg1, agg2);

      List<List<Integer>> groupingSets = new ArrayList<List<Integer>>();
      groupingSets.add(Arrays.asList(0, 1));
      groupingSets.add(Arrays.asList(1, 0));

      new Aggregation.Builder().setProperties(properties)
         .setAggregators(aggs).setGroupingSets(groupingSets).build();
   }

   /**
    * Ensure that an <code>IllegalArgumentException</code> is thrown when a
    * grouping set index is negative.
    */
   @Test(expected=IllegalArgumentException.class)
   public void testGroupingSetsLess()
   {
      List<String> properties = Arrays.asList("category1", "category2");

      Aggregator agg1 = new SumAggregator("value1");
      Aggregator agg2 = new AvgAggregator("value2");
      List<Aggregator> aggs = Arrays.asList(agg1, agg2);

      List<List<Integer>> groupingSets = new ArrayList<List<Integer>>();
      groupingSets.add(Arrays.asList(0, 1));
      groupingSets.add(Arrays.asList(-1));

      new Aggregation.Builder().setProperties(properties)
         .setAggregators(aggs).setGroupingSets(groupingSets).build();
   }

   /**
    * Ensure that an <code>IllegalArgumentException</code> is thrown when a
    * grouping set index is too large.
    */
   @Test(expected=IllegalArgumentException.class)
   public void testGroupingSetsGreater()
   {
      List<String> properties = Arrays.asList("category1", "category2");

      Aggregator agg1 = new SumAggregator("value1");
      Aggregator agg2 = new AvgAggregator("value2");
      List<Aggregator> aggs = Arrays.asList(agg1, agg2);

      List<List<Integer>> groupingSets = new ArrayList<List<Integer>>();
      groupingSets.add(Arrays.asList(0, 1));
      groupingSets.add(Arrays.asList(2));

      new Aggregation.Builder().setProperties(properties)
         .setAggregators(aggs).setGroupingSets(groupingSets).build();
   }
}
