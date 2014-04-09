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
 * Tests what happens when attempting to aggregate empty lists of data.
 *
 * @author Randy Gettman
 * @since 0.5.0
 */
public class EmptyListTest
{
   /**
    * Ensure that all <code>Aggregators</code>, parallel or serial, return
    * <code>AggregateValues</code> with initial values when not grouping by
    * anything and there are no values.
    */
   @Test
   public void testNoProperties()
   {
      List<Record> records = new ArrayList<Record>();
      List<String> properties = new ArrayList<String>();

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

      List<AggregateValue<Record>> aggValuesSerial = Aggregations.groupBy(records, properties, aggs);
      List<AggregateValue<Record>> aggValuesParallel = Aggregations.groupBy(records, properties, aggs, 2);

      assertEquals(1, aggValuesSerial.size());
      assertEquals(1, aggValuesParallel.size());

      AggregateValue<Record> aggValueSerial = aggValuesSerial.get(0);
      AggregateValue<Record> aggValueParallel = aggValuesParallel.get(0);

      assertNull(aggValueSerial.getPropertyValue(0));
      assertNull(aggValueParallel.getPropertyValue(0));

      for (int i = 0; i < aggs.size(); i++)
      {
         Object parValue = aggValueParallel.getAggregateValue(i);
         Object serValue = aggValueSerial.getAggregateValue(i);

         if (aggs.get(i) instanceof MinAggregator || aggs.get(i) instanceof MaxAggregator)
         {
            // Max and Min return null if no values.
            assertNull(parValue);
            assertNull(serValue);
         }
         else if (aggs.get(i) instanceof CountAggregator || aggs.get(i) instanceof SumAggregator)
         {
            // Count and Sum return 0 if no values.
            double parDbl = ((Number) parValue).doubleValue();
            double serDbl = ((Number) serValue).doubleValue();
            assertEquals(0, parDbl, 0);
            assertEquals(0, serDbl, 0);
         }
         else if (aggs.get(i) instanceof ProductAggregator)
         {
            // Product returns 1 if no values.
            double parDbl = ((Number) parValue).doubleValue();
            double serDbl = ((Number) serValue).doubleValue();
            assertEquals(1, parDbl, 0);
            assertEquals(1, serDbl, 0);
         }
         else if (parValue instanceof Number)
         {
            // All other Numeric Aggs return NaN if no values.
            Double parDbl = (Double) parValue;
            Double serDbl = (Double) serValue;
            assertTrue(parDbl.isNaN());
            assertTrue(serDbl.isNaN());
         }
         else if (parValue instanceof LinearRegressionStats)
         {
            // LinearRegression returns a LinearRegressionStats(NaN, NaN, 0, NaN, NaN, NaN, NaN) if no values.
            LinearRegressionStats parStats = (LinearRegressionStats) parValue;
            LinearRegressionStats serStats = (LinearRegressionStats) serValue;
            assertTrue(Double.isNaN(parStats.getLineSlope()));
            assertTrue(Double.isNaN(serStats.getLineSlope()));
            assertTrue(Double.isNaN(parStats.getLineIntercept()));
            assertTrue(Double.isNaN(serStats.getLineIntercept()));
            assertEquals(0, parStats.getCount());
            assertEquals(0, serStats.getCount());
            assertTrue(Double.isNaN(parStats.getRSquared()));
            assertTrue(Double.isNaN(serStats.getRSquared()));
            assertTrue(Double.isNaN(parStats.getCorrelation()));
            assertTrue(Double.isNaN(serStats.getCorrelation()));
            assertTrue(Double.isNaN(parStats.getAvg1()));
            assertTrue(Double.isNaN(serStats.getAvg1()));
            assertTrue(Double.isNaN(parStats.getAvg2()));
            assertTrue(Double.isNaN(serStats.getAvg2()));
         }
         else
         {
            // Strings
            assertEquals("", parValue);
            assertEquals("", serValue);
         }
      }
   }

   /**
    * Ensure that all <code>Aggregators</code>, parallel or serial, do NOT
    * return any <code>AggregateValues</code> when grouping by something, and
    * there are no values.
    */
   @Test
   public void testProperties()
   {
      List<Record> records = new ArrayList<Record>();
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

      List<AggregateValue<Record>> aggValuesSerial = Aggregations.groupBy(records, properties, aggs);
      List<AggregateValue<Record>> aggValuesParallel = Aggregations.groupBy(records, properties, aggs, 2);

      assertEquals(0, aggValuesSerial.size());
      assertEquals(0, aggValuesParallel.size());
   }
}
