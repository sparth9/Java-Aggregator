package net.sf.jagg.test.model;

import java.util.HashMap;
import java.util.Set;

/**
 * This test class is used to test explicit method calling, including a
 * parameter that is an Enum.
 *
 * @author Randy Gettman
 * @since 0.1.0
 */
public class CountyData
{
   /**
    * The enumeration of month codes.
    */
   public enum MonthCode
   {
      CURR_MONTH,
      LAST_MONTH,
      YEAR_AGO_MONTH
   }

   /**
    * Metric Type ID for "Searches"
    */
   public static final int METRIC_TYPE_SRCHES = 4;
   /**
    * Metric Type ID for "Search Time"
    */
   public static final int METRIC_TYPE_SRCH_TIME = 15;
   /**
    * Metric Type ID for "Searches plus Reports Billed"
    */
   public static final int METRIC_TYPE_SRCHES_PLUS_BILLS = 25;
   /**
    * Metric Type ID for "Searches plus Search Time"
    */
   public static final int METRIC_TYPE_SRCHES_PLUS_SRCH_TIME = 30;

   private String myFipsCode;
   private String myStateCode;
   private String myCountyName;
   private HashMap<MonthMetricTypeIdPair, Double> myValues;

   /**
    * Default constructor initializes things to null/empty.
    */
   public CountyData()
   {
      myFipsCode = null;
      myStateCode = null;
      myCountyName = null;
      myValues = new HashMap<MonthMetricTypeIdPair, Double>();
   }

   // Getters.
   /**
    * Returns the FIPS code.
    * @return The FIPS code.
    */
   public String getFipsCode()
   {
      return myFipsCode;
   }

   /**
    * Returns the state code.
    * @return The state code.
    */
   public String getStateCode()
   {
      return myStateCode;
   }

   /**
    * Returns the county name.
    * @return The county name.
    */
   public String getCountyName()
   {
      return myCountyName;
   }

   /**
    * Returns the value corresponding to the given metric type ID.
    *
    * @param monthCode A MonthCode.
    * @param metricTypeId The metric type ID, key to the value.
    * @return The value, as a <code>double</code>.
    */
   public double getValue(MonthCode monthCode, int metricTypeId)
   {
      return myValues.get(new MonthMetricTypeIdPair(monthCode, metricTypeId));
   }

   /**
    * Convenience getter for number of searches in "current month".
    * @return The number of searches for the "current month".
    */
   public double getCurrMonthSearches()
   {
      return myValues.get(new MonthMetricTypeIdPair(MonthCode.CURR_MONTH, METRIC_TYPE_SRCHES));
   }

   /**
    * Convenience getter for total search time in "current month".
    * @return The total search time for the "current month".
    */
   public double getCurrMonthSearchTime()
   {
      return myValues.get(new MonthMetricTypeIdPair(MonthCode.CURR_MONTH, METRIC_TYPE_SRCH_TIME));
   }

   /**
    * Convenience getter for number of searches plus total search time in
    * "current month".
    * @return The number of searches plus total search time for the "current
    *    month".
    */
   public double getCurrMonthSearchesPlusSearchTime()
   {
      return myValues.get(new MonthMetricTypeIdPair(MonthCode.CURR_MONTH, METRIC_TYPE_SRCHES_PLUS_SRCH_TIME));
   }

   /**
    * Returns a <code>Set</code> of all keys that have been entered.
    * @return A <code>Set</code> of <code>Integer</code> keys.
    */
   public Set<MonthMetricTypeIdPair> getKeys()
   {
      return myValues.keySet();
   }

   // Setters.
   /**
    * Sets the FIPS code.
    * @param fipsCode The FIPS code.
    */
   public void setFipsCode(String fipsCode)
   {
      myFipsCode = fipsCode;
   }

   /**
    * Sets the state code.
    * @param stateCode The state code.
    */
   public void setStateCode(String stateCode)
   {
      myStateCode = stateCode;
   }

   /**
    * Sets the county name.
    * @param countyName The county name.
    */
   public void setCountyName(String countyName)
   {
      myCountyName = countyName;
   }

   /**
    * Sets the value corresponding to the given metric type ID.
    *
    * @param monthCode A MonthCode.
    * @param metricTypeId The metric type ID, key to the value.
    * @param value The value, as a <code>double</code>.
    */
   public void setValue(MonthCode monthCode, int metricTypeId, double value)
   {
      myValues.put(new MonthMetricTypeIdPair(monthCode, metricTypeId), value);
   }

   /**
    * This class is used as the key into the internal hash table of values.
    */
   public class MonthMetricTypeIdPair
   {
      private MonthCode myMonthCode;
      private int myMetricTypeId;

      /**
       * Construct a <code>MonthMetricTypeIdPair</code>.
       * @param monthCode The MonthCode, one of <code>CURR_MONTH</code>,
       *    <code>LAST_MONTH</code>, or <code>YEAR_AGO_MONTH</code>.
       * @param metricTypeId The metric type ID.
       */
      public MonthMetricTypeIdPair(MonthCode monthCode, int metricTypeId)
      {
         myMonthCode = monthCode;
         myMetricTypeId = metricTypeId;
      }

      /**
       * Returns the MonthCode.
       * @return The MonthCode.
       */
      public MonthCode getMonthCode()
      {
         return myMonthCode;
      }

      /**
       * Returns the metric type ID.
       * @return The metric type ID.
       */
      public int getMetricTypeId()
      {
         return myMetricTypeId;
      }

      /**
       * Returns a hash code.
       * @return A hash code.
       */
      public int hashCode()
      {
         return myMonthCode.hashCode() + myMetricTypeId;
      }

      public boolean equals(Object o)
      {
         if (o instanceof MonthMetricTypeIdPair)
         {
            MonthMetricTypeIdPair other = (MonthMetricTypeIdPair) o;
            return (myMonthCode == other.myMonthCode && myMetricTypeId == other.myMetricTypeId);
         }
         return false;
      }

   }
}

