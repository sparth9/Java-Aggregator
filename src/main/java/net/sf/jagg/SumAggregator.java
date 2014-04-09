package net.sf.jagg;

/**
 * This class represents the "sum" aggregator over numeric values.
 *
 * @author Randy Gettman
 * @since 0.1.0
 */
public class SumAggregator extends Aggregator
{
   private DoubleDouble mySum = new DoubleDouble();

   /**
    * Constructs an <code>SumAggregator</code> that operates on the specified
    * property.
    * @param property Add up all this property's values.
    */
   public SumAggregator(String property)
   {
      setProperty(property);
   }

   /**
    * Returns an uninitialized copy of this <code>Aggregator</code> object,
    * with the same property(ies) to analyze.
    * @return An uninitialized copy of this <code>Aggregator</code> object.
    */
   public SumAggregator replicate()
   {
      return new SumAggregator(getProperty());
   }

   /**
    * Initialize the sum to zero.
    */
   public void init()
   {
      mySum.reset();
   }

   /**
    * Add the property value to the sum.
    *
    * @param value The value to aggregate.
    */
   public void iterate(Object value)
   {
      if (value != null)
      {
         String property = getProperty();
         try
         {
            Number obj = (Number) getValueFromProperty(value, property);
            // Don't count nulls.
            if (obj != null)
            {
               mySum.addToSelf(obj.doubleValue());
            }
         }
         catch (ClassCastException e)
         {
            throw new UnsupportedOperationException("Property \"" + property +
               "\" must represent a Number.", e);
         }
      }
   }

   /**
    * Merge the given <code>Aggregator</code> into this one by adding the
    * respective sums.
    *
    * @param agg The <code>Aggregator</code> to merge into this one.
    */
   public void merge(Aggregator agg)
   {
      if (agg != null && agg instanceof SumAggregator)
      {
         SumAggregator otherAgg = (SumAggregator) agg;
         mySum.addToSelf(otherAgg.mySum);
      }
   }

   /**
    * Return the sum.
    * 
    * @return The sum as a <code>Double</code>, or <code>0</code> if
    *    no values have been accumulated.
    */
   public Double terminate()
   {
      return terminateDoubleDouble().doubleValue();
   }

   /**
    * Return the result as a <code>DoubleDouble</code>.  This is used mainly
    * when other <code>Aggregators</code> that use this result must maintain a
    * high precision.
    * @return The sum as a <code>DoubleDouble</code>, or <code>0</code> if
    *    no values have been accumulated.
    * @since 0.4.0
    */
   public DoubleDouble terminateDoubleDouble()
   {
      return new DoubleDouble(mySum);
   }
}
