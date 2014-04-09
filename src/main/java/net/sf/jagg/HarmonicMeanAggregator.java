package net.sf.jagg;

/**
 * This class represents the "harmonic mean" aggregator over numeric values.
 *
 * @author Randy Gettman
 * @since 0.1.0
 */
public class HarmonicMeanAggregator extends Aggregator
{
   private DoubleDouble mySum = new DoubleDouble();
   private long   myCount;
   private long   myZeroes;

   /**
    * Constructs an <code>HarmonicMeanAggregator</code> that operates on the specified
    * property.
    * @param property Calculate the harmonic mean of this property's values.
    */
   public HarmonicMeanAggregator(String property)
   {
      setProperty(property);
   }

   /**
    * Returns an uninitialized copy of this <code>Aggregator</code> object,
    * with the same property(ies) to analyze.
    * @return An uninitialized copy of this <code>Aggregator</code> object.
    */
   public HarmonicMeanAggregator replicate()
   {
      return new HarmonicMeanAggregator(getProperty());
   }
   
   /**
    * Initialize the sum and count to zero.
    */
   public void init()
   {
      mySum.reset();
      myCount = 0;
      myZeroes = 0;
   }

   /**
    * If not null, add the reciprocal of the property value to the sum and
    * count it.
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
               double d = obj.doubleValue();
               if (d == 0)
                  myZeroes++;
               else
               {
                  DoubleDouble temp = new DoubleDouble(1.0);
                  temp.divideSelfBy(obj.doubleValue());
                  mySum.addToSelf(temp);
               }
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
      if (agg != null && agg instanceof HarmonicMeanAggregator)
      {
         HarmonicMeanAggregator otherAgg = (HarmonicMeanAggregator) agg;
         mySum.addToSelf(otherAgg.mySum);
         myCount += otherAgg.myCount;
         myZeroes += otherAgg.myZeroes;
      }
   }

   /**
    * Return the harmonic mean by dividing the count by the sum.
    *
    * @return The harmonic mean as a <code>Double</code>.  Could return
    *    <code>NaN</code> if no values have been accumulated or if a zero
    *    exists in the values.
    */
   public Double terminate()
   {
      return terminateDoubleDouble().doubleValue();
   }

   /**
    * Return the result as a <code>DoubleDouble</code>.  This is used mainly
    * when other <code>Aggregators</code> that use this result must maintain a
    * high precision.
    * @return The harmonic mean as a <code>DoubleDouble</code>, or
    *    <code>NaN</code> if no values have been accumulated.
    * @since 0.4.0
    */
   public DoubleDouble terminateDoubleDouble()
   {
      if (myCount <= 0 || myZeroes > 0 || mySum.compareTo(DoubleDouble.ZERO) == 0)
         return new DoubleDouble(DoubleDouble.NaN);
      DoubleDouble result = new DoubleDouble(myCount);
      result.divideSelfBy(mySum);
      return result;
   }
}
