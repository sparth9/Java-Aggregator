package net.sf.jagg.test;

import org.junit.Test;
import static org.junit.Assert.*;

import net.sf.jagg.DoubleDouble;

/**
 * Tests the <code>DoubleDouble</code> class.
 *
 * @author Randy Gettman
 * @since 0.4.0
 */
public class DoubleDoubleTest
{
   /**
    * Tests adding a <code>double</code>.
    */
   @Test
   public void testAddDouble()
   {
      // Basic
      DoubleDouble dd = new DoubleDouble(2);
      dd.addToSelf(4);
      assertEquals(6, dd.doubleValue(), Math.ulp(6));
      assertEquals(0, dd.getLow(), Double.MIN_VALUE);

      // Would cause loss of precision in a double.
      DoubleDouble big = new DoubleDouble((1L << 52) + 1);
      big.addToSelf(1L << 52);
      double high = 1L << 53;
      double low = 1;
      assertEquals(high, big.doubleValue(), Math.ulp(high));
      assertEquals(low, big.getLow(), Math.ulp(low));

      big.addToSelf(-0.0625);
      assertEquals(high, big.doubleValue(), Math.ulp(high));
      assertEquals(0.9375, big.getLow(), Math.ulp(low));
   }

   /**
    * Tests adding a <code>DoubleDouble</code>.
    */
   @Test
   public void testAddDoubleDouble()
   {
      // Basic
      DoubleDouble dd = new DoubleDouble(5);
      dd.addToSelf(new DoubleDouble(7));
      assertEquals(12, dd.doubleValue(), Math.ulp(12));

      DoubleDouble x = new DoubleDouble(-72);
      DoubleDouble y = new DoubleDouble(72);
      x.addToSelf(y);
      assertEquals(0, x.doubleValue(), Math.ulp(72));

      // Would cause loss of precision in a double.
      DoubleDouble dd2 = new DoubleDouble(1L << 52);
      dd2.addToSelf(new DoubleDouble(0.6));
      DoubleDouble dd3 = new DoubleDouble(1L << 52);
      dd3.addToSelf(new DoubleDouble(0.7));
      dd2.addToSelf(dd3);
      double high = (1L << 53) + 2;  // Rounds up to next ulp, low goes negative.
      assertEquals(high, dd2.doubleValue(), Math.ulp(high));  // Ulp is 2 here.
      assertEquals(-0.7, dd2.getLow(), Math.ulp(-0.7));

      DoubleDouble dd4 = new DoubleDouble((1L << 53) + 2);
      dd2.subtractFromSelf(dd4);
      assertEquals(-0.7, dd2.doubleValue(), Math.ulp(-0.7));
      assertEquals(0, dd2.getLow(), Double.MIN_VALUE);
   }

   /**
    * Tests the negation feature.
    */
   @Test
   public void testNegate()
   {
      DoubleDouble dd = new DoubleDouble(1L << 53);
      dd.addToSelf(0.3125);
      dd.negateSelf();

      double high = -(1L << 53);
      double low = -0.3125;
      assertEquals(high, dd.doubleValue(), Math.ulp(high));
      assertEquals(low, dd.getLow(), Math.ulp(low));

      DoubleDouble ddNaN = new DoubleDouble(Double.NaN);
      ddNaN.negateSelf();
      assertTrue(ddNaN.isNaN());
   }

   /**
    * Tests multiplication by a <code>double</code>.
    */
   @Test
   public void testMultiplyByDouble()
   {
      // Basic
      DoubleDouble dd = new DoubleDouble(11);
      dd.multiplySelfBy(9);
      assertEquals(99, dd.doubleValue(), Math.ulp(99));
      assertEquals(0, dd.getLow(), Double.MIN_VALUE);

      // Would cause loss of precision in a double.
      DoubleDouble big = new DoubleDouble((1L << 52) + 1);
      big.multiplySelfBy(1.1);
      double high = (1L << 52) * 1.1 + 1;
      double low = 0.1;
      assertEquals(high, big.doubleValue(), Math.ulp(high));
      assertEquals(low, big.getLow(), low * TestUtility.DELTA);
   }

   /**
    * Tests multiplication by a <code>DoubleDouble</code>.
    */
   @Test
   public void testMultiplyByDoubleDouble()
   {
      // Basic
      DoubleDouble dd = new DoubleDouble(11);
      dd.multiplySelfBy(new DoubleDouble(23));
      assertEquals(253, dd.doubleValue(), Double.MIN_VALUE);
      assertEquals(0, dd.getLow(), Double.MIN_VALUE);

      // Would cause loss of precision in a double.
      DoubleDouble dd2 = new DoubleDouble(1L << 52);
      dd2.addToSelf(0.9);
      DoubleDouble dd3 = new DoubleDouble(1L << 52);
      dd3.addToSelf(0.8);
      // "Ideal" result is 1L << 104 + 1.7 * 1L << 52 + 0.72.
      dd2.multiplySelfBy(dd3);
      double high = 1L << 52;
      high *= high;
      high += (2L << 52);  // ulp in high is 1L << 52 (2^52).
      double low = -(0.3 * (1L << 52)) + 1;  // ulp in low is 1.
      assertEquals(high, dd2.doubleValue(), Math.ulp(high));
      assertEquals(low, dd2.getLow(), Math.ulp(low));
   }

   /**
    * Tests division by a <code>double</code>.
    */
   @Test
   public void testDivideByDouble()
   {
      // Basic
      DoubleDouble dd = new DoubleDouble(72);
      dd.divideSelfBy(9);
      assertEquals(8, dd.doubleValue(), Double.MIN_VALUE);
      assertEquals(0, dd.getLow(), Double.MIN_VALUE);

      // Would cause loss of precision in a double.
      DoubleDouble dd3 = new DoubleDouble(1L << 52);
      dd3.addToSelf(2);
      // Note: This isn't exactly 1.1, it's exactly 1.1000000000000000888178419700125.
      dd3.divideSelfBy(1.1);
      // (2^52 + 2) / 1.1 would be ....................... 4094181479427725.4545454545454545... .
      // (2^52 + 2) / 1.1000000000000000888178419700125 is 4094181479427725.1239669421487602.
      double high = (double) (1L << 52) / 1.1 + 2;
      double low = 0.1239669421487602;
      assertEquals(high, dd3.doubleValue(), Math.ulp(high));
      assertEquals(low, dd3.getLow(), Math.ulp(low));
   }

   /**
    * Tests division by a <code>DoubleDouble</code>.
    */
   @Test
   public void testDivideByDoubleDouble()
   {
      // Basic
      DoubleDouble dd = new DoubleDouble(72);
      DoubleDouble dd2 = new DoubleDouble(9);
      dd.divideSelfBy(dd2);
      assertEquals(8, dd.doubleValue(), Double.MIN_VALUE);
      assertEquals(0, dd.getLow(), Double.MIN_VALUE);

      // Would cause loss of precision in a double.
      DoubleDouble dd3 = new DoubleDouble(1L << 52);
      dd3.addToSelf(2);
      DoubleDouble dd4 = new DoubleDouble(1.1);
      dd4.addToSelf(-8.881784197001253e-17);
      // Note: This STILL isn't exactly 1.1, but it's a lot closer.
      dd3.divideSelfBy(dd4);
      // (2^52 + 2) / 1.1 is 4094181479427725.4545454545454545... .
      double high = (double) (1L << 52) / 1.1 + 2;
      double low = -1.0 / 22.0;
      assertEquals(high, dd3.doubleValue(), Math.ulp(high));
      assertEquals(low, dd3.getLow(), Math.abs(TestUtility.DELTA * low));
   }

   /**
    * Tests squaring a <code>DoubleDouble</code>.
    */
   @Test
   public void testSquare()
   {
      // Basic
      DoubleDouble dd = new DoubleDouble(23);
      dd.squareSelf();
      assertEquals(529, dd.doubleValue(), Double.MIN_VALUE);

      // Would cause loss of precision in a double.
      DoubleDouble dd2 = new DoubleDouble(1L << 52);
      dd2.addToSelf(1.8);
      dd2.squareSelf();
      // (2^52 + 1.8)^2 = 2^104 + 3.6 * 2^52 + 3.24.
      // An ulp at 2^104 is 2^52.
      double high = (double) (1L << 52) * (1L << 52) + 4 * (double) (1L << 52);
      double low = 3.24 - (0.4 * (1L << 52));

      assertEquals(high, dd2.doubleValue(), Math.ulp(high));
      assertEquals(low, dd2.getLow(), Math.abs(TestUtility.DELTA * (low)));
   }

   /**
    * Tests taking the square root of a <code>DoubleDouble</code>.
    */
   @Test
   public void testSqrt()
   {
      // Basic
      DoubleDouble dd = new DoubleDouble(1002001);
      dd.sqrtSelf();
      assertEquals(1001, dd.doubleValue(), Double.MIN_VALUE);

      // Would cause loss of precision in a double.
      DoubleDouble dd2 = new DoubleDouble(1.000030517578125);  // 1 + 2^-15
      dd2.sqrtSelf();
      double high = 1.0000152586726490;
      double low = -3.3880594123701711e-20;
      assertEquals(high, dd2.doubleValue(), Double.MIN_VALUE);
      assertEquals(low, dd2.getLow(), Math.abs(TestUtility.DELTA * (low)));
   }

   /**
    * Tests raising to an integer power.
    */
   @Test
   public void testPow()
   {
      // Basic
      DoubleDouble dd = new DoubleDouble(8.6);
      dd.powSelf(0);
      assertEquals(1, dd.doubleValue(), Double.MIN_VALUE);
      assertEquals(0, dd.getLow(), Double.MIN_VALUE);

      DoubleDouble dd1 = new DoubleDouble(5);
      dd1.powSelf(1);
      assertEquals(5, dd1.doubleValue(), Double.MIN_VALUE);
      assertEquals(0, dd1.getLow(), Double.MIN_VALUE);

      DoubleDouble dd2 = new DoubleDouble(11);
      dd2.powSelf(2);
      assertEquals(121, dd2.doubleValue(), Double.MIN_VALUE);
      assertEquals(0, dd2.getLow(), Double.MIN_VALUE);

      DoubleDouble ddM1 = new DoubleDouble(8);
      ddM1.powSelf(-1);
      assertEquals(0.125, ddM1.doubleValue(), Double.MIN_VALUE);
      assertEquals(0, ddM1.getLow(), Double.MIN_VALUE);

      DoubleDouble ddM2 = new DoubleDouble(4);
      ddM2.powSelf(-2);
      assertEquals(0.0625, ddM2.doubleValue(), Double.MIN_VALUE);
      assertEquals(0, ddM2.getLow(), Double.MIN_VALUE);

      DoubleDouble dd10 = new DoubleDouble(2);
      dd10.powSelf(10);
      assertEquals(1024, dd10.doubleValue(), Double.MIN_VALUE);
      assertEquals(0, dd10.getLow(), Double.MIN_VALUE);

      DoubleDouble dd21 = new DoubleDouble(3);
      dd21.powSelf(21);
      assertEquals(10460353203L, dd21.doubleValue(), Double.MIN_VALUE);
      assertEquals(0, dd21.getLow(), Double.MIN_VALUE);
   }

   /**
    * Tests the nth root.
    */
   @Test
   public void testNthRoot()
   {
      // Basic
      DoubleDouble dd = new DoubleDouble(3.14);
      dd.nthRootSelf(1);
      assertEquals(3.14, dd.doubleValue(), Double.MIN_VALUE);

      DoubleDouble dd2 = new DoubleDouble(8);
      dd2.nthRootSelf(3);
      assertEquals(2, dd2.doubleValue(), Double.MIN_VALUE);

      DoubleDouble dd3 = new DoubleDouble(2);
      dd3.nthRootSelf(12);
      double high = 1.0594630943592953;
      double low = -4.5281280019993407e-17;
      assertEquals(high, dd3.doubleValue(), Double.MIN_VALUE);
      assertEquals(low, dd3.getLow(), Math.abs(TestUtility.DELTA * low));
   }
}
