package net.sf.jagg;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * This abstract class allows for the state necessary to implement aggregate
 * functions.  Subclasses define the following steps of the Aggregation
 * algorithm:
 * <ol>
 * <li>Initialization, with the <code>init</code> method.  This initializes
 *    the state of the Aggregator.  <code>Aggregators</code> may be reused, so
 *    this method must be prepared to instantiate or reset any state objects it
 *    maintains.
 * <li>Iteration, with the <code>iterate</code> method.  This adds a value to
 *    the aggregation.  This will be called exactly once per object to
 *    aggregate.
 * <li>Merging, with the <code>merge</code> method.  In parallel execution,
 *    this merges results from two <code>Aggregator</code> objects resulting
 *    from parallel execution.  After the <code>merge</code> method completes,
 *    then this <code>Aggregator</code> reflects the combined state of both
 *    this <code>Aggregator</code> and another <code>Aggregator</code>. Merging
 *    takes place during parallel execution and during super aggregation
 *    (rollups, cubes, and grouping sets).
 * <li>Termination, with the <code>terminate</code> method.  At this point, all
 *    aggregation is complete, and only a final result needs to be constructed.
 * </ol>
 *
 * <p>The factory method <code>getAggregator</code> creates
 * <code>Aggregators</code> and marks them as in use.  They are stored in a
 * cache (a <code>HashMap</code>) so they may be reused.  After an
 * <code>Aggregator</code> is used, it will be marked as not in use; it remains
 * in the cache and it may be reused.</p>
 *
 * <p>The abstract method <code>replicate</code> must be defined for every
 * <code>Aggregator</code>.  This method returns an uninitialized copy of the
 * <code>Aggregator</code>, with the same type and the same properties to
 * analyze as the original <code>Aggregator</code>.</p>
 *
 * <p>The concrete method <code>terminateDoubleDouble</code> may be overridden
 * by <code>Aggregators</code> that operate on floating-point numbers.  This
 * allows other <code>Aggregators</code> to use the high-precision result, a
 * <code>DoubleDouble</code>, internally in their calculations.  The default
 * implementation simply returns <code>DoubleDouble.NaN</code>.</p>
 *
 * <p>Currently, <code>Aggregators</code> do not need to be thread-safe.  The
 * <code>Aggregation</code> class is the only class that uses
 * <code>Aggregators</code>, and only one <code>Thread</code> at a time uses
 * any <code>Aggregator</code>.</p>
 *
 * <p>However, internally, the <code>Aggregator</code> class uses synchronized
 * access to internal <code>HashMaps</code> to cache <code>Aggregators</code>
 * (and <code>Methods</code>).</p>
 *
 * <p>The {@link #getValueFromProperty(Object, String) getValueProperty} method
 * has been made <code>public</code> as of version 0.7.2.</p>
 *
 * @see DoubleDouble
 *
 * @author Randy Gettman
 * @since 0.1.0
 */
public abstract class Aggregator
{
   /**
    * Special pseudo-property indicating that the object itself is to be
    * aggregated, instead of a property of the object.
    */
   public static final String PROP_SELF = ".";

   // Cache Method objects to save on instantiation/garbage collection costs.
   private static final MethodCache myMethodCache = MethodCache.getMethodCache();
   // Cache Aggregator objects to save on instantiation/garbage collection
   // costs.  Key is "nameAndProperty".
   private static final AggregatorCache myAggregatorCache = AggregatorCache.getAggregatorCache();

   private String myProperty;
   private boolean amIInUse = false;

   /**
    * Default constructor is protected so that only subclasses of
    * <code>Aggregator</code> can be instantiated.
    */
   protected Aggregator() {}

   /**
    * Adds the given <code>Aggregator</code> to an internal cache.  If it's not
    * in use, then it marks it as "in use" and returns it.  Else, it searches
    * the cache for an <code>Aggregator</code> that matches the given
    * <code>Aggregator</code> and is not already in use.  If none exist in the
    * cache, then it replicates the given <code>Aggregator</code>, adds it to
    * the cache, and returns it.
    *
    * @param archetype The <code>Aggregator</code> whose properties (and type)
    *    need to be matched.
    * @return A matching <code>Aggregator</code> object.  It could be
    *    <code>archetype</code> itself if it's not already in use, or it could
    *    be <code>null</code> if <code>archetype</code> was null.
    */
   public static Aggregator getAggregator(Aggregator archetype)
   {
      return myAggregatorCache.getAggregator(archetype);
   }

   /**
    * Adds the given <code>Aggregator</code> to an internal cache.  Does not
    * mark it as in use.  Does not add it to the internal cache.  This is meant
    * to aid the caller in creating an <code>Aggregator</code> based on the
    * following specification string format:
    * <code>aggName(property/-ies)</code>.
    * This assumes that the desired <code>Aggregator</code> has a one-argument
    * constructor with a <code>String</code> argument for its property or
    * properties.
    *
    * @param aggSpec The String specification of an <code>Aggregator</code>.
    * @return An <code>Aggregator</code> object.
    * @throws IllegalArgumentException If the aggregator specification was mal-
    *    formed.
    */
   public static Aggregator getAggregator(String aggSpec)
   {
      int leftParenIdx = aggSpec.indexOf("(");
      int rightParenIdx = aggSpec.lastIndexOf(")");
      if (leftParenIdx == -1 || rightParenIdx == -1 || leftParenIdx > rightParenIdx)
         throw new IllegalArgumentException("Malformed Aggregator specification: " + aggSpec);

      String aggName = aggSpec.substring(0, leftParenIdx);
      if (aggName.indexOf(".") == -1)
         aggName = Aggregator.class.getPackage().getName() + "." + aggName;
      if (!aggName.endsWith("Aggregator"))
         aggName = aggName + "Aggregator";
      String property = aggSpec.substring(leftParenIdx + 1, rightParenIdx);

      try
      {
         Class aggClass = Class.forName(aggName);
         Constructor ctor = aggClass.getConstructor(String.class);
         return (Aggregator) ctor.newInstance(property);
      }
      catch (ClassNotFoundException e)
      {
         throw new IllegalArgumentException("Unknown Aggregator class \"" + aggName + "\".", e);
      }
      catch (NoSuchMethodException e)
      {
         throw new IllegalArgumentException("Can't find constructor for Aggregator class \"" +
            aggName + "\" that contains exactly one String parameter.", e);
      }
      catch (InstantiationException e)
      {
         throw new IllegalArgumentException("Aggregator specified is not a concreted class: \"" +
            aggName + "\".", e);
      }
      catch (IllegalAccessException e)
      {
         throw new IllegalArgumentException("Unable to constructor Aggregator \"" +
            aggName + "\".", e);
      }
      catch (InvocationTargetException e)
      {
         throw new IllegalArgumentException("Exception caught instantiating Aggregator \"" +
            aggName + "\": " + e.getCause().getClass().getName(), e);
      }
      catch (ClassCastException e)
      {
         throw new IllegalArgumentException("Class found is not an Aggregator: \"" +
            aggName + "\".", e);
      }
   }
   
   /**
    * Gets a specific <code>Method</code> from an internal cache, or creates it
    * using reflection if it does not exist.  The method is looked up via the
    * name "get&lt;Property&gt;", with the given property name, on the given
    * value object.  Invokes the <code>Method</code> and returns the value.
    * This is expected to be called in the <code>iterate</code> method, so that
    * it can access the object's property, although it can be called from any
    * <code>Aggregator</code> method.  This method is here to standardize the
    * "bean" method naming convention specified by <code>Aggregator</code>
    * and to cache <code>Method</code> objects for internal use.
    *
    * @param value The object on which to lookup a property value.
    * @param property The property to lookup.
    * @return The object's property value.
    * @throws UnsupportedOperationException If the desired <code>Method</code>
    *    does not exist, if the <code>Method</code> cannot be invoked because
    *    of Java language access control (e.g. private, etc.), or if  the
    *    invoked <code>Method</code> throws an <code>Exception</code>.
    * @see #iterate
    */
   public static Object getValueFromProperty(Object value, String property)
   {
      try
      {
         return myMethodCache.getValueFromProperty(value, property);
      }
      catch (NoSuchMethodException e)
      {
         throw new UnsupportedOperationException("No matching method found for \"" +
            property + "\".", e);
      }
      catch (IllegalAccessException e)
      {
         throw new UnsupportedOperationException("Illegal method access detected for property \"" +
            property + "\".", e);
      }
      catch (InvocationTargetException e)
      {
         throw new UnsupportedOperationException("Exception detected getting property \"" +
            property + "\".", e);
      }
   }

   /**
    * Sets the property name.  Subclasses may override this method if they
    * want to extract more information from the property string, e.g.
    * "Name(property, addlInfo)".  The default implementation simply stores the
    * entire string to be made available via "getProperty".
    * 
    * @param property The property name.
    * @see #getProperty()
    */
   protected void setProperty(String property)
   {
      myProperty = property;
   }

   /**
    * Determines whether this <code>Aggregator</code> is in use.
    * @return A boolean indicating whether it's in use.
    */
   public final boolean isInUse()
   {
      return amIInUse;
   }

   /**
    * Sets whether this <code>Aggregator</code> is in use.
    * @param inUse The boolean indicating whether it's in use.
    */
   public final void setInUse(boolean inUse)
   {
      amIInUse = inUse;
   }

   /**
    * Returns an uninitialized copy of this <code>Aggregator</code> object,
    * with the same property(ies) to analyze.
    * @return An uninitialized copy of this <code>Aggregator</code> object.
    */
   public abstract Aggregator replicate();

   /**
    * Initializes the <code>Aggregator</code>.  Subclasses should override this
    * method to instantiate state objects that will hold the state of the
    * aggregation.  E.g., a "sum" aggregation will initialize a sum object to
    * zero.  This <code>Aggregator</code> may be reused, so any objects may
    * already be instantiated, but their state must be reset.
    */
   public abstract void init();

   /**
    * Processes the given value into the aggregation.  E.g., a "sum"
    * aggregation will add this object's property value to a sum object.  An
    * implementation will likely want to call <code>getValueFromProperty</code>,
    * which accesses a cache of <code>Methods</code> to find the property's
    * value in the given object.
    *
    * @param value The value to aggregate.
    * @see #getValueFromProperty
    */
   public abstract void iterate(Object value);

   /**
    * Merges the state of the given <code>Aggregator</code> into this own
    * <code>Aggregator</code>'s state.  Called when parallel execution
    * yields more than one <code>Aggregator</code> to combine into one.
    *
    * @param agg The <code>Aggregator</code> whose state needs to be merged
    *    into this one.
    */
   public abstract void merge(Aggregator agg);

   /**
    * At this point the aggregation of values is complete, and a final result
    * needs to be constructed.  This method constructs that final result.
    *
    * @return A value representing the result of the aggregation.
    */
   public abstract Object terminate();

   /**
    * Return the result as a <code>DoubleDouble</code>.  This is used mainly
    * when other <code>Aggregators</code> that use this result must maintain a
    * high precision.
    * @return A <code>DoubleDouble</code> representing the result of the
    *    aggregation.  The default implementation returns
    * <code>DoubleDouble.NaN</code>.
    * @see DoubleDouble
    * @since 0.4.0
    */
   public DoubleDouble terminateDoubleDouble()
   {
      return DoubleDouble.NaN;
   }

   /**
    * Determines whether the given <code>Aggregator</code> is equivalent to
    * this <code>Aggregator</code>.  This is necessary because
    * <code>Aggregator</code> objects will be stored in a <code>HashMap</code>.
    *
    * @param o Another <code>Aggregator</code>.
    * @return <code>true</code> if equivalent, <code>false</code> otherwise.
    */
   public boolean equals(Object o)
   {
      return (getClass().equals(o.getClass()) && toString().equals(o.toString()));
   }

   /**
    * Calculates a hash code for this <code>Aggregator</code>.  This is
    * necessary because <code>Aggregator</code> objects will be stored in a
    * <code>HashMap</code>.
    *
    * @return The hash code of this <code>Aggregator</code>.  It is computed by
    *    taking the hash of the result of the <code>toString</code> method.
    * @see #toString
    */
   public int hashCode()
   {
      return toString().hashCode();
   }

   /**
    * Retrieves the property that this <code>Aggregator</code> aggregates.
    *
    * @return A property name.
    */
   public String getProperty()
   {
      return myProperty;
   }

   /**
    * A String representation of this <code>Aggregator</code>, in the form
    * "className(property)".
    */
   public String toString()
   {
      return getClass().getName() + "(" + getProperty() + ")";
   }
}
