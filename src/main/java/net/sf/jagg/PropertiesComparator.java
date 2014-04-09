package net.sf.jagg;

import java.util.Comparator;
import java.util.List;

/**
 * This class represents a flexible <code>Comparator</code> that is capable of
 * comparing two objects based on a dynamic list of properties of the objects
 * of type <code>T</code>.  This class is used internally by
 * <code>Aggregation</code> to sort a <code>List&lt;T&gt;</code> according to
 * a specified list of properties.
 *
 * @author Randy Gettman
 * @since 0.1.0
 */
public class PropertiesComparator<T> implements Comparator<T>
{
   private List<String> myProperties;
   private int mySize;

   /**
    * Construct a <code>PropertiesComparator</code> that pays attention to the
    * given <code>List</code> of properties in the generic type <code>T</code>.
    * All properties must be <code>Comparable</code>.
    *
    * @param properties A <code>List&lt;String&gt;</code> of properties.
    */
   public PropertiesComparator(List<String> properties)
   {
      myProperties = properties;
      mySize = properties.size();
   }

   /**
    * <p>Compares the given objects to determine order.  Fulfills the
    * <code>Comparator</code> contract by returning a negative integer, 0, or a
    * positive integer if <code>o1</code> is less than, equal to, or greater
    * than <code>o2</code>.</p>
    * <p>Nulls properties compare equal to each other, and a null property
    * compares greater than a non-null property</code>.
    *
    * @param o1 The left-hand-side object to compare.
    * @param o2 The right-hand-side object to compare.
    * @return A negative integer, 0, or a positive integer if <code>o1</code>
    *    is less than, equal to, or greater than <code>o2</code>.
    * @throws UnsupportedOperationException If any property specified in the
    *    constructor doesn't correspond to a no-argument "get&lt;Property&gt;"
    *    getter method in <code>T</code>, or if the property's type is not
    *    <code>Comparable</code>.
    */
   @SuppressWarnings("unchecked")
   public int compare(T o1, T o2) throws UnsupportedOperationException
   {
      int comp;
      for (int i = 0; i < mySize; i++)
      {
         String property = myProperties.get(i);
         Comparable value1 = (Comparable)
            Aggregator.getValueFromProperty(o1, property);
         Comparable value2 = (Comparable)
            Aggregator.getValueFromProperty(o2, property);
         try
         {
            if (value1 == null)
            {
               if (value2 == null)
                  comp = 0;
               else
                  comp = 1;
            }
            else
            {
               if (value2 == null)
                  comp = -1;
               else
                  comp = value1.compareTo(value2);
            }
            if (comp != 0) return comp;
         }
         catch (ClassCastException e)
         {
            throw new UnsupportedOperationException("Property \"" + property + "\" needs to be Comparable.");
         }
      }
      return 0;
   }

   /**
    * Indicates whether the given <code>PropertiesComparator</code> is equal to
    * this <code>PropertiesComparator</code>.  All property names must match in
    * order.
    *
    * @param obj The other <code>PropertiesComparator</code>.
    */
   public boolean equals(Object obj)
   {
      if (obj instanceof PropertiesComparator)
      {
         PropertiesComparator otherComp = (PropertiesComparator) obj;
         if (mySize != otherComp.mySize)
            return false;
         for (int i = 0; i < mySize; i++)
         {
            if (!myProperties.get(i).equals(otherComp.myProperties.get(i)))
               return false;
         }
         return true;
      }
      return false;
   }
}
