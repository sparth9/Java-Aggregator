package net.sf.jagg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class represents the "mode" aggregator over <code>Comparable</code>
 * values.
 *
 * @author Randy Gettman
 * @since 0.6.0
 */
public class ModeAggregator extends Aggregator
{
   private List<Comparable> myRecords;

   /**
    * Constructs a <code>ModeAggregator</code> that operates on the specified
    * property.
    * @param property Determine the statistical mode of this property's values.
    */
   public ModeAggregator(String property)
   {
      setProperty(property);
   }

   /**
    * Returns an uninitialized copy of this <code>Aggregator</code> object,
    * with the same property(ies) to analyze.
    * @return An uninitialized copy of this <code>Aggregator</code> object.
    */
   public ModeAggregator replicate()
   {
      return new ModeAggregator(getProperty());
   }

   /**
    * Initialize an internal list to empty.
    */
   public void init()
   {
      myRecords = new ArrayList<Comparable>();
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
         String property = getProperty();

         try
         {
            // The property must be Comparable.
            Comparable comp = (Comparable) getValueFromProperty(value, property);

            // Don't count nulls.
            if (comp != null)
            {
               myRecords.add(comp);
            }
         }
         catch (ClassCastException e)
         {
            throw new UnsupportedOperationException("Property \"" + property +
               "\" must be Comparable.", e);
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
      if (agg != null && agg instanceof ModeAggregator)
      {
         ModeAggregator otherAgg = (ModeAggregator) agg;
         myRecords.addAll(otherAgg.myRecords);
      }
   }

   /**
    * Return the value among the values in the specified property that occurs
    * most often (the statistical mode), or any of the modes if there is more
    * than one, with the following algorithm:
    * <ol>
    * <li>Sort the internal list with respect to all values, using
    *    <code>Collections.sort</code>.
    * <li>Walk through the list of values, keeping track of the current value
    *    and the current value's frequency.
    * <li>Return the mode.
    * </ol>
    *
    * @return The statistical mode.
    * @see java.util.Collections#sort
    * @see ComparableComparator
    */
   @SuppressWarnings("unchecked")
   public Comparable terminate()
   {
      int numItems = myRecords.size();

      if (numItems == 0)
         return null;

      // Must sort it before determining the mode!
      Collections.sort(myRecords);

      Comparable mode = null;
      Comparable prev = null;
      int frequency = 0;
      int maxFrequency = 0;

      // Walk through the list.
      for (int i = 0; i < numItems; i++)
      {
         Comparable c = myRecords.get(i);
         if (prev != null && c.compareTo(prev) == 0)
         {
            frequency++;
         }
         else
         {
            if (frequency > maxFrequency)
            {
               maxFrequency = frequency;
               mode = prev;
            }
            frequency = 1;
            prev = c;
         }
      }
      // Account for last run.
      if (frequency > maxFrequency)
      {
         mode = prev;
      }
      return mode;
   }
}
