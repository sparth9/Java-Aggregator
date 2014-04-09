package net.sf.jagg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created as a wrapper around a HashMap that maps aggregator specification
 * strings to <code>Lists</code> of <code>Aggregators</code>.
 *
 * @author Randy Gettman
 * @since 0.3.0
 */
public class AggregatorCache
{
   private static AggregatorCache theAggregatorCache = null;

   private final HashMap<String, List<Aggregator>> myAggregators;

   /**
    * Private constructor for the singleton pattern.
    */
   private AggregatorCache()
   {
      myAggregators = new HashMap<String, List<Aggregator>>();
   }

   /**
    * Returns the singleton <code>AggregatorCache</code>.
    * @return The singleton <code>AggregatorCache</code>.
    */
   public static AggregatorCache getAggregatorCache()
   {
      if (theAggregatorCache == null)
      {
         theAggregatorCache = new AggregatorCache();
      }
      return theAggregatorCache;
   }

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
   public Aggregator getAggregator(Aggregator archetype)
   {
      if (archetype == null)
         return null;

      List<Aggregator> aggsList;
      Aggregator agg = null;

//      long time1 = System.nanoTime();

      // Synchronize access to the cache so multiple Threads don't "find"
      // the same Aggregator.
      synchronized (myAggregators)
      {
         // Use the given Aggregator if it was not already in use.
         // This must be within a synchronized block so that the same archetype
         // is not chosen by multiple threads.  It is in THIS synchronized
         // block to avoid having to get multiple locks.
         if (!archetype.isInUse())
         {
            archetype.setInUse(true);
            agg = archetype;
         }

         // If we can't use the archetype itself...
         if (agg == null)
         {
            aggsList = myAggregators.get(archetype.toString());
            // If we have a list of aggregators matching the name and property.
            if (aggsList != null)
            {
               // Look for one that's not in use.
               int size = aggsList.size();
               for (int a = 0; a < size; a++)
               {
                  Aggregator candidate = aggsList.get(a);
                  if (!candidate.isInUse())
                  {
                     agg = candidate;
                     // Set as "in use" before coming out of synchronization.
                     agg.setInUse(true);
                     break;
                  }
               }

            }
            if (aggsList == null)
            {
               aggsList = new ArrayList<Aggregator>();
               myAggregators.put(archetype.toString(), aggsList);
            }

            // We must create another Aggregator and add it to the cache.
            // Only replicated Aggregators are added to the cache; archetypes
            // are not added to the cache.
            if (agg == null)
            {
               agg = archetype.replicate();

               // Set as in use before adding to the cache, so another Thread
               // won't pick up this one.
               agg.setInUse(true);
               aggsList.add(agg);
            }
         }
      }
//      long time10 = System.nanoTime();
//      System.out.println("AC timings: All of gA: " + (time10 - time1));
      return agg;
   }
}
