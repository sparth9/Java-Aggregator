package net.sf.jagg;

/**
 * This class represents the "geometric mean" aggregator over numeric values.
 *
 * @author Randy Gettman
 * @since 0.1.0
 */
public class GeometricMeanAggregator extends Aggregator
{
   private double myProduct;
   private long   myCount;

   /**
    * Constructs an <code>GeometricMeanAggregator</code> that operates on the specified
    * property.
    * @param property Calculate the geometric mean of this property's values.
    */
   public GeometricMeanAggregator(String property)
   {
      setProperty(property);
   }

   /**
    * Returns an uninitialized copy of this <code>Aggregator</code> object,
    * with the same property(ies) to analyze.
    * @return An uninitialized copy of this <code>Aggregator</code> object.
    */
   public GeometricMeanAggregator replicate()
   {
      return new GeometricMeanAggregator(getProperty());
   }
   
   /**
    * Initialize the product to one and count to zero.
    */
   public void init()
   {
      myProduct = 1;
      myCount = 0;
   }

   /**
    * If not null, multiply the property value into the product and count it.
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
               myProduct *= obj.doubleValue();
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
    * Merge the given <code>Aggregator</code> into this one by multiplying
    * products and adding sums.
    *
    * @param agg The <code>Aggregator</code> to merge into this one.
    */
   public void merge(Aggregator agg)
   {
      if (agg != null && agg instanceof GeometricMeanAggregator)
      {
         GeometricMeanAggregator otherAgg = (GeometricMeanAggregator) agg;
         myProduct *= otherAgg.myProduct;
         myCount += otherAgg.myCount;
      }
   }

   /**
    * Return the geometric mean by taking the <em>n</em>th root of the product
    * of all values, where <em>n</em> is the count of all non-null values.
    *
    * @return The geometric mean as a <code>Double</code>.  Could return
    *    <code>NaN</code> if no values have been accumulated.
    */
   public Double terminate()
   {
      return terminateDoubleDouble().doubleValue();
   }

   /**
    * Return the result as a <code>DoubleDouble</code>.  This is used mainly
    * when other <code>Aggregators</code> that use this result must maintain a
    * high precision.
    * @return The geometric mean as a <code>DoubleDouble</code>, or
    *    <code>NaN</code> if no values have been accumulated.
    * @since 0.4.0
    */
   public DoubleDouble terminateDoubleDouble()
   {
      if (myCount > 0)
      {
         DoubleDouble result = new DoubleDouble(myProduct);
         result.nthRootSelf(myCount);
         return result;
      }
      return new DoubleDouble(DoubleDouble.NaN);
   }
}
