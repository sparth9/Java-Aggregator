package net.sf.jagg.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.Assert;
import static org.junit.Assert.*;

import net.sf.jagg.AggregateValue;
import net.sf.jagg.Aggregations;
import net.sf.jagg.Aggregator;
import net.sf.jagg.AvgAggregator;
import net.sf.jagg.CountAggregator;
import net.sf.jagg.MaxAggregator;
import net.sf.jagg.MinAggregator;
import net.sf.jagg.SumAggregator;
import net.sf.jagg.test.model.CountyData;
import net.sf.jagg.test.model.Record;

/**
 * Tests functionality generic to all <code>Aggregators</code>.
 *
 * @author Randy Gettman
 * @since 0.3.0
 */
public class GenericAggregatorTest
{
   /**
    * Ensure that it can group by correctly when some category values are null.
    */
   @Test
   public void testNullCatValues()
   {
      List<Record> records = TestUtility.getSomeNullData();

      List<String> properties = Arrays.asList("category3");

      Aggregator agg1 = new AvgAggregator("value1");
      Aggregator agg2 = new AvgAggregator("value2");
      List<Aggregator> aggs = Arrays.asList(agg1, agg2);

      List<AggregateValue<Record>> recordAggValues = Aggregations.groupBy(records,
         properties, aggs);

      assertEquals(2, recordAggValues.size());

      List<Double> values1 = Arrays.asList(11.0 / 3, 10.0 / 3);
      List<Double> values2 = Arrays.asList(12.4 / 3, 11.3 / 3);

      for (int i = 0; i < recordAggValues.size(); i++)
      {
         AggregateValue<Record> aggValue = recordAggValues.get(i);
         assertEquals(values1.get(i), ((Number) aggValue.getAggregateValue(0)).doubleValue(), Math.abs(TestUtility.DELTA * values1.get(i)));
         assertEquals(values2.get(i), ((Number) aggValue.getAggregateValue(1)).doubleValue(), Math.abs(TestUtility.DELTA * values2.get(i)));
      }
   }

   /**
    * Ensure that it can treat the object itself as the value, instead of a
    * property of the object.
    */
   @Test
   public void testDotProperty()
   {
      List<Integer> records = TestUtility.getSomeNumericData();

      List<String> properties = new ArrayList<String>();

      Aggregator agg1 = new AvgAggregator(".");
      List<Aggregator> aggs = Arrays.asList(agg1);

      List<AggregateValue<Integer>> recordAggValues = Aggregations.groupBy(records,
         properties, aggs);

      assertEquals(1, recordAggValues.size());

      AggregateValue<Integer> aggValue = recordAggValues.get(0);
      assertEquals(64.0, ((Number) aggValue.getAggregateValue(0)).doubleValue(), Math.abs(TestUtility.DELTA * 64.0));
   }

   /**
    * Ensure that the overloaded <code>groupBy</code> method that does not take
    * a <code>List</code> of properties relies on the "natural ordering"
    * defined for <code>Comparable</code> objects.
    */
   @Test
   public void testComparable()
   {
      List<Record> records = TestUtility.getTestData();

      Aggregator agg1 = new SumAggregator("value1");
      Aggregator agg2 = new SumAggregator("value2");
      List<Aggregator> aggs = Arrays.asList(agg1, agg2);

      List<AggregateValue<Record>> recordAggValues = Aggregations.groupBy(records, aggs);

      assertEquals(19, recordAggValues.size());

      List<String> cats1 = Arrays.asList("AAA", "AAA", "AAA", "AAA", "AAA", "AAA", "AAA", "AAA", "BBB", "BBB", "BBB",
         "BBB", "BBB", "BBB", "BBB", "BBB", "CCC", "CCC", "CCC");
      List<Integer> cats2 = Arrays.asList(1, 1, 1, 1, 2, 2, 2, 2, 1, 1, 1, 1, 2, 2, 2, 2, 1, 2, 3);
      List<String> cats3 = Arrays.asList("yyy", "yyy", "zzz", "zzz", "yyy", "yyy", "zzz", "zzz", "yyy", "yyy", "zzz",
         "zzz", "yyy", "yyy", "zzz", "zzz", "abcd", "efgh", "ijk");
      List<String> cats4 = Arrays.asList("baseball", "basketball", "baseball", "basketball", "baseball", "basketball",
         "baseball", "basketball", "baseball", "basketball", "baseball", "basketball", "baseball", "basketball",
         "baseball", "basketball", "football", "football", "football");
      List<Double> values1 = Arrays.asList(23.0, 7.0, 10.0, 11.0, 323.0, 710.0, 1.0, 3.0, 99.0, 68.0, 1111.0, 1001.0,
         55.0, 1010.0, 405.0, 80.0, 10.0, 21.0, 15.0);
      List<Double> values2 = Arrays.asList(3.14159, 2.71828, 6.5, 1.338, 92.15, 2.25, 201.5, 68.67, 20.25, 30.25, 6.25,
         12.25, 72.25, 200.5, 42.25, 56.25, 24.0, 55.0, 69.0);

      for (int i = 0; i < recordAggValues.size(); i++)
      {
         AggregateValue<Record> aggValue = recordAggValues.get(i);
         assertEquals(cats1.get(i), aggValue.getPropertyValue("category1"));
         assertEquals(cats2.get(i), aggValue.getPropertyValue("category2"));
         assertEquals(cats3.get(i), aggValue.getPropertyValue("category3"));
         assertEquals(cats4.get(i), aggValue.getPropertyValue("category4"));
         assertEquals(values1.get(i), ((Number) aggValue.getAggregateValue(agg1)).doubleValue(), Math.abs(TestUtility.DELTA * values1.get(i)));
         assertEquals(values2.get(i), ((Number) aggValue.getAggregateValue(agg2)).doubleValue(), Math.abs(TestUtility.DELTA * values2.get(i)));
      }
   }

   /**
    * Ensure that the <code>AggregateValue</code> methods
    * <code>getAggregateValue(Aggregator)</code> and
    * <code>getAggregateValue(int)</code> methods return the same result.
    */
   @Test
   public void testAggregateValueAccess()
   {
      List<Record> records = TestUtility.getTestData();

      List<String> properties = Arrays.asList("category1", "category2");

      Aggregator agg1 = new SumAggregator("value1");
      Aggregator agg2 = new AvgAggregator("value2");
      Aggregator agg3 = new CountAggregator("value1");
      Aggregator agg4 = new MinAggregator("value1");
      Aggregator agg5 = new MaxAggregator("value2");
      List<Aggregator> aggs = Arrays.asList(agg1, agg2, agg3, agg4, agg5);

      List<AggregateValue<Record>> recordAggValues = Aggregations.groupBy(records,
         properties, aggs);

      assertEquals(7, recordAggValues.size());

      for (AggregateValue<Record> aggValue : recordAggValues)
      {
         for (int j = 0; j < aggs.size(); j++)
         {
            Assert.assertEquals(((Number) aggValue.getAggregateValue(aggs.get(j))).doubleValue(),
                         ((Number) aggValue.getAggregateValue(j)).doubleValue(), TestUtility.DELTA);
         }
      }
   }

   /**
    * If a property is not found, then a
    * <code>UnsupportedOperationException</code> is thrown.
    */
   @Test(expected = UnsupportedOperationException.class)
   public void testNoSuchProperty()
   {
      List<Record> records = TestUtility.getTestData();

      List<String> properties = Arrays.asList("category1", "category2");

      Aggregator agg1 = new SumAggregator("blah");
      List<Aggregator> aggs = Arrays.asList(agg1);

      Aggregations.groupBy(records, properties, aggs);
   }

   /**
    * Test the presence of method calls and enumerated values as parameters in
    * the method calls, which take the place of properties.
    */
   @Test
   public void testMethodsAndEnums()
   {
      List<CountyData> records = TestUtility.getSomeCountyData();

      List<String> properties = Arrays.asList("getStateCode()");

      Aggregator agg1 = new SumAggregator("getValue(net.sf.jagg.test.model.CountyData$MonthCode:CURR_MONTH, 4)");
      Aggregator agg2 = new SumAggregator("getValue(net.sf.jagg.test.model.CountyData$MonthCode:LAST_MONTH, 1)");
      Aggregator agg3 = new SumAggregator("getValue(net.sf.jagg.test.model.CountyData$MonthCode:YEAR_AGO_MONTH, 25)");
      List<Aggregator> aggs = Arrays.asList(agg1, agg2, agg3);

      List<AggregateValue<CountyData>> recordAggValues = Aggregations.groupBy(records,
         properties, aggs);

      List<String> states = Arrays.asList("CA", "FL");
      List<Double> values1 = Arrays.asList(5215082.0, 941519.0);
      List<Double> values2 = Arrays.asList(3224623.0, 1071894.0);
      List<Double> values3 = Arrays.asList(7779599.0, 2053475.0);

      assertEquals(2, recordAggValues.size());

      for (int i = 0; i < recordAggValues.size(); i++)
      {
         AggregateValue<CountyData> aggValue = recordAggValues.get(i);
         assertEquals(states.get(i), aggValue.getPropertyValue(0));
         assertEquals(values1.get(i), ((Number) aggValue.getAggregateValue(0)).doubleValue(), values1.get(i) * TestUtility.DELTA);
         assertEquals(values2.get(i), ((Number) aggValue.getAggregateValue(1)).doubleValue(), values1.get(i) * TestUtility.DELTA);
         assertEquals(values3.get(i), ((Number) aggValue.getAggregateValue(2)).doubleValue(), values1.get(i) * TestUtility.DELTA);
      }
   }

   /**
    * Ensures that an <code>IllegalArgumentException</code> is thrown when
    * an aggregator specification string specifies an <code>Aggregator</code>
    * that doesn't exist.
    */
   @Test(expected = IllegalArgumentException.class)
   public void testNoSuchAggregator()
   {
      Aggregator.getAggregator("DoesNotExistAggregator(prop1)");
   }

   /**
    * Ensures that Aggregations works using Multiset Discrimination.
    * @since 0.5.0
    */
   @Test
   public void testAggOnMsd()
   {
      List<Record> records = TestUtility.getTestData();
      List<String> properties = Arrays.asList("category1", "category2");

      List<Aggregator> aggs = Arrays.asList(new SumAggregator("value1"), new CountAggregator("*"));

      List<Double> values1 = Arrays.asList(51.0, 1037.0, 2279.0, 1550.0, 10.0, 21.0, 15.0);
      List<Integer> values2 = Arrays.asList(4, 4, 4, 5, 4, 6, 5);
      List<String> cats1 = Arrays.asList("AAA", "AAA", "BBB", "BBB", "CCC", "CCC", "CCC");
      List<Integer> cats2 = Arrays.asList(1, 2, 1, 2, 1, 2, 3);

      List<AggregateValue<Record>> aggValues = Aggregations.groupBy(records, properties, aggs, true);

      assertEquals(7, aggValues.size());

      for (int i = 0; i < aggValues.size(); i++)
      {
         AggregateValue<Record> aggValue = aggValues.get(i);
         assertEquals(cats1.get(i), aggValue.getPropertyValue(0));
         assertEquals(cats2.get(i), aggValue.getPropertyValue(1));
         assertEquals(values1.get(i), ((Number) aggValue.getAggregateValue(0)).doubleValue(), values1.get(i) * TestUtility.DELTA);
         assertEquals(values2.get(i), ((Number) aggValue.getAggregateValue(1)).doubleValue(), values2.get(i) * TestUtility.DELTA);
      }
   }

   /**
    * Ensures that Aggregations works using MSD on self-properties.
    */
   @Test
   public void testSelfPropertyOnMsd()
   {
      List<Integer> records = Arrays.asList(7, 10, 3, 23, 11, 11, 5, 7, 5, 13, 9, 18, 2, 12);
      List<String> properties = Arrays.asList(".");
      List<Aggregator> aggs = Arrays.<Aggregator>asList(new CountAggregator("*"));

      List<Integer> categories = Arrays.asList(10,  3, 23, 13,  9, 18,  2, 12,  7, 11,  5);
      List<Long> values        = Arrays.asList(1L, 1L, 1L, 1L, 1L, 1L, 1L, 1L, 2L, 2L, 2L);

      List<AggregateValue<Integer>> aggValues = Aggregations.groupBy(records, properties, aggs, true);

      assertEquals(11, aggValues.size());

      for (int i = 0; i < aggValues.size(); i++)
      {
         AggregateValue<Integer> aggValue = aggValues.get(i);
         assertEquals(categories.get(i), aggValue.getPropertyValue(0));
         assertEquals(values.get(i), aggValue.getAggregateValue(0));
      }
   }
}