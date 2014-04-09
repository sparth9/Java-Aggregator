package net.sf.jagg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class allows represents the "percentile" aggregator over numeric
 * values.
 *
 * The first property is the desired percentile, between 0 and 1 inclusive, and
 * the second property denotes the desired sort order and return value.
 *
 * @author Randy Gettman
 * @since 0.1.0
 */
public class PercentileAggregator extends TwoPropAggregator
{
   private List<Object> myRecords;
   private double  myPercentile;

   /**
    * Constructs a <code>PercentileAggregator</code> on the specified
    * properties, in the format: <code>percentile, property</code>.
    * @param properties A specification string in the format:
    *    <code>percentile, property</code>.
    */
   public PercentileAggregator(String properties)
   {
      setProperty(properties);
   }

   /**
    * Constructs a <code>PercentileAggregator</code> that operates on the specified
    * properties.
    * @param percentile The percentile value, between zero and one.
    * @param property Determine the percentile of this property.
    */
   public PercentileAggregator(double percentile, String property)
   {
      setProperty("" + percentile + "," + property);
   }

   /**
    * Returns an uninitialized copy of this <code>Aggregator</code> object,
    * with the same property(ies) to analyze.
    * @return An uninitialized copy of this <code>Aggregator</code> object.
    */
   public PercentileAggregator replicate()
   {
      return new PercentileAggregator(myPercentile, getProperty2());
   }

   /**
    * Expects that the first "property" given is the actual desired percentile,
    * from 0 to 1 inclusive.  The second "property" is the sort parameter.
    *
    * @param property The property string, with one comma separating two actual
    *    properties.
    * @throws NumberFormatException If the first property, the percentile, is
    *    not a number.
    * @throws IllegalArgumentException If the first property, the percentile,
    *    is not between 0 and 1, inclusive.
    * @see Aggregator#getProperty()
    * @see TwoPropAggregator#getProperty2()
    */
   @Override
   protected void setProperty(String property)
   {
      super.setProperty(property);
      myPercentile = Double.parseDouble(getProperty());
      if (myPercentile < 0 || myPercentile > 1)
      {
         throw new IllegalArgumentException("First property (percentile) must be between 0 and 1 inclusive: " +
            myPercentile);
      }
      if (getProperty2() == null)
      {
         throw new IllegalArgumentException("Second property (sort parameter) must not be null.");
      }
   }

   /**
    * Initialize an internal list to empty.
    */
   public void init()
   {
      myRecords = new ArrayList<Object>();
   }

   /**
    * Make sure the second property's value is not null, then add the entire
    * <code>Object</code> to an internal list.
    *
    * @param value The value to aggregate.
    */
   public void iterate(Object value)
   {
      if (value != null)
      {
         String property = getProperty2();

         try
         {
            // Examine the second property which is the sort order.
            Number obj = (Number) getValueFromProperty(value, property);

            // Don't count nulls.
            if (obj != null)
            {
               myRecords.add(value);
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
    * contents of the given <code>Aggregator's</code> internal list into this
    * <code>Aggregator's</code> internal list.
    *
    * @param agg The <code>Aggregator</code> to merge into this one.
    */
   public void merge(Aggregator agg)
   {
      if (agg != null && agg instanceof PercentileAggregator)
      {
         PercentileAggregator otherAgg = (PercentileAggregator) agg;
         myRecords.addAll(otherAgg.myRecords);
      }
   }

   /**
    * Return the value among the values in the specified property that matches
    * the given percentile value, with the following algorithm:
    * <ol>
    * <li>Sort the internal list with respect to the second property, using
    *    <code>Collections.sort</code>, using a
    *    <code>PropertiesComparator</code> that compares values based on the
    *    second property given.
    * <li>Calculate a zero-based "row number" based on the percentile value
    *    (the first property given), with the formula <em>r</em> = <em>p</em> *
    *    (<em>n</em> - 1), where <em>r</em> is the row number, <em>p</em> is
    *    the percentile value, and <em>n</em> is the number of non-null values
    *    processed.
    * <li>If <em>r</em> is an integer, then return the value of that row's
    *    property.
    * <li>Else, return a linear interpolation of the values of the two rows'
    *    properties bounding <em>r</em>.
    * </ol>
    *
    * @return The desired <code>Double</code> that best matches the given
    *    percentile value, or <code>null</code> if no items were processed.
    * @see Collections#sort
    * @see PropertiesComparator
    */
   public Double terminate()
   {
      return terminateDoubleDouble().doubleValue();
   }

   /**
    * Return the result as a <code>DoubleDouble</code>.  This is used mainly
    * when other <code>Aggregators</code> that use this result must maintain a
    * high precision.
    * @return The desired <code>DoubleDouble</code> that best matches the given
    *    percentile value, or <code>NaN</code> if no values have been
    *    accumulated.
    * @since 0.4.0
    */
   public DoubleDouble terminateDoubleDouble()
   {
      int numItems = myRecords.size();
      DoubleDouble rownum = new DoubleDouble(myPercentile);
      rownum.multiplySelfBy(numItems - 1);
      // Don't need to implement DoubleDouble.floor() and DoubleDouble.ceil(),
      // unless we are aggregating over 2^52 items (4 quadrillion).  That's
      // impossible, because the ArrayList.get method takes an int, which is
      // limited to 2 billion or so.
      double floor = Math.floor(rownum.doubleValue());
      double ceiling = Math.ceil(rownum.doubleValue());

      if (numItems == 0)
         return new DoubleDouble(DoubleDouble.NaN);

      // Must sort it before determining the correct value!
      ArrayList<String> sortProperties = new ArrayList<String>();
      sortProperties.add(getProperty2());
      PropertiesComparator<Object> comparator = new PropertiesComparator<Object>(sortProperties);
      Collections.sort(myRecords, comparator);

      // Now check if the mapped row number maps directly to a specific row or
      // somewhere in between two rows.
      if (rownum.doubleValue() == floor && rownum.doubleValue() == ceiling)
      {
         // Return value of property at specified row.
         String property = null;
         Object value = myRecords.get((int) floor);

         try
         {
            // Examine the second property which is the sort order.
            property = getProperty2();
            Number obj = (Number) getValueFromProperty(value, property);
            return new DoubleDouble(obj.doubleValue());
         }
         catch (ClassCastException e)
         {
            throw new UnsupportedOperationException("Property \"" + property +
               "\" must represent a Number.", e);
         }
      }
      else
      {
         // Return linear interpolation of the values at the floor row and the
         // ceiling row.
         String property = null;
         Object lowValue = myRecords.get((int) floor);
         Object highValue = myRecords.get((int) ceiling);

         try
         {
            // Examine the second property which is the sort order.
            property = getProperty2();
            Number obj1 = (Number) getValueFromProperty(lowValue, property);
            Number obj2 = (Number) getValueFromProperty(highValue, property);
            DoubleDouble low = new DoubleDouble(obj1.doubleValue());
            DoubleDouble high = new DoubleDouble(obj2.doubleValue());
            DoubleDouble temp = new DoubleDouble(rownum);
            temp.subtractFromSelf(floor);
            temp.multiplySelfBy(high);
            DoubleDouble temp2 = new DoubleDouble(rownum);
            temp2.negateSelf();
            temp2.addToSelf(ceiling);
            temp2.multiplySelfBy(low);
            temp2.addToSelf(temp);
            return temp2;
         }
         catch (ClassCastException e)
         {
            throw new UnsupportedOperationException("Property \"" + property +
               "\" must represent a Number.", e);
         }
      }
   }
}
