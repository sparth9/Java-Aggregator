package net.sf.jagg.test;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.*;

import net.sf.jagg.AggregateValue;
import net.sf.jagg.Aggregations;
import net.sf.jagg.Aggregator;
import net.sf.jagg.LinearRegressionAggregator;
import net.sf.jagg.LinearRegressionStats;
import net.sf.jagg.test.model.Record;

/**
 * Tests the <code>LinearRegressionAggregator</code>.
 *
 * @author Randy Gettman
 * @since 0.3.0
 */
public class LinearRegressionAggregatorTest
{
   /**
    * Test the linear regression functionality.
    */
   @Test
   public void testByProperty()
   {
      List<Record> records = TestUtility.getTestData();

      List<String> properties = Arrays.asList("category1", "category2");
      Aggregator agg1 = new LinearRegressionAggregator("value1", "value2");
      List<Aggregator> aggs = Arrays.asList(agg1);

      List<AggregateValue<Record>> recordAggValues = Aggregations.groupBy(records,
         properties, aggs);

      assertEquals(7, recordAggValues.size());

      List<String> cats1 = Arrays.asList("AAA", "AAA", "BBB", "BBB", "CCC", "CCC", "CCC");
      List<Integer> cats2 = Arrays.asList(1, 2, 1, 2, 1, 2, 3);
      List<Double> slopes  = Arrays.asList( -0.25330559343908796505155516072,
                                            -3.04785931228232719626354273710,
                                           -49.52160493827160493827160493827,
                                             7.48966942148760330578512396694,
                                             0.38235294117647058823529411765,
                                             0.22471295790049207217058501914,
                                             0.05274053627760252365930599369);
      List<Double> itrcpts = Arrays.asList( 13.61743677230036996596018647235,
                                           537.03951736969200648544994391623,
                                          1423.99768518518518518518518518519,
                                          -246.10795454545454545454545454545,
                                             0.20588235294117647058823529412,
                                             1.44013121924548933843630399125,
                                             2.27218059936908517350157728707);
      List<Double> counts  = Arrays.asList(  4.0,
                                             4.0,
                                             4.0,
                                             5.0,
                                             4.0,
                                             6.0,
                                             5.0);
      List<Double> r2Vals  = Arrays.asList(  0.00620758350308185107631594737,
                                             0.56313520375324471790971150754,
                                             0.83438538994117885314960845694,
                                             0.23172163572881276230558527209,
                                             0.99411764705882352941176470588,
                                             0.87959072092478325392486136062,
                                             0.56432373817034700315457413249);
      List<Double> avgs1   = Arrays.asList( 12.75,
                                           259.25,
                                           569.75,
                                           310.0,
                                             2.5,
                                             3.5,
                                             3.0);
      List<Double> avgs2   = Arrays.asList(  3.4244675,
                                            91.1425,
                                            17.25,
                                            74.25,
                                             6.0,
                                             9.16666666666666666666666666667,
                                            13.8);

      for (int i = 0; i < recordAggValues.size(); i++)
      {
         AggregateValue<Record> aggValue = recordAggValues.get(i);
         LinearRegressionStats stats = (LinearRegressionStats) aggValue.getAggregateValue(0);
         assertEquals(cats1.get(i), aggValue.getPropertyValue(0));
         assertEquals(cats2.get(i), aggValue.getPropertyValue(1));
         assertEquals(slopes.get(i), stats.getLineSlope(), Math.abs(TestUtility.DELTA * slopes.get(i)));
         assertEquals(itrcpts.get(i), stats.getLineIntercept(), Math.abs(TestUtility.DELTA * itrcpts.get(i)));
         assertEquals(counts.get(i), stats.getCount(), Math.abs(TestUtility.DELTA * counts.get(i)));
         assertEquals(r2Vals.get(i), stats.getRSquared(), Math.abs(TestUtility.DELTA * r2Vals.get(i)));
         assertEquals(avgs1.get(i), stats.getAvg1(), Math.abs(TestUtility.DELTA * avgs1.get(i)));
         assertEquals(avgs2.get(i), stats.getAvg2(), Math.abs(TestUtility.DELTA * avgs2.get(i)));
      }
   }
}