package net.sf.jagg.test;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.*;

import net.sf.jagg.PropertyParser;
import net.sf.jagg.AggregateValue;
import net.sf.jagg.Aggregation;
import net.sf.jagg.Aggregator;
import net.sf.jagg.AvgAggregator;
import net.sf.jagg.test.model.ResultValue;

/**
 * Tests the <code>PropertyParser</code>.
 *
 * @author Randy Gettman
 * @since 0.3.0
 */
public class PropertyParserTest
{
   /**
    * Tests simple detection of a property.
    */
   @Test
   public void testProperty()
   {
      PropertyParser parser = new PropertyParser();
      parser.setPropertyText("property");
      parser.parse();

      boolean isMethod = parser.isMethod();
      String propertyName = parser.getPropertyName();

      assertFalse(isMethod);
      assertEquals("property", propertyName);
   }

   /**
    * Tests to ensure an <code>IllegalArgumentException</code> is thrown with a
    * property that is an empty string.
    */
   @Test(expected = IllegalArgumentException.class)
   public void testMissingProperty()
   {
      PropertyParser parser = new PropertyParser();
      parser.setPropertyText("");
      parser.parse();
   }

   /**
    * Tests to ensure an <code>IllegalArgumentException</code> is thrown with a
    * property that is a method call without a right parenthesis.
    */
   @Test(expected = IllegalArgumentException.class)
   public void testMissingRightParen()
   {
      PropertyParser parser = new PropertyParser();
      parser.setPropertyText("missingParen1(");
      parser.parse();
   }

   /**
    * Tests to ensure an <code>IllegalArgumentException</code> is thrown with a
    * property that is a method call without a left parenthesis.
    */
   @Test(expected = IllegalArgumentException.class)
   public void testMissingLeftParen()
   {
      PropertyParser parser = new PropertyParser();
      parser.setPropertyText("missingParen2)");
      parser.parse();
   }

   /**
    * Tests to ensure an <code>IllegalArgumentException</code> is thrown with a
    * property that is a method call with extra input on the end.
    */
   @Test(expected = IllegalArgumentException.class)
   public void testExtraInput()
   {
      PropertyParser parser = new PropertyParser();
      parser.setPropertyText("extra(input)given");
      parser.parse();
   }

   /**
    * Tests to ensure an <code>IllegalArgumentException</code> is thrown with a
    * property that is a method call without a method name.
    */
   @Test(expected = IllegalArgumentException.class)
   public void testMissingMethodName()
   {
      PropertyParser parser = new PropertyParser();
      parser.setPropertyText("(no, name)");
      parser.parse();
   }

   /**
    * Tests simple detection of a method with no params.
    */
   @Test
   public void testMethodNoParams()
   {
      PropertyParser parser = new PropertyParser();
      parser.setPropertyText("method()");
      parser.parse();

      boolean isMethod = parser.isMethod();
      String methodName = parser.getPropertyName();
      List<Object> params = parser.getParameters();

      assertTrue(isMethod);
      assertEquals("method", methodName);
      assertEquals(0, params.size());
   }

   /**
    * Tests detection of the boolean parameter <code>true</code>.
    */
   @Test
   public void testMethodOneBoolParamTrue()
   {
      PropertyParser parser = new PropertyParser();
      parser.setPropertyText("method(true)");
      parser.parse();

      boolean isMethod = parser.isMethod();
      String methodName = parser.getPropertyName();
      List<Object> params = parser.getParameters();

      assertTrue(isMethod);
      assertEquals("method", methodName);
      assertEquals(1, params.size());

      Object param = params.get(0);
      assertEquals(Boolean.class, param.getClass());
      assertEquals(true, param);
   }



   /**
    * Tests detection of the boolean parameter <code>false</code>.
    */
   @Test
   public void testMethodOneBoolParamFalse()
   {
      PropertyParser parser = new PropertyParser();
      parser.setPropertyText("method(false)");
      parser.parse();

      boolean isMethod = parser.isMethod();
      String methodName = parser.getPropertyName();
      List<Object> params = parser.getParameters();

      assertTrue(isMethod);
      assertEquals("method", methodName);
      assertEquals(1, params.size());

      Object param = params.get(0);
      assertEquals(Boolean.class, param.getClass());
      assertEquals(false, param);
   }

   /**
    * Tests detection of a <code>String</code> parameter plus removal of
    * whitespace.
    */
   @Test
   public void testMethodOneStringParamWhitespace()
   {
      PropertyParser parser = new PropertyParser();
      parser.setPropertyText("method(   whitespace   )");
      parser.parse();

      boolean isMethod = parser.isMethod();
      String methodName = parser.getPropertyName();
      List<Object> params = parser.getParameters();

      assertTrue(isMethod);
      assertEquals("method", methodName);
      assertEquals(1, params.size());

      Object param = params.get(0);
      assertEquals(String.class, param.getClass());
      assertEquals("whitespace", param);
   }

   /**
    * Tests multiple parameters, both <code>Strings</code>, one quoted.
    */
   @Test
   public void testMethodTwoParamsOneQuoted()
   {
      PropertyParser parser = new PropertyParser();
      parser.setPropertyText("method(multiple, \"parameters\")");
      parser.parse();

      boolean isMethod = parser.isMethod();
      String methodName = parser.getPropertyName();
      List<Object> params = parser.getParameters();

      assertTrue(isMethod);
      assertEquals("method", methodName);
      assertEquals(2, params.size());

      Object param = params.get(0);
      assertEquals(String.class, param.getClass());
      assertEquals("multiple", param);

      param = params.get(1);
      assertEquals(String.class, param.getClass());
      assertEquals("parameters", param);
   }

   /**
    * Tests with lots of parameters.
    */
   @Test
   public void testMethodManyParams()
   {
      PropertyParser parser = new PropertyParser();
      parser.setPropertyText("method(lots, of ,parameters , are   ,  \" given \")");
      parser.parse();

      boolean isMethod = parser.isMethod();
      String methodName = parser.getPropertyName();
      List<Object> params = parser.getParameters();

      assertTrue(isMethod);
      assertEquals("method", methodName);
      assertEquals(5, params.size());

      List<String> values = Arrays.asList("lots", "of", "parameters", "are", " given ");

      for (int i = 0; i < params.size(); i++)
      {
         Object param = params.get(i);
         assertEquals(String.class, param.getClass());
         assertEquals(values.get(i), param);
      }
   }

   /**
    * Tests numeric parameters of all types given.
    */
   @Test
   public void testNumerics()
   {
      PropertyParser parser = new PropertyParser();
      parser.setPropertyText(
         "numerics(1, 3.141592653589, 710, 2789039087316897013, 18903745890789013489015712341802348913456134, -2.718281828459, 100000, 789023746120901234091623416276187837483673684138673685447168736924.890713789023389016123)");
      parser.parse();

      boolean isMethod = parser.isMethod();
      String methodName = parser.getPropertyName();
      List<Object> params = parser.getParameters();

      assertTrue(isMethod);
      assertEquals("numerics", methodName);
      assertEquals(8, params.size());

      List<Class> classes = new ArrayList<Class>();
      classes.add(Byte.class);
      classes.add(Double.class);
      classes.add(Short.class);
      classes.add(Long.class);
      classes.add(BigInteger.class);
      classes.add(Double.class);
      classes.add(Integer.class);
      classes.add(Double.class);
      List<Number> values = new ArrayList<Number>();
      values.addAll(Arrays.asList((byte) 1, 3.141592653589, (short) 710, 2789039087316897013L,
         new BigInteger("18903745890789013489015712341802348913456134"), -2.718281828459, 100000,
         789023746120901234091623416276187837483673684138673685447168736924.890713789023389016123));

      for (int i = 0; i < params.size(); i++)
      {
         Object param = params.get(i);
         assertEquals(classes.get(i), param.getClass());
         assertEquals(values.get(i), param);
      }
   }

   /**
    * Tests that strings quoted with single quotes or double quotes are strings
    * and not other things like numbers or booleans.
    */
   @Test
   public void testQuoted()
   {
      PropertyParser parser = new PropertyParser();
      parser.setPropertyText("quoted('string', '14.5', 'seventeen', 'false', \"-23\")");
      parser.parse();

      boolean isMethod = parser.isMethod();
      String methodName = parser.getPropertyName();
      List<Object> params = parser.getParameters();

      assertTrue(isMethod);
      assertEquals("quoted", methodName);
      assertEquals(5, params.size());

      List<String> values = Arrays.asList("string", "14.5", "seventeen", "false", "-23");

      for (int i = 0; i < params.size(); i++)
      {
         Object param = params.get(i);
         assertEquals(String.class, param.getClass());
         assertEquals(values.get(i), param);
      }
   }

   /**
    * Tests that empty strings and null literals are treated properly.
    */
   @Test
   public void testNullAndEmpty()
   {
      PropertyParser parser = new PropertyParser();
      parser.setPropertyText("quoted(\"\", '', null)");
      parser.parse();

      boolean isMethod = parser.isMethod();
      String methodName = parser.getPropertyName();
      List<Object> params = parser.getParameters();

      assertTrue(isMethod);
      assertEquals("quoted", methodName);
      assertEquals(3, params.size());

      List<Class> classes = new ArrayList<Class>();
      classes.add(String.class);
      classes.add(String.class);
      classes.add(null);
      List<String> values = Arrays.asList("", "", null);

      for (int i = 0; i < params.size(); i++)
      {
         Object param = params.get(i);
         assertEquals(classes.get(i), (param == null) ? null : param.getClass());
         assertEquals(values.get(i), param);
      }
   }

   /**
    * Tests pickup of "isProperty" method when given a property "property".
    * @since 0.7.2
    */
   @Test
   public void testIsMethod()
   {
      List<ResultValue> resultValues = new ArrayList<ResultValue>();
      resultValues.add(new ResultValue(false, 0.1));
      resultValues.add(new ResultValue(false, 0.3));
      resultValues.add(new ResultValue(true, 0.6));
      resultValues.add(new ResultValue(true, 1.0));

      List<String> properties = Arrays.asList("control");

      Aggregator agg1 = new AvgAggregator("value");
      List<Aggregator> aggs = Arrays.asList(agg1);

      Aggregation agg = new Aggregation.Builder().setProperties(properties)
         .setAggregators(aggs).build();

      List<AggregateValue<ResultValue>> aggValues = agg.groupBy(resultValues);

      assertEquals(2, aggValues.size());

      List<Double> expectedValues = Arrays.asList(0.2, 0.8);
      for (int i = 0; i < 2; i++)
      {
         AggregateValue<ResultValue> aggValue = aggValues.get(i);
         assertEquals(expectedValues.get(i), (Double) aggValue.getAggregateValue(0), Math.ulp(expectedValues.get(i)));
      }
   }
}
