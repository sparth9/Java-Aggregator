package net.sf.jagg.test;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.*;

import net.sf.jagg.AggregateValue;
import net.sf.jagg.Aggregation;
import net.sf.jagg.Aggregator;
import net.sf.jagg.SumAggregator;
import net.sf.jagg.test.model.Record;

/**
 * Tests the "grouping" and "grouping ID" functionality of
 * <code>AggregateValues</code>, with super-aggregate data.
 *
 * @author Randy Gettman
 * @since 0.7.0
 */
public class GroupingGroupingIdTest
{
   /**
    * Test the "isGrouping" method on <code>AggregateValue</code>.
    */
   @Test
   public void testGrouping()
   {
      List<Record> records = TestUtility.getTestData();
      List<String> properties = Arrays.asList("category1", "category2", "category3", "category4");  // {0, 1, 2, 3}

      Aggregator agg1 = new SumAggregator("value1");
      List<Aggregator> aggs = Arrays.asList(agg1);

      List<Integer> cube = Arrays.asList(2, 3);

      Aggregation aggregation = new Aggregation.Builder().setProperties(properties)
         .setAggregators(aggs).setCube(cube).build();

      List<AggregateValue<Record>> aggValues = aggregation.groupBy(records);

      // 19 values for {0, 1, 2, 3}, 11 values for {0, 1, 2}, 11 values for {0, 1, 3},
      // 7 values for {0, 1}.
      assertEquals(48, aggValues.size());

      // Refer to CubeTest for the actual values of the categories.

      // isGrouping(0), isGrouping(1) should always be false.
      List<Boolean> isGroupingTwo = Arrays.asList(
         // grouping set: {0, 1, 2, 3}
         false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false,
         // grouping set: {0, 1, 3}
         true, true, true, true, true, true, true, true, true, true, true,
         // grouping set: {0, 1, 2}
         false, false, false, false, false, false, false, false, false, false, false,
         // grouping set: {0, 1}
         true, true, true, true, true, true, true
      );
      List<Boolean> isGroupingThree = Arrays.asList(
         // grouping set: {0, 1, 2, 3}
         false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false,
         // grouping set: {0, 1, 3}
         false, false, false, false, false, false, false, false, false, false, false,
         // grouping set: {0, 1, 2}
         true, true, true, true, true, true, true, true, true, true, true,
         // grouping set: {0, 1}
         true, true, true, true, true, true, true
      );

      for (int i = 0; i < aggValues.size(); i++)
      {
         AggregateValue<Record> aggValue = aggValues.get(i);
         assertFalse(aggValue.isGrouping(0));
         assertFalse(aggValue.isGrouping("category1"));
         assertFalse(aggValue.isGrouping("0"));
         assertFalse(aggValue.isGrouping(1));
         assertFalse(aggValue.isGrouping("category2"));
         assertFalse(aggValue.isGrouping("1"));
         assertEquals(isGroupingTwo.get(i), aggValue.isGrouping(2));
         assertEquals(isGroupingTwo.get(i), aggValue.isGrouping("category3"));
         assertEquals(isGroupingTwo.get(i), aggValue.isGrouping("2"));
         assertEquals(isGroupingThree.get(i), aggValue.isGrouping(3));
         assertEquals(isGroupingThree.get(i), aggValue.isGrouping("category4"));
         assertEquals(isGroupingThree.get(i), aggValue.isGrouping("3"));
      }
   }

   /**
    * Ensure that an <code>IllegalArgumentException</code> is thrown when the
    * field reference is negative.
    */
   @Test(expected=IllegalArgumentException.class)
   public void testGroupingOutOfRangeLess()
   {
      List<Record> records = TestUtility.getTestData();
      List<String> properties = Arrays.asList("category1", "category2", "category3", "category4");  // {0, 1, 2, 3}

      Aggregator agg1 = new SumAggregator("value1");
      List<Aggregator> aggs = Arrays.asList(agg1);

      List<Integer> cube = Arrays.asList(2, 3);

      Aggregation aggregation = new Aggregation.Builder().setProperties(properties)
         .setAggregators(aggs).setCube(cube).build();

      List<AggregateValue<Record>> aggValues = aggregation.groupBy(records);

      aggValues.get(0).isGrouping(-1);
   }

   /**
    * Ensure that an <code>IllegalArgumentException</code> is thrown when the
    * field reference is too large.
    */
   @Test(expected=IllegalArgumentException.class)
   public void testGroupingOutOfRangeGreater()
   {
      List<Record> records = TestUtility.getTestData();
      List<String> properties = Arrays.asList("category1", "category2", "category3", "category4");  // {0, 1, 2, 3}

      Aggregator agg1 = new SumAggregator("value1");
      List<Aggregator> aggs = Arrays.asList(agg1);

      List<Integer> cube = Arrays.asList(2, 3);

      Aggregation aggregation = new Aggregation.Builder().setProperties(properties)
         .setAggregators(aggs).setCube(cube).build();

      List<AggregateValue<Record>> aggValues = aggregation.groupBy(records);

      aggValues.get(0).isGrouping(4);
   }

   /**
    * Ensure that an <code>IllegalArgumentException</code> is thrown when the
    * field reference string does not exist.
    */
   @Test(expected=IllegalArgumentException.class)
   public void testGroupingPropDoesNotExist()
   {
      List<Record> records = TestUtility.getTestData();
      List<String> properties = Arrays.asList("category1", "category2", "category3", "category4");  // {0, 1, 2, 3}

      Aggregator agg1 = new SumAggregator("value1");
      List<Aggregator> aggs = Arrays.asList(agg1);

      List<Integer> cube = Arrays.asList(2, 3);

      Aggregation aggregation = new Aggregation.Builder().setProperties(properties)
         .setAggregators(aggs).setCube(cube).build();

      List<AggregateValue<Record>> aggValues = aggregation.groupBy(records);

      aggValues.get(0).isGrouping("doesnotexist");
   }

   /**
    * Test the "getGroupingId" method on <code>AggregateValue</code>.
    */
   @Test
   public void testGroupingId()
   {
      List<Record> records = TestUtility.getTestData();
      List<String> properties = Arrays.asList("category1", "category2", "category3", "category4");  // {0, 1, 2, 3}

      Aggregator agg1 = new SumAggregator("value1");
      List<Aggregator> aggs = Arrays.asList(agg1);

      List<Integer> cube = Arrays.asList(2, 3);

      Aggregation aggregation = new Aggregation.Builder().setProperties(properties)
         .setAggregators(aggs).setCube(cube).build();

      List<AggregateValue<Record>> aggValues = aggregation.groupBy(records);

      // 19 values for {0, 1, 2, 3}, 11 values for {0, 1, 2}, 11 values for {0, 1, 3},
      // 7 values for {0, 1}.
      assertEquals(48, aggValues.size());

      // Refer to CubeTest for the actual values of the categories.

      // Grouping IDs for arguments containing only 0 and/or 1 are always 0.
      List<Integer> groupingIdsTwo = Arrays.asList(
         // grouping set: {0, 1, 2, 3}
         0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
         // grouping set: {0, 1, 3}
         1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
         // grouping set: {0, 1, 2}
         0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
         // grouping set: {0, 1}
         1, 1, 1, 1, 1, 1, 1
      );
      List<Integer> groupingIdsThree = Arrays.asList(
         // grouping set: {0, 1, 2, 3}
         0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
         // grouping set: {0, 1, 3}
         0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
         // grouping set: {0, 1, 2}
         1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
         // grouping set: {0, 1}
         1, 1, 1, 1, 1, 1, 1
      );
      List<Integer> groupingIdsTwoThree = Arrays.asList(
         // grouping set: {0, 1, 2, 3}
         0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
         // grouping set: {0, 1, 3}
         2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
         // grouping set: {0, 1, 2}
         1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
         // grouping set: {0, 1}
         3, 3, 3, 3, 3, 3, 3
      );
      List<Integer> groupingIdsThreeTwo = Arrays.asList(
         // grouping set: {0, 1, 2, 3}
         0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
         // grouping set: {0, 1, 3}
         1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
         // grouping set: {0, 1, 2}
         2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
         // grouping set: {0, 1}
         3, 3, 3, 3, 3, 3, 3
      );
      List<Integer> groupingIdsTwoZero = Arrays.asList(
         // grouping set: {0, 1, 2, 3}
         0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
         // grouping set: {0, 1, 3}
         2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
         // grouping set: {0, 1, 2}
         0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
         // grouping set: {0, 1}
         2, 2, 2, 2, 2, 2, 2
      );
      List<Integer> groupingIdsTwoZeroOne = Arrays.asList(
         // grouping set: {0, 1, 2, 3}
         0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
         // grouping set: {0, 1, 3}
         4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4,
         // grouping set: {0, 1, 2}
         0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
         // grouping set: {0, 1}
         4, 4, 4, 4, 4, 4, 4
      );
      List<Integer> groupingIdsThreeTwoOneZero = Arrays.asList(
         // grouping set: {0, 1, 2, 3}
         0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
         // grouping set: {0, 1, 3}
         4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4,
         // grouping set: {0, 1, 2}
         8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8,
         // grouping set: {0, 1}
         12, 12, 12, 12, 12, 12, 12
      );

      for (int i = 0; i < aggValues.size(); i++)
      {
         AggregateValue<Record> aggValue = aggValues.get(i);
         assertEquals(0, aggValue.getGroupingId(Arrays.asList(0)));
         assertEquals(0, aggValue.getGroupingId(Arrays.asList("category1")));
         assertEquals(0, aggValue.getGroupingId(Arrays.asList("0")));
         assertEquals(0, aggValue.getGroupingId(Arrays.asList(1)));
         assertEquals(0, aggValue.getGroupingId(Arrays.asList("category2")));
         assertEquals(0, aggValue.getGroupingId(Arrays.asList("1")));
         assertEquals(0, aggValue.getGroupingId(Arrays.asList(0, 1)));
         assertEquals(0, aggValue.getGroupingId(Arrays.asList("category1", "category2")));
         assertEquals(0, aggValue.getGroupingId(Arrays.asList("0", "1")));
         assertEquals(groupingIdsTwo.get(i).intValue(), aggValue.getGroupingId(Arrays.asList(2)));
         assertEquals(groupingIdsTwo.get(i).intValue(), aggValue.getGroupingId(Arrays.asList("category3")));
         assertEquals(groupingIdsTwo.get(i).intValue(), aggValue.getGroupingId(Arrays.asList("2")));
         assertEquals(groupingIdsThree.get(i).intValue(), aggValue.getGroupingId(Arrays.asList(3)));
         assertEquals(groupingIdsThree.get(i).intValue(), aggValue.getGroupingId(Arrays.asList("category4")));
         assertEquals(groupingIdsThree.get(i).intValue(), aggValue.getGroupingId(Arrays.asList("3")));
         assertEquals(groupingIdsTwoThree.get(i).intValue(), aggValue.getGroupingId(Arrays.asList(2, 3)));
         assertEquals(groupingIdsTwoThree.get(i).intValue(), aggValue.getGroupingId(Arrays.asList("category3", "category4")));
         assertEquals(groupingIdsTwoThree.get(i).intValue(), aggValue.getGroupingId(Arrays.asList("2", "3")));
         assertEquals(groupingIdsThreeTwo.get(i).intValue(), aggValue.getGroupingId(Arrays.asList(3, 2)));
         assertEquals(groupingIdsThreeTwo.get(i).intValue(), aggValue.getGroupingId(Arrays.asList("category4", "category3")));
         assertEquals(groupingIdsThreeTwo.get(i).intValue(), aggValue.getGroupingId(Arrays.asList("3", "2")));
         assertEquals(groupingIdsTwoZero.get(i).intValue(), aggValue.getGroupingId(Arrays.asList(2, 0)));
         assertEquals(groupingIdsTwoZero.get(i).intValue(), aggValue.getGroupingId(Arrays.asList("category3", "category1")));
         assertEquals(groupingIdsTwoZero.get(i).intValue(), aggValue.getGroupingId(Arrays.asList("2", "0")));
         assertEquals(groupingIdsTwoZeroOne.get(i).intValue(), aggValue.getGroupingId(Arrays.asList(2, 0, 1)));
         assertEquals(groupingIdsTwoZeroOne.get(i).intValue(), aggValue.getGroupingId(Arrays.asList("category3", "category1", "category2")));
         assertEquals(groupingIdsTwoZeroOne.get(i).intValue(), aggValue.getGroupingId(Arrays.asList("2", "0", "1")));
         assertEquals(groupingIdsThreeTwoOneZero.get(i).intValue(), aggValue.getGroupingId(Arrays.asList(3, 2, 1, 0)));
         assertEquals(groupingIdsThreeTwoOneZero.get(i).intValue(), aggValue.getGroupingId(Arrays.asList("category4", "category3", "category2", "category1")));
         assertEquals(groupingIdsThreeTwoOneZero.get(i).intValue(), aggValue.getGroupingId(Arrays.asList("3", "2", "1", "0")));
      }
   }

   /**
    * Ensure that an <code>IllegalArgumentException</code> is thrown when the
    * field reference is negative.
    */
   @Test(expected=IllegalArgumentException.class)
   public void testGroupingIdOutOfRangeLess()
   {
      List<Record> records = TestUtility.getTestData();
      List<String> properties = Arrays.asList("category1", "category2", "category3", "category4");  // {0, 1, 2, 3}

      Aggregator agg1 = new SumAggregator("value1");
      List<Aggregator> aggs = Arrays.asList(agg1);

      List<Integer> cube = Arrays.asList(2, 3);

      Aggregation aggregation = new Aggregation.Builder().setProperties(properties)
         .setAggregators(aggs).setCube(cube).build();

      List<AggregateValue<Record>> aggValues = aggregation.groupBy(records);

      aggValues.get(0).getGroupingId(Arrays.asList(0, -1));
   }

   /**
    * Ensure that an <code>IllegalArgumentException</code> is thrown when the
    * field reference is too large.
    */
   @Test(expected=IllegalArgumentException.class)
   public void testGroupingIdOutOfRangeGreater()
   {
      List<Record> records = TestUtility.getTestData();
      List<String> properties = Arrays.asList("category1", "category2", "category3", "category4");  // {0, 1, 2, 3}

      Aggregator agg1 = new SumAggregator("value1");
      List<Aggregator> aggs = Arrays.asList(agg1);

      List<Integer> cube = Arrays.asList(2, 3);

      Aggregation aggregation = new Aggregation.Builder().setProperties(properties)
         .setAggregators(aggs).setCube(cube).build();

      List<AggregateValue<Record>> aggValues = aggregation.groupBy(records);

      aggValues.get(0).getGroupingId(Arrays.asList(4));
   }

   /**
    * Ensure that an <code>IllegalArgumentException</code> is thrown when the
    * field reference string does not exist.
    */
   @Test(expected=IllegalArgumentException.class)
   public void testGroupingIdPropDoesNotExist()
   {
      List<Record> records = TestUtility.getTestData();
      List<String> properties = Arrays.asList("category1", "category2", "category3", "category4");  // {0, 1, 2, 3}

      Aggregator agg1 = new SumAggregator("value1");
      List<Aggregator> aggs = Arrays.asList(agg1);

      List<Integer> cube = Arrays.asList(2, 3);

      Aggregation aggregation = new Aggregation.Builder().setProperties(properties)
         .setAggregators(aggs).setCube(cube).build();

      List<AggregateValue<Record>> aggValues = aggregation.groupBy(records);

      aggValues.get(0).getGroupingId(Arrays.asList("category1", "doesnotexist", "category2"));
   }
}
