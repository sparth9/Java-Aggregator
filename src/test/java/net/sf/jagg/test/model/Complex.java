package net.sf.jagg.test.model;

/**
 * Used solely to test a custom <code>Aggregator</code>.
 *
 * @author Randy Gettman
 * @since 0.3.0
 */
public class Complex
{
   private double myReal;
   private double myImaginary;

   /**
    * Creates the complex number 0 (+ 0i).
    */
   public Complex()
   {
      reset();
   }

   /**
    * Creates a complex number.
    * @param real The real part.
    * @param imaginary The imaginary part.
    */
   public Complex(double real, double imaginary)
   {
      myReal = real;
      myImaginary = imaginary;
   }

   /**
    * Adds a <code>Complex</code> number to this one.
    * @param other The other <code>Complex</code> number.
    * @return A new <code>Complex</code> number representing the sum.
    */
   public Complex add(Complex other)
   {
      return new Complex(myReal + other.myReal, myImaginary + other.myImaginary);
   }

   /**
    * Adds a real number to this one.
    * @param d A real number.
    * @return A new <code>Complex</code> number representing the sum.
    */
   public Complex add(double d)
   {
      return new Complex(myReal + d, myImaginary);
   }

   /**
    * Subtracts a <code>Complex</code> number from this one.
    * @param other The other <code>Complex</code> number.
    * @return A new <code>Complex</code> number representing the difference.
    */
   public Complex subtract(Complex other)
   {
      return new Complex(myReal - other.myReal, myImaginary - other.myImaginary);
   }

   /**
    * Subtracts a real number from this one.
    * @param d A real number.
    * @return A new <code>Complex</code> number representing the difference.
    */
   public Complex subtract(double d)
   {
      return new Complex(myReal - d, myImaginary);
   }

   /**
    * Sets this complex number to 0 (+0i).
    */
   public void reset()
   {
      myReal = 0;
      myImaginary = 0;
   }

   /**
    * Determines whether this <code>Complex</code> is equal to another object,
    * usually another <code>Complex</code>.
    * @param other Another object.
    * @return <code>true</code> if both real and imaginary parts compare equal,
    *    <code>false</code> otherwise.
    */
   public boolean equals(Object other)
   {
      if (other instanceof Complex)
      {
         Complex c = (Complex) other;
         return myReal == c.myReal && myImaginary == c.myImaginary;
      }
      return false;
   }

   /**
    * String representation.
    * @return The string representation.
    */
   public String toString()
   {
      StringBuffer buf = new StringBuffer();
      if (myReal != 0)
      {
         buf.append(myReal);
         if (myImaginary < 0)
         {
            buf.append(" - ");
            buf.append(-myImaginary);
            buf.append("i");
         }
         else if (myImaginary > 0)
         {
            buf.append(" + ");
            buf.append(myImaginary);
            buf.append("i");
         }
      }
      else if (myImaginary != 0)
      {
         buf.append(myImaginary);
         buf.append("i");
      }
      else
      {
         buf.append("0");
      }
      return buf.toString();
   }
}
