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
import net.sf.jagg.StdDevAggregator;
import net.sf.jagg.SumAggregator;
import net.sf.jagg.test.model.Record;

/**
 * Tests the "rollups" functionality, which creates grouping sets based on
 * "rollup" functionality for super-aggregation.
 *
 * @author Randy Gettman
 * @since 0.7.0
 */
public class RollupsTest
{
   /**
    * Tests a single rollup, with multiple categories in the rollup and out.
    */
   @Test
   public void testSingleRollup()
   {
      List<Record> records = TestUtility.getTestData();
      List<String> properties = Arrays.asList("category1", "category2", "category3", "category4");  // {0, 1, 2, 3}

      Aggregator agg1 = new SumAggregator("value1");
      Aggregator agg2 = new AvgAggregator("value2");
      List<Aggregator> aggs = Arrays.asList(agg1, agg2);

      List<Integer> rollup = Arrays.asList(2, 3);

      Aggregation aggregation = new Aggregation.Builder().setProperties(properties)
         .setAggregators(aggs).setRollup(rollup).build();

      List<AggregateValue<Record>> aggValues = aggregation.groupBy(records);

      //for (AggregateValue<Record> aggValue : aggValues)
      //   System.err.println(aggValue);

      // 19 values for all 4 categories, 11 values for cats1-3 only, 7 values for cats1-2 only.
      assertEquals(37, aggValues.size());

      List<String> cats1 = Arrays.asList(
         // grouping set: {0, 1, 2, 3}
         "AAA", "AAA", "AAA", "AAA", "AAA", "AAA", "AAA", "AAA", "BBB", "BBB", "BBB", "BBB", "BBB", "BBB", "BBB", "BBB", "CCC", "CCC", "CCC",
         // grouping set: {0, 1, 2}
         "AAA", "AAA", "AAA", "AAA", "BBB", "BBB", "BBB", "BBB", "CCC", "CCC", "CCC",
         // grouping set: {0, 1}
         "AAA", "AAA", "BBB", "BBB", "CCC", "CCC", "CCC"
      );
      List<Integer> cats2 = Arrays.asList(
         // grouping set: {0, 1, 2, 3}
         1, 1, 1, 1, 2, 2, 2, 2, 1, 1, 1, 1, 2, 2, 2, 2, 1, 2, 3,
         // grouping set: {0, 1, 2}
         1, 1, 2, 2, 1, 1, 2, 2, 1, 2, 3,
         // grouping set: {0, 1}
         1, 2, 1, 2, 1, 2, 3
      );
      List<String> cats3 = Arrays.asList(
         // grouping set: {0, 1, 2, 3}
         "yyy", "yyy", "zzz", "zzz", "yyy", "yyy", "zzz", "zzz", "yyy", "yyy", "zzz", "zzz", "yyy", "yyy", "zzz", "zzz", "abcd", "efgh", "ijk",
         // grouping set: {0, 1, 2}
         "yyy", "zzz", "yyy", "zzz", "yyy", "zzz", "yyy", "zzz", "abcd", "efgh", "ijk",
         // grouping set: {0, 1}
         null, null, null, null, null, null, null
      );
      List<String> cats4 = Arrays.asList(
         // grouping set: {0, 1, 2, 3}
         "baseball", "basketball", "baseball", "basketball", "baseball", "basketball", "baseball", "basketball",
         "baseball", "basketball", "baseball", "basketball", "baseball", "basketball", "baseball", "basketball",
         "football", "football", "football",
         // grouping set: {0, 1, 2}
         null, null, null, null, null, null, null, null, null, null, null,
         // grouping set: {0, 1}
         null, null, null, null, null, null, null
      );
      List<Double> values1 = Arrays.asList(
         // grouping set: {0, 1, 2, 3}
         23.0, 7.0, 10.0, 11.0, 323.0, 710.0, 1.0, 3.0, 99.0, 68.0, 1111.0, 1001.0, 55.0, 1010.0, 405.0, 80.0, 10.0, 21.0, 15.0,
         // grouping set: {0, 1, 2}
         30.0, 21.0, 1033.0, 4.0, 167.0, 2112.0, 1065.0, 485.0, 10.0, 21.0, 15.0,
         // grouping set: {0, 1}
         51.0, 1037.0, 2279.0, 1550.0, 10.0, 21.0, 15.0
      );
      List<Double> values2 = Arrays.asList(
         // grouping set: {0, 1, 2, 3}
         3.14159, 2.71828, 6.5, 1.338, 92.15, 2.25, 201.5, 68.67, 20.25, 30.25, 6.25, 12.25, 72.25, 100.25, 42.25, 56.25, 6.0, 9.1666666666666667, 13.8,
         // grouping set: {0, 1, 2}
         2.929935, 3.919, 47.2, 135.085, 25.25, 9.25, 90.9166666666666667, 49.25, 6.0, 9.1666666666666667, 13.8,
         // grouping set: {0, 1}
         3.4244675, 91.1425, 17.25, 74.25, 6.0, 9.1666666666666667, 13.8
      );

      for (int i = 0; i < aggValues.size(); i++)
      {
         //System.err.println("Testing i = " + i);
         AggregateValue<Record> aggValue = aggValues.get(i);
         assertEquals(cats1.get(i), aggValue.getPropertyValue(0));
         assertEquals(cats2.get(i), aggValue.getPropertyValue(1));
         assertEquals(cats3.get(i), aggValue.getPropertyValue(2));
         assertEquals(cats4.get(i), aggValue.getPropertyValue(3));
         assertEquals(values1.get(i), ((Number) aggValue.getAggregateValue(agg1)).doubleValue(), Math.abs(TestUtility.DELTA * values1.get(i)));
         assertEquals(values2.get(i), ((Number) aggValue.getAggregateValue(agg2)).doubleValue(), Math.abs(TestUtility.DELTA * values2.get(i)));
      }
   }

   /**
    * Tests multiple rollups, with a property out of the rollup.
    */
   @Test
   public void testMultipleRollup()
   {
      List<Record> records = TestUtility.getTestData();
      List<String> properties = Arrays.asList("category1", "category2", "category3", "category4");  // {0, 1, 2, 3}

      Aggregator agg1 = new SumAggregator("value1");
      Aggregator agg2 = new AvgAggregator("value2");
      List<Aggregator> aggs = Arrays.asList(agg1, agg2);

      List<List<Integer>> rollups = new ArrayList<List<Integer>>();
      rollups.add(Arrays.asList(0, 1));
      rollups.add(Arrays.asList(2));

      Aggregation aggregation = new Aggregation.Builder().setProperties(properties)
         .setAggregators(aggs).setRollups(rollups).build();

      List<AggregateValue<Record>> aggValues = aggregation.groupBy(records);

      //for (AggregateValue<Record> aggValue : aggValues)
      //   System.err.println(aggValue);

      // 19 values for {0, 1, 2, 3}, 11 values for {0, 1, 3}, 5 values for {0, 3},
      // 11 values for {0, 2, 3}, 7 values for {2, 3}, 3 values for {3}.
      assertEquals(56, aggValues.size());

      List<String> cats1 = Arrays.asList(
         // grouping set: {0, 1, 2, 3}
         "AAA", "AAA", "AAA", "AAA", "AAA", "AAA", "AAA", "AAA", "BBB", "BBB", "BBB", "BBB", "BBB", "BBB", "BBB", "BBB", "CCC", "CCC", "CCC",
         // grouping set: {0, 2, 3}
         "CCC", "CCC", "CCC", "AAA", "BBB", "AAA", "BBB", "AAA", "BBB", "AAA", "BBB",
         // grouping set: {0, 1, 3}
         "AAA", "AAA", "AAA", "AAA", "BBB", "BBB", "BBB", "BBB", "CCC", "CCC", "CCC",
         // grouping set: {2, 3}
         null, null, null, null, null, null, null,
         // grouping set: {0, 3}
         "AAA", "BBB", "AAA", "BBB", "CCC",
         // grouping set: {3}
         null, null, null
      );
      List<Integer> cats2 = Arrays.asList(
         // grouping set: {0, 1, 2, 3}
         1, 1, 1, 1, 2, 2, 2, 2, 1, 1, 1, 1, 2, 2, 2, 2, 1, 2, 3,
         // grouping set: {0, 2, 3}
         null, null, null, null, null, null, null, null, null, null, null,
         // grouping set: {0, 1, 3}
         1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 3,
         // grouping set: {2, 3}
         null, null, null, null, null, null, null,
         // grouping set: {0, 3}
         null, null, null, null, null,
         // grouping set: {3}
         null, null, null
      );
      List<String> cats3 = Arrays.asList(
         // grouping set: {0, 1, 2, 3}
         "yyy", "zzz", "yyy", "zzz", "yyy", "zzz", "yyy", "zzz", "yyy", "zzz", "yyy", "zzz", "yyy", "zzz", "yyy", "zzz", "abcd", "efgh", "ijk",
         // grouping set: {0, 2, 3}
         "abcd", "efgh", "ijk", "yyy", "yyy", "yyy", "yyy", "zzz", "zzz", "zzz", "zzz",
         // grouping set: {0, 1, 3}
         null, null, null, null, null, null, null, null, null, null, null,
         // grouping set: {2, 3}
         "abcd", "efgh", "ijk", "yyy", "yyy", "zzz", "zzz",
         // grouping set: {0, 3}
         null, null, null, null, null,
         // grouping set: {3}
         null, null, null
      );
      List<String> cats4 = Arrays.asList(
         // grouping set: {0, 1, 2, 3}
         "baseball", "baseball", "basketball", "basketball", "baseball", "baseball", "basketball", "basketball",
         "baseball", "baseball", "basketball", "basketball", "baseball", "baseball", "basketball", "basketball",
         "football", "football", "football",
         // grouping set: {0, 2, 3}
         "football", "football", "football",
         "baseball", "baseball", "basketball", "basketball", "baseball", "baseball", "basketball", "basketball",
         // grouping set: {0, 1, 3}
         "baseball", "baseball", "basketball", "basketball", "baseball", "baseball", "basketball", "basketball",
         "football", "football", "football",
         // grouping set: {2, 3}
         "football", "football", "football", "baseball", "basketball", "baseball", "basketball",
         // grouping set: {0, 3}
         "baseball", "baseball", "basketball", "basketball", "football",
         // grouping set: {3}
         "baseball", "basketball", "football"
      );
      List<Double> values1 = Arrays.asList(
         // grouping set: {0, 1, 2, 3}
         23.0, 10.0, 7.0, 11.0, 323.0, 1.0, 710.0, 3.0, 99.0, 1111.0, 68.0, 1001.0, 55.0, 405.0, 1010.0, 80.0, 10.0, 21.0, 15.0,
         // grouping set: {0, 2, 3}
         10.0, 21.0, 15.0, 346.0, 154.0, 717.0, 1078.0, 11.0, 1516.0, 14.0, 1081.0,
         // grouping set: {0, 1, 3}
         33.0, 324.0, 18.0, 713.0, 1210.0, 460.0, 1069.0, 1090.0, 10.0, 21.0, 15.0,
         // grouping set: {2, 3}
         10.0, 21.0, 15.0, 500.0, 1795.0, 1527.0, 1095.0,
         // grouping set: {0, 3}
         357.0, 1670.0, 731.0, 2159.0, 46.0,
         // grouping set: {3}
         2027.0, 2890.0, 46.0
      );
      List<Double> values2 = Arrays.asList(
         // grouping set: {0, 1, 2, 3}
         3.14159, 6.5, 2.71828, 1.338, 92.15, 201.5, 2.25, 68.67, 20.25, 6.25, 30.25, 12.25, 72.25, 42.25, 100.25, 56.25, 6.0, 9.1666666666666667, 13.8,
         // grouping set: {0, 2, 3}
         6.0, 9.1666666666666667, 13.8, 47.645795, 46.25, 2.48414, 76.9166666666666667, 104.0, 24.25, 35.004, 34.25,
         // grouping set: {0, 1, 3}
         4.820795, 146.825, 2.02814, 35.46, 13.25, 57.25, 21.25, 85.5833333333333333, 6.0, 9.1666666666666667, 13.8,
         // grouping set: {2, 3}
         6.0, 9.1666666666666667, 13.8, 46.9478975, 47.143656, 64.125, 34.627,
         // grouping set: {0, 3}
         75.8228975, 35.25, 18.74407, 59.85, 9.8666666666666667,
         // grouping set: {3}
         55.53644875, 41.580697777777778, 9.8666666666666667
      );

      for (int i = 0; i < aggValues.size(); i++)
      {
         //System.err.println("Testing i = " + i);
         AggregateValue<Record> aggValue = aggValues.get(i);
         assertEquals(cats1.get(i), aggValue.getPropertyValue(0));
         assertEquals(cats2.get(i), aggValue.getPropertyValue(1));
         assertEquals(cats3.get(i), aggValue.getPropertyValue(2));
         assertEquals(cats4.get(i), aggValue.getPropertyValue(3));
         assertEquals(values1.get(i), ((Number) aggValue.getAggregateValue(agg1)).doubleValue(), Math.abs(TestUtility.DELTA * values1.get(i)));
         assertEquals(values2.get(i), ((Number) aggValue.getAggregateValue(agg2)).doubleValue(), Math.abs(TestUtility.DELTA * values2.get(i)));
      }
   }

   /**
    * Ensure that an <code>IllegalArgumentException</code> is thrown when a
    * rollup property reference integer appears more than once in a rollup
    * specification.
    */
   @Test(expected=IllegalArgumentException.class)
   public void testPropDuplication()
   {
      List<String> properties = Arrays.asList("category1", "category2", "category3", "category4");  // {0, 1, 2, 3}

      Aggregator agg1 = new SumAggregator("value1");
      Aggregator agg2 = new AvgAggregator("value2");
      List<Aggregator> aggs = Arrays.asList(agg1, agg2);

      List<List<Integer>> rollups = new ArrayList<List<Integer>>();
      rollups.add(Arrays.asList(1, 1));

      new Aggregation.Builder().setProperties(properties)
         .setAggregators(aggs).setRollups(rollups).build();
   }

   /**
    * Ensure that an <code>IllegalArgumentException</code> is thrown when a
    * rollup property reference integer appears more than once overall.
    */
   @Test(expected=IllegalArgumentException.class)
   public void testAppearsTwice()
   {
      List<String> properties = Arrays.asList("category1", "category2", "category3", "category4");  // {0, 1, 2, 3}

      Aggregator agg1 = new SumAggregator("value1");
      Aggregator agg2 = new AvgAggregator("value2");
      List<Aggregator> aggs = Arrays.asList(agg1, agg2);

      List<List<Integer>> rollups = new ArrayList<List<Integer>>();
      rollups.add(Arrays.asList(0, 1));
      rollups.add(Arrays.asList(1));

      new Aggregation.Builder().setProperties(properties)
         .setAggregators(aggs).setRollups(rollups).build();
   }

   /**
    * Ensure that an <code>IllegalArgumentException</code> is thrown when a
    * rollup property reference integer is negative.
    */
   @Test(expected=IllegalArgumentException.class)
   public void testOutOfRangeLess()
   {
      List<String> properties = Arrays.asList("category1", "category2", "category3", "category4");  // {0, 1, 2, 3}

      Aggregator agg1 = new SumAggregator("value1");
      Aggregator agg2 = new AvgAggregator("value2");
      List<Aggregator> aggs = Arrays.asList(agg1, agg2);

      List<List<Integer>> rollups = new ArrayList<List<Integer>>();
      rollups.add(Arrays.asList(-1));

      new Aggregation.Builder().setProperties(properties)
         .setAggregators(aggs).setRollups(rollups).build();
   }

   /**
    * Ensure that an <code>IllegalArgumentException</code> is thrown when a
    * rollup property reference integer is too large.
    */
   @Test(expected=IllegalArgumentException.class)
   public void testOutOfRangeGreater()
   {
      List<String> properties = Arrays.asList("category1", "category2", "category3", "category4");  // {0, 1, 2, 3}

      Aggregator agg1 = new SumAggregator("value1");
      Aggregator agg2 = new AvgAggregator("value2");
      List<Aggregator> aggs = Arrays.asList(agg1, agg2);

      List<List<Integer>> rollups = new ArrayList<List<Integer>>();
      rollups.add(Arrays.asList(4));

      new Aggregation.Builder().setProperties(properties)
         .setAggregators(aggs).setRollups(rollups).build();
   }

   /**
    * Test standard deviation rollups.
    * @since 0.7.2
    */
   @Test
   public void testStdDevRollups()
   {
      List<Record> records = TestUtility.getTestData();
      List<String> properties = Arrays.asList("category1");

      Aggregator agg1 = new StdDevAggregator("value1");
      List<Aggregator> aggs = Arrays.asList(agg1);
      List<List<Integer>> rollups = new ArrayList<List<Integer>>();
      rollups.add(Arrays.asList(0));

      Aggregation agg = new Aggregation.Builder().setProperties(properties)
         .setAggregators(aggs).setRollups(rollups).build();

      List<AggregateValue<Record>> aggValues = agg.groupBy(records);

      assertEquals(4, aggValues.size());

      List<Double> expectedValues = Arrays.asList(
          256.7016055378585, 475.0289757244054, 1.579632265825846, 324.9779505547633);

      for (int i = 0; i < 4; i++)
      {
         AggregateValue<Record> aggValue = aggValues.get(i);
         assertEquals(expectedValues.get(i), (Double) aggValue.getAggregateValue(0), Math.ulp(expectedValues.get(i)));
      }
   }
}
