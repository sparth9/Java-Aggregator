package net.sf.jagg;

/**
 * This class represents the "avg" aggregator over numeric values.
 *
 * @author Randy Gettman
 * @since 0.1.0
 */
public class AvgAggregator extends Aggregator
{
   private DoubleDouble mySum = new DoubleDouble();
   private long   myCount;

   /**
    * Constructs an <code>AvgAggregator</code> that operates on the specified
    * property.
    * @param property Average this property's values.
    */
   public AvgAggregator(String property)
   {
      setProperty(property);
   }

   /**
    * Returns an uninitialized copy of this <code>Aggregator</code> object,
    * with the same property(ies) to analyze.
    * @return An uninitialized copy of this <code>Aggregator</code> object.
    */
   public AvgAggregator replicate()
   {
      return new AvgAggregator(getProperty());
   }

   /**
    * Initialize the sum and count to zero.
    */
   public void init()
   {
      mySum.reset();
      myCount = 0;
   }

   /**
    * If not null, add the property to the sum and count it.
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
               myCount++;
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
    * Merge the given <code>Aggregator</code> into this one by adding counts
    * and sums.
    *
    * @param agg The <code>Aggregator</code> to merge into this one.
    */
   public void merge(Aggregator agg)
   {
      if (agg != null && agg instanceof AvgAggregator)
      {
         AvgAggregator otherAgg = (AvgAggregator) agg;
         mySum.addToSelf(otherAgg.mySum);
         myCount += otherAgg.myCount;
      }
   }

   /**
    * Return the average by dividing the sum by the count.
    *
    * @return The average as a <code>Double</code>, or <code>NaN</code> if no
    *    values have been accumulated.
    */
   public Double terminate()
   {
      return terminateDoubleDouble().doubleValue();
   }

   /**
    * Return the result as a <code>DoubleDouble</code>.  This is used mainly
    * when other <code>Aggregators</code> that use this result must maintain a
    * high precision.
    * @return The average as a <code>Double</code>, or <code>NaN</code> if no
    *    values have been accumulated.
    * @since 0.4.0
    */
   public DoubleDouble terminateDoubleDouble()
   {
      if (myCount > 0)
      {
         DoubleDouble result = new DoubleDouble(mySum);
         result.divideSelfBy(myCount);
         return result;
      }
      return DoubleDouble.NaN;
   }
}
