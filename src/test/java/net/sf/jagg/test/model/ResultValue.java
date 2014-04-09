package net.sf.jagg.test.model;

/**
 * Tests finding a property name with an "is" method.
 *
 * @author Randy Gettman
 * @since 0.7.2
 */
public class ResultValue
{
   private boolean amIControl;
   private double myValue;

   /**
    * Constructs a <code>TestValue</code>.
    * @param control Some boolean value.
    * @param value Some double value.
    */
   public ResultValue(boolean control, double value)
   {
      amIControl = control;
      myValue = value;
   }

   /**
    * Returns the boolean control value.
    * @return The boolean control value.
    */
   public boolean isControl()
   {
      return amIControl;
   }

   /**
    * Sets the boolean control value.
    * @param control The boolean control value.
    */
   public void setControl(boolean control)
   {
      amIControl = control;
   }

   /**
    * Returns the double value.
    * @return The double value.
    */
   public double getValue()
   {
      return myValue;
   }

   /**
    * Sets the double value.
    * @param value The double value.
    */
   public void setValue(double value)
   {
      myValue = value;
   }
}
