package net.sf.jagg.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.*;

import net.sf.jagg.AggregateValue;
import net.sf.jagg.Aggregations;
import net.sf.jagg.Aggregator;
import net.sf.jagg.AvgAggregator;
import net.sf.jagg.ConcatAggregator;
import net.sf.jagg.CorrelationAggregator;
import net.sf.jagg.CountAggregator;
import net.sf.jagg.CovarianceAggregator;
import net.sf.jagg.CovariancePopAggregator;
import net.sf.jagg.GeometricMeanAggregator;
import net.sf.jagg.HarmonicMeanAggregator;
import net.sf.jagg.LinearRegressionAggregator;
import net.sf.jagg.LinearRegressionStats;
import net.sf.jagg.MaxAggregator;
import net.sf.jagg.MinAggregator;
import net.sf.jagg.PercentileAggregator;
import net.sf.jagg.ProductAggregator;
import net.sf.jagg.StdDevAggregator;
import net.sf.jagg.StdDevPopAggregator;
import net.sf.jagg.SumAggregator;
import net.sf.jagg.VarianceAggregator;
import net.sf.jagg.VariancePopAggregator;
import net.sf.jagg.test.model.Record;

/**
 * Tests <code>Aggregators</code> for performance.
 *
 * @author Randy Gettman
 * @since 0.3.0
 */
public class PerformanceTest
{
   /**
    * Have 115,000 records.  With 2 threads, there will be a break point at
    * 57,500.  Thread 0 will iterate the first 7500 where category2 is 5, and
    * thread 1 will iterate the last 2500 where category2 is 6.  When they are
    * merged, this will (happen to) test the merge functionality for Variance
    * and Covariance Aggregators such that they will need to divide 2500 by
    * 7500, and hopefully in double floating point arithmetic will come to 1/3,
    * instead of the zero that would come in long (integer) arithmetic.
    */
   @Test
   public void testPerf1()
   {
      List<Record> records = new ArrayList<Record>(115000);
      for (int i = 0; i < 115000; i++)
         records.add(new Record("A", i / 10000, "B", "C", i, (double) i / 100 + 1));

      runTest(records);
   }

   /**
    * Like the first test but all "category2" values are the same.
    */
   @Test
   public void testPerf2()
   {
      List<Record> records = new ArrayList<Record>(115000);
      for (int i = 0; i < 115000; i++)
         records.add(new Record("A", 1, "B", "C", i, (double) i / 100 + 1));

      runTest(records);
   }

   /**
    * Run Aggregations on a <code>List</code> of <code>Records</code>.
    * @param records A <code>List</code> of <code>Records</code>.
    */
   private void runTest(List<Record> records)
   {
      List<String> properties = Arrays.asList("category2");

      List<Aggregator> aggs = new ArrayList<Aggregator>();
      aggs.add(new CountAggregator("*"));
      aggs.add(new AvgAggregator("value1"));
      aggs.add(new SumAggregator("value2"));
      aggs.add(new MinAggregator("value2"));
      aggs.add(new MaxAggregator("value2"));
      aggs.add(new ProductAggregator("value2"));
      aggs.add(new VarianceAggregator("value1"));
      aggs.add(new VariancePopAggregator("value2"));
      aggs.add(new StdDevAggregator("value2"));
      aggs.add(new StdDevPopAggregator("value1"));
      aggs.add(new GeometricMeanAggregator("value2"));
      aggs.add(new HarmonicMeanAggregator("value2"));
      aggs.add(new PercentileAggregator(0.5, "value2"));
      aggs.add(new CovarianceAggregator("value1", "value2"));
      aggs.add(new CovariancePopAggregator("value2", "value1"));
      aggs.add(new ConcatAggregator("value1", "|"));
      aggs.add(new CorrelationAggregator("value1", "value2"));
      aggs.add(new LinearRegressionAggregator("value2", "value1"));

      //long startTimeS = System.nanoTime();
      List<AggregateValue<Record>> serialAggValues = Aggregations.groupBy(records,
         properties, aggs);
      //long endTimeS = System.nanoTime();

      int numProcessors = Runtime.getRuntime().availableProcessors();
      if (numProcessors > 1)
      {
         //long startTimeP = System.nanoTime();
         List<AggregateValue<Record>> parallelAggValues = Aggregations.groupBy(records, properties, aggs, numProcessors);
         //long endTimeP = System.nanoTime();

         //long diffP = endTimeP - startTimeP;
         //long diffS = endTimeS - startTimeS;
         //System.out.println("Parallel: " + diffP);
         //System.out.println("Serial: " + diffS);
         //assertTrue(diffP < diffS);

         assertEquals(parallelAggValues.size(), serialAggValues.size());
         int size = parallelAggValues.size();
         for (int i = 0; i < size; i++)
         {
            AggregateValue<Record> parValues = parallelAggValues.get(i);
            AggregateValue<Record> serValues = serialAggValues.get(i);
            assertEquals(parValues.getPropertyValue(0), serValues.getPropertyValue(0));
            for (int j = 0; j < aggs.size(); j++)
            {
               Object parValue = parValues.getAggregateValue(j);
               Object serValue = serValues.getAggregateValue(j);

               if (parValue instanceof Number)
               {
                  double parDbl = ((Number) parValue).doubleValue();
                  double serDbl = ((Number) serValue).doubleValue();
                  assertEquals(parDbl, serDbl, Math.abs(serDbl * TestUtility.DELTA));
               }
               else if (parValue instanceof LinearRegressionStats)
               {
                  LinearRegressionStats parStats = (LinearRegressionStats) parValue;
                  LinearRegressionStats serStats = (LinearRegressionStats) serValue;
                  assertEquals(parStats.getLineSlope(), serStats.getLineSlope(), Math.abs(serStats.getLineSlope() * TestUtility.DELTA));
                  assertEquals(parStats.getLineIntercept(), serStats.getLineIntercept(), Math.abs(serStats.getLineIntercept() * TestUtility.DELTA));
                  assertEquals(parStats.getCount(), serStats.getCount(), Math.abs(serStats.getCount() * TestUtility.DELTA));
                  assertEquals(parStats.getRSquared(), serStats.getRSquared(), Math.abs(serStats.getRSquared() * TestUtility.DELTA));
                  assertEquals(parStats.getCorrelation(), serStats.getCorrelation(), Math.abs(serStats.getCorrelation() * TestUtility.DELTA));
                  assertEquals(parStats.getAvg1(), serStats.getAvg1(), Math.abs(serStats.getAvg1() * TestUtility.DELTA));
                  assertEquals(parStats.getAvg2(), serStats.getAvg2(), Math.abs(serStats.getAvg2() * TestUtility.DELTA));
               }
               else
               {
                  // Strings
                  assertEquals(parValue, serValue);
               }
            }
         }
      }
   }
}