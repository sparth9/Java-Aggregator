package acceptancetest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import static org.junit.Assert.*;
import net.sf.jagg.AggregateValue;
import net.sf.jagg.Aggregations;
import net.sf.jagg.Aggregator;
import net.sf.jagg.ConcatAggregator;
import net.sf.jagg.test.model.Record;

/**
 * Tests the <code>ConcatAggregator</code>.
 *
 * @author Randy Gettman
 * @since 0.3.0
 */
public class ConcatAggregatorTest
{
   /**
    * Test the concatenation functionality.
    */
   @Test
   public void testByProperty()
   {
      List<Record> records = TestUtility.getTestData();

      List<String> properties = Arrays.asList("category1", "category2");
      Aggregator agg = new ConcatAggregator("value1");
      Aggregator aggPipe = new ConcatAggregator("value1", "|");
      Aggregator aggComma = new ConcatAggregator("value1", ",");
      List<Aggregator> aggs = Arrays.asList(agg, aggPipe, aggComma);

      List<AggregateValue<Record>> recordAggValues = Aggregations.groupBy(records,
         properties, aggs);

      assertEquals(7, recordAggValues.size());

      List<String> cats1 = Arrays.asList("AAA", "AAA", "BBB", "BBB", "CCC", "CCC", "CCC");
      List<Integer> cats2 = Arrays.asList(1, 2, 1, 2, 1, 2, 3);
      List<String> values      = Arrays.asList("1011237"   , "13323710"   , "111110019968"   , "405805551005"    ,
         "1234"   , "123456"     , "12345"    );
      List<String> valuesPipe  = Arrays.asList("10|11|23|7", "1|3|323|710", "1111|1001|99|68", "405|80|55|5|1005",
         "1|2|3|4", "1|2|3|4|5|6", "1|2|3|4|5");
      List<String> valuesComma = Arrays.asList("10,11,23,7", "1,3,323,710", "1111,1001,99,68", "405,80,55,5,1005",
         "1,2,3,4", "1,2,3,4,5,6", "1,2,3,4,5");

      for (int i = 0; i < recordAggValues.size(); i++)
      {
         AggregateValue<Record> aggValue = recordAggValues.get(i);
         assertEquals(cats1.get(i), aggValue.getPropertyValue(0));
         assertEquals(cats2.get(i), aggValue.getPropertyValue(1));
         assertEquals(values.get(i), aggValue.getAggregateValue(0));
         assertEquals(valuesPipe.get(i), aggValue.getAggregateValue(1));
         assertEquals(valuesComma.get(i), aggValue.getAggregateValue(2));
      }
   }

   /**
    * Test when some values are null.
    */
   @Test
   public void testSomeNull()
   {
      List<Record> records = TestUtility.getSomeNullData();

      List<String> properties = Arrays.asList("category1");

      Aggregator aggCat3 = new ConcatAggregator("category3", "+");
      List<Aggregator> aggs = Arrays.asList(aggCat3);

      List<AggregateValue<Record>> recordAggValues = Aggregations.groupBy(records,
         properties, aggs);

      assertEquals(3, recordAggValues.size());

      List<String> cats3 = Arrays.asList("abcd", "efgh", "wxyz");
      List<String> valuesCat3 = Arrays.asList("not null+not null", "", "not null");

      for (int i = 0; i < recordAggValues.size(); i++)
      {
         AggregateValue<Record> aggValue = recordAggValues.get(i);
         assertEquals(cats3.get(i), aggValue.getPropertyValue(0));
         assertEquals(valuesCat3.get(i), aggValue.getAggregateValue(0));
      }
   }

   /**
    * Tests the merge functionality.
    */
   @Test
   public void testMerge()
   {
      ConcatAggregator agg1 = new ConcatAggregator(".", " ");
      ConcatAggregator agg2 = new ConcatAggregator(".", " ");
      agg1.init();
      agg2.init();

      agg1.iterate("I");
      agg2.iterate("construct");
      agg1.iterate("am");
      agg2.iterate("a");
      agg1.iterate("trying");
      agg2.iterate("complete");
      agg1.iterate("to");
      agg2.iterate("sentence.");

      agg1.merge(agg2);

      assertEquals("I am trying to construct a complete sentence.", agg1.terminate());
   }
   
	/**
	 * Aggregate the input integer objects
	 */
   @Test
   public void testInteger() {
	   List<Integer> records = Arrays.asList(5, 4, 5);

		List<String> properties = new ArrayList<String>(0);
		Aggregator agg = new ConcatAggregator(".");
		List<Aggregator> aggs = Arrays.asList(agg);

		List<AggregateValue<Integer>> recordAggValues = Aggregations.groupBy(
				records, properties, aggs);
		
		assertEquals(1, recordAggValues.size());
		assertEquals("545", recordAggValues.get(0).getAggregateValue(0));
   }
   

	 /**
	 * Test the replicate functionality
	 */
  @Test
  public void testReplicate() {
	   List<Integer> records = Arrays.asList(5, 4, 5);

		List<String> properties = new ArrayList<String>(0);
		Aggregator agg = new ConcatAggregator(".");
		List<Aggregator> aggs = Arrays.asList(agg);

		List<AggregateValue<Integer>> recordAggValues = Aggregations.groupBy(
				records, properties, aggs);
		
		assertEquals(1, recordAggValues.size());
		assertEquals("545", recordAggValues.get(0).getAggregateValue(0));
  }
   
   
   /**
	 * Test when input is empty
	 */
   @Test
   public void test_empty_input() {
	   List<Integer> records = Arrays.asList();

		List<String> properties = new ArrayList<String>(0);
		Aggregator agg = new ConcatAggregator(".");
		List<Aggregator> aggs = Arrays.asList(agg);

		List<AggregateValue<Integer>> recordAggValues = Aggregations.groupBy(
				records, properties, aggs);
		
		assertEquals(1, recordAggValues.size());
		assertEquals("", recordAggValues.get(0).getAggregateValue(0));
   }
}