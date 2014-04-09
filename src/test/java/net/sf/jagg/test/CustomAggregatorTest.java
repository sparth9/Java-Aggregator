package net.sf.jagg.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import net.sf.jagg.AggregateValue;
import net.sf.jagg.Aggregations;
import net.sf.jagg.Aggregator;
import net.sf.jagg.test.model.Complex;
import net.sf.jagg.test.model.ComplexSumAggregator;


/**
 * Tests the custom <code>Aggregator</code> feature.
 *
 * @author Randy Gettman
 * @since 0.3.0
 */
public class CustomAggregatorTest
{
   /**
    * Tests the custom <code>Aggregator</code> feature.
    */
   @Test
   public void testByProperty()
   {
      List<Complex> complexNbrs = new ArrayList<Complex>();
      complexNbrs.add(new Complex(1, 4));
      complexNbrs.add(new Complex(5, -2));
      complexNbrs.add(new Complex(-7.1, 3.23));
      complexNbrs.add(new Complex(11, 0));
      complexNbrs.add(new Complex(0, -1));

      List<String> properties = new ArrayList<String>(0);
      Aggregator agg1 = new ComplexSumAggregator(".");
      List<Aggregator> aggs = Arrays.asList(agg1);

      List<AggregateValue<Complex>> recordAggValues = Aggregations.groupBy(complexNbrs,
         properties, aggs);

      assertEquals(1, recordAggValues.size());

      AggregateValue<Complex> aggValue = recordAggValues.get(0);
      assertEquals(new Complex(9.9, 4.23), aggValue.getAggregateValue(agg1));
   }

   /**
    * Ensure that specifying the fully-qualified class name (minus
    * "Aggregator"), resolves to the desired custom <code>Aggregator</code>.
    */
   @Test
   public void testByAggSpecString()
   {
      Aggregator agg = Aggregator.getAggregator("net.sf.jagg.test.model.ComplexSum(.)");
      assertEquals(ComplexSumAggregator.class, agg.getClass());
   }
}
