package net.sf.jagg.test;

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
 * Tests the "cube" functionality, which creates grouping sets based on "cube"
 * functionality for super-aggregation.
 *
 * @author Randy Gettman
 * @since 0.7.0
 */
public class CubeTest
{
   /**
    * Tests a cube, with multiple categories in the cube and out.
    */
   @Test
   public void testCube()
   {
      List<Record> records = TestUtility.getTestData();
      List<String> properties = Arrays.asList("category1", "category2", "category3", "category4");  // {0, 1, 2, 3}

      Aggregator agg1 = new SumAggregator("value1");
      Aggregator agg2 = new AvgAggregator("value2");
      List<Aggregator> aggs = Arrays.asList(agg1, agg2);

      List<Integer> cube = Arrays.asList(2, 3);

      Aggregation aggregation = new Aggregation.Builder().setProperties(properties)
         .setAggregators(aggs).setCube(cube).build();

      List<AggregateValue<Record>> aggValues = aggregation.groupBy(records);

      // 19 values for {0, 1, 2, 3}, 11 values for {0, 1, 2}, 11 values for {0, 1, 3},
      // 7 values for {0, 1}.
      assertEquals(48, aggValues.size());

      List<String> cats1 = Arrays.asList(
         // grouping set: {0, 1, 2, 3}
         "AAA", "AAA", "AAA", "AAA", "AAA", "AAA", "AAA", "AAA", "BBB", "BBB", "BBB", "BBB", "BBB", "BBB", "BBB", "BBB", "CCC", "CCC", "CCC",
         // grouping set: {0, 1, 3}
         "AAA", "AAA", "AAA", "AAA", "BBB", "BBB", "BBB", "BBB", "CCC", "CCC", "CCC",
         // grouping set: {0, 1, 2}
         "AAA", "AAA", "AAA", "AAA", "BBB", "BBB", "BBB", "BBB", "CCC", "CCC", "CCC",
         // grouping set: {0, 1}
         "AAA", "AAA", "BBB", "BBB", "CCC", "CCC", "CCC"
      );
      List<Integer> cats2 = Arrays.asList(
         // grouping set: {0, 1, 2, 3}
         1, 1, 1, 1, 2, 2, 2, 2, 1, 1, 1, 1, 2, 2, 2, 2, 1, 2, 3,
         // grouping set: {0, 1, 3}
         1, 1, 2, 2, 1, 1, 2, 2, 1, 2, 3,
         // grouping set: {0, 1, 2}
         1, 1, 2, 2, 1, 1, 2, 2, 1, 2, 3,
         // grouping set: {0, 1}
         1, 2, 1, 2, 1, 2, 3
      );
      List<String> cats3 = Arrays.asList(
         // grouping set: {0, 1, 2, 3}
         "yyy", "yyy", "zzz", "zzz", "yyy", "yyy", "zzz", "zzz", "yyy", "yyy", "zzz", "zzz", "yyy", "yyy", "zzz", "zzz", "abcd", "efgh", "ijk",
         // grouping set: {0, 1, 3}
         null, null, null, null, null, null, null, null, null, null, null,
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
         // grouping set: {0, 1, 3}
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
         // grouping set: {0, 1, 3}
         33.0, 18.0, 324.0, 713.0, 1210.0, 1069.0, 460.0, 1090.0, 10.0, 21.0, 15.0,
         // grouping set: {0, 1, 2}
         30.0, 21.0, 1033.0, 4.0, 167.0, 2112.0, 1065.0, 485.0, 10.0, 21.0, 15.0,
         // grouping set: {0, 1}
         51.0, 1037.0, 2279.0, 1550.0, 10.0, 21.0, 15.0
      );
      List<Double> values2 = Arrays.asList(
         // grouping set: {0, 1, 2, 3}
         3.14159, 2.71828, 6.5, 1.338, 92.15, 2.25, 201.5, 68.67, 20.25, 30.25, 6.25, 12.25, 72.25, 100.25, 42.25, 56.25, 6.0, 9.1666666666666667, 13.8,
         // grouping set: {0, 1, 3}
         4.820795, 2.02814, 146.825, 35.46, 13.25, 21.25, 57.25, 85.5833333333333333, 6.0, 9.1666666666666667, 13.8,
         // grouping set: {0, 1, 2}
         2.929935, 3.919, 47.2, 135.085, 25.25, 9.25, 90.9166666666666667, 49.25, 6.0, 9.1666666666666667, 13.8,
         // grouping set: {0, 1}
         3.4244675, 91.1425, 17.25, 74.25, 6.0, 9.1666666666666667, 13.8
      );

      for (int i = 0; i < aggValues.size(); i++)
      {
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
    * cube property reference integer appears more than once in a cube
    * specification.
    */
   @Test(expected=IllegalArgumentException.class)
   public void testPropDuplication()
   {
      List<String> properties = Arrays.asList("category1", "category2", "category3", "category4");  // {0, 1, 2, 3}

      Aggregator agg1 = new SumAggregator("value1");
      Aggregator agg2 = new AvgAggregator("value2");
      List<Aggregator> aggs = Arrays.asList(agg1, agg2);

      List<Integer> cube = Arrays.asList(1, 1);

      new Aggregation.Builder().setProperties(properties)
         .setAggregators(aggs).setCube(cube).build();
   }

   /**
    * Ensure that an <code>IllegalArgumentException</code> is thrown when a
    * cube property reference integer is negative.
    */
   @Test(expected=IllegalArgumentException.class)
   public void testOutOfRangeLess()
   {
      List<String> properties = Arrays.asList("category1", "category2", "category3", "category4");  // {0, 1, 2, 3}

      Aggregator agg1 = new SumAggregator("value1");
      Aggregator agg2 = new AvgAggregator("value2");
      List<Aggregator> aggs = Arrays.asList(agg1, agg2);

      List<Integer> cube = Arrays.asList(-1);

      new Aggregation.Builder().setProperties(properties)
         .setAggregators(aggs).setCube(cube).build();
   }

   /**
    * Ensure that an <code>IllegalArgumentException</code> is thrown when a
    * cube property reference integer is too large.
    */
   @Test(expected=IllegalArgumentException.class)
   public void testOutOfRangeGreater()
   {
      List<String> properties = Arrays.asList("category1", "category2", "category3", "category4");  // {0, 1, 2, 3}

      Aggregator agg1 = new SumAggregator("value1");
      Aggregator agg2 = new AvgAggregator("value2");
      List<Aggregator> aggs = Arrays.asList(agg1, agg2);

      List<Integer> cube = Arrays.asList(4);

      new Aggregation.Builder().setProperties(properties)
         .setAggregators(aggs).setCube(cube).build();
   }
}
