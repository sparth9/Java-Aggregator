package net.sf.jagg.test.model;

import java.util.Arrays;
import java.util.List;

import net.sf.jagg.msd.Discriminable;

/**
 * Just a random test class meant to demonstrate aggregations.
 *
 * @author Randy Gettman
 * @since 0.1.0
 */
public class Record implements Comparable<Record>, Discriminable
{
   private String myCategory1;
   private int    myCategory2;
   private String myCategory3;
   private String myCategory4;
   private int    myValue1;
   private double myValue2;

   /**
    * Creates a <code>Record</code> with the given values.
    * @param category1 The first category.
    * @param category2 The second category.
    * @param category3 The third category.
    * @param category4 The fourth category.
    * @param value1 The first value.
    * @param value2 The second value.
    */
   public Record(String category1, int category2, String category3, String category4,
      int value1, double value2)
   {
      myCategory1 = category1;
      myCategory2 = category2;
      myCategory3 = category3;
      myCategory4 = category4;
      myValue1 = value1;
      myValue2 = value2;
   }

   /**
    * Returns the first category.
    * @return The first category.
    */
   public String getCategory1()
   {
      return myCategory1;
   }

   /**
    * Sets the first category.
    * @param myCategory1 The first category.
    */
   public void setCategory1(String myCategory1)
   {
      this.myCategory1 = myCategory1;
   }

   /**
    * Returns the second category.
    * @return The second category.
    */
   public int getCategory2()
   {
      return myCategory2;
   }

   /**
    * Sets the second category.
    * @param myCategory2 The second category.
    */
   public void setCategory2(int myCategory2)
   {
      this.myCategory2 = myCategory2;
   }

   /**
    * Returns the third category.
    * @return The third category.
    */
   public String getCategory3()
   {
      return myCategory3;
   }

   /**
    * Sets the third category.
    * @param myCategory3 The third category.
    */
   public void setCategory3(String myCategory3)
   {
      this.myCategory3 = myCategory3;
   }

   /**
    * Returns the fourth category.
    * @return The fourth category.
    */
   public String getCategory4()
   {
      return myCategory4;
   }

   /**
    * Sets the fourth category.
    * @param myCategory4 The fourth category.
    */
   public void setCategory4(String myCategory4)
   {
      this.myCategory4 = myCategory4;
   }

   /**
    * Returns the first value.
    * @return The first value.
    */
   public int getValue1()
   {
      return myValue1;
   }

   /**
    * Sets the first value.
    * @param myValue1 The first value.
    */
   public void setValue1(int myValue1)
   {
      this.myValue1 = myValue1;
   }

   /**
    * Returns the second value.
    * @return The second value.
    */
   public double getValue2()
   {
      return myValue2;
   }

   /**
    * Sets the second value.
    * @param myValue2 The second value.
    */
   public void setValue2(double myValue2)
   {
      this.myValue2 = myValue2;
   }

   /**
    * Compares this <code>Record</code> to another.
    * @param other Another <code>Record</code>.
    * @return An integer less than zero, equal to zero, or greater than zero,
    *    if this is less than, equal to, or greater than the other
    *    <code>Record</code>.
    */
   public int compareTo(Record other)
   {
      int comp = myCategory1.compareTo(other.myCategory1);
      if (comp != 0) return comp;
      comp = myCategory2 - other.myCategory2;
      if (comp != 0) return comp;
      comp = myCategory3.compareTo(other.myCategory3);
      if (comp != 0) return comp;
      comp = myCategory4.compareTo(other.myCategory4);
      return comp;
   }

   /**
    * Gets the <code>List</code> of discriminable properties.
    * @return A <code>List</code> of discriminable properties.
    * @since 0.5.0
    */
   public List<String> getDiscriminableProperties()
   {
      return Arrays.asList("category1", "category2", "category3", "category4");
   }

   /**
    * Returns the string representation.
    * @return The string representation.
    */
   public String toString()
   {
      StringBuffer buf = new StringBuffer();
      buf.append("Record(");
      buf.append(myCategory1);
      buf.append(",");
      buf.append(myCategory2);
      buf.append(",");
      buf.append(myCategory3);
      buf.append(",");
      buf.append(myCategory4);
      buf.append(",");
      buf.append(myValue1);
      buf.append(",");
      buf.append(myValue2);
      buf.append(")");
      return buf.toString();
   }
}
