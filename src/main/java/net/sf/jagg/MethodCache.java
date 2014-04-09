package net.sf.jagg;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created as a wrapper around a <code>HashMap</code> that maps property names
 * to <code>Methods</code>.  Method calls with best matches for argument types
 * are found.  Property names, e.g. "property", are mapped to "getProperty()"
 * or "isProperty()" methods if found.
 *
 * @author Randy Gettman
 * @since 0.1.0
 */
public class MethodCache
{
   private static final boolean DEBUG = false;

   private static MethodCache theMethodCache = null;

   private final HashMap<String, MethodCall> myMethods;

   /**
    * Private constructor for the singleton pattern.
    */
   private MethodCache()
   {
      myMethods = new HashMap<String, MethodCall>();
   }

   /**
    * Returns the singleton <code>MethodCache</code>.
    * @return The singleton <code>MethodCache</code>.
    */
   public static MethodCache getMethodCache()
   {
      if (theMethodCache == null)
      {
         theMethodCache = new MethodCache();
      }
      return theMethodCache;
   }

   /**
    * Gets a specific <code>Method</code> from the cache, or finds it using
    * reflection if it does not exist.  Invokes the <code>Method</code> and
    * returns the value.
    *
    * @param value The object on which to lookup a property value.
    * @param property The property or method name plus signature to lookup.
    * @return The object's property value.
    * @throws NoSuchMethodException If a suitable <code>Method</code> couldn't
    *    be found.
    * @throws IllegalAccessException If the <code>Method</code> is inaccessible
    *    (private, etc.)
    * @throws InvocationTargetException If the <code>Method</code> throws an
    *    <code>Exception</code> during execution.
    */
   public Object getValueFromProperty(Object value, String property)
      throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
   {
      // Pseudo-property indicating to use the object itself, instead of a
      // property of the object.
      if (Aggregator.PROP_SELF.equals(property))
         return value;

      MethodCall methodCall = getMethodCallFromProperty(value, property);
      return methodCall.invoke(value);
   }

   /**
    * Gets a specific <code>MethodCall</code> from the cache, or creates it by
    * finding the <code>Method</code> using reflection if it does not exist.
    *
    * @param value The object on which to lookup a property value.
    * @param property The property or method name plus signature to lookup.
    * @return A <code>MethodCall</code>.
    * @throws NoSuchMethodException If a suitable <code>Method</code> couldn't
    *    be found.
    * @since 0.5.0
    */
   public MethodCall getMethodCallFromProperty(Object value, String property)
      throws NoSuchMethodException
   {
      MethodCall methodCall;
      String lookup = value.getClass().getName() + "." + property;
      Object[] parameterArray;
      // Synchronize access to the cache so multiple Threads don't "find"
      // the same Method.
      synchronized (myMethods)
      {
         methodCall = myMethods.get(lookup);
         // If not in cache...
         if (methodCall == null)
         {
            if (Aggregator.PROP_SELF.equals(property))
            {
               // Get a SelfMethodCall.
               methodCall = new SelfMethodCall(value);
            }
            else
            {
               // Get a MethodCall.
               PropertyParser parser = new PropertyParser(property);
               parser.parse();
               Method method;

               if (parser.isMethod())
               {
                  // Method with possible parameters.
                  String methodName = parser.getPropertyName();
                  if (DEBUG)
                     System.out.println("Method name: \"" + methodName + "\".");
                  List<Object> parameters = parser.getParameters();
                  Class<?>[] classes = new Class<?>[parameters.size()];
                  for (int i = 0; i < parameters.size(); i++)
                  {
                     Class theClass = parameters.get(i).getClass();
                     classes[i] = theClass;
                     if (DEBUG)
                        System.out.println("  Param Class: \"" + classes[i].getName() + "\".");
                  }
                  parameterArray = parameters.toArray();
                  // Hopefully the parameter types match EXACTLY.
                  try
                  {
                     method = value.getClass().getMethod(methodName, classes);
                  }
                  catch (NoSuchMethodException e)
                  {
                     // No exact match.  Find the "best" "applicable" method.
                     method = findMethod(value.getClass(), methodName, classes);
                  }
                  if (method != null)
                  {
                     assignParameters(method.getParameterTypes(), parameterArray);
                  }
                  else
                  {
                     // Couldn't find a Method.
                     throw new NoSuchMethodException("Couldn't find Method: " + methodName);
                  }
               }
               else
               {
                  // Simple property name.
                  try
                  {
                     // getPropertyName
                     String methodName = "get" + property.substring(0, 1).toUpperCase() +
                             property.substring(1);
                     method = value.getClass().getMethod(methodName);
                  }
                  catch (NoSuchMethodException e)
                  {
                     try
                     {
                        // isPropertyName
                        String methodName = "is" + property.substring(0, 1).toUpperCase() +
                                property.substring(1);
                        method = value.getClass().getMethod(methodName);
                     }
                     catch (NoSuchMethodException e2)
                     {
                        throw new NoSuchMethodException("No matching method found for property \"" + property + "\"");
                     }
                  }
                  parameterArray = new Object[0];
               }

               if (DEBUG)
                  System.out.println("  Method found: " + method);
               methodCall = new MethodCall(method, parameterArray);
            }
            myMethods.put(lookup, methodCall);
         }
      }
      return methodCall;
   }

   /**
    * Attempts to find a <code>Method</code> that can be executed given the
    * <code>Class</code> of the object on which to find a <code>Method</code>,
    * the parameter values, and the types of those parameters.
    * @param theClass The <code>Class</code> of the object on which to find a
    *    <code>Method</code>.
    * @param methodName The name of the <code>Method</code>.
    * @param paramTypes The types of the parameters.
    * @return The best <code>Method</code>, or <code>null</code> if no good
    *    <code>Method</code> could be found.
    */
   @SuppressWarnings("ForLoopReplaceableByForEach")
   private Method findMethod(Class<?> theClass, String methodName, Class<?>[] paramTypes)
   {
      ArrayList<Method> methods = new ArrayList<Method>();
      Method[] allMethods = theClass.getMethods();
      // Restrict to all methods with the given name and with the same number of parameters.
      for (int i = 0; i < allMethods.length; i++)
      {
         Method method = allMethods[i];
         if (methodName.equals(method.getName()) && doesMethodApply(method, paramTypes))
            methods.add(method);
      }
      int size = methods.size();
      if (size == 1)
      {
         return methods.get(0);
      }
      else if (size == 0)
      {
         return null;
      }
      // If we get here, multiple Methods match (overloading with assignable
      // types has occurred).  Determine which is the most "specific"
      // (narrowest types found).
      Method mostSpecific = null;
      for (int i = 0; i < size; i++)
      {
         Method method = methods.get(i);
         if (mostSpecific == null ||
             isMoreSpecific(method.getParameterTypes(), mostSpecific.getParameterTypes()))
            mostSpecific = method;
      }

      return mostSpecific;
   }

   /**
    * Assigns all given values to the given desired types.  If the type of a
    * specific value is not the same as the corresponding <code>Class</code>,
    * then that value is replaced with a value that is equivalent, but it is of
    * the same type as the corresponding <code>Class</code>.  Assumes that the
    * lengths of both arrays are the same.  Also assumes that all conversions
    * are already determined to be possible and legal.
    * @param desiredTypes An array of <code>Classes</code> representing a
    *    desired method signature.
    * @param values An array of values that may not (yet) match the desired
    *    types array.
    */
   private void assignParameters(Class<?>[] desiredTypes, Object[] values)
   {
      for (int i = 0; i < desiredTypes.length; i++)
      {
         Class<?> valueType = values[i].getClass();
         Class<?> desiredType = desiredTypes[i];
         if (desiredType != valueType)
         {
            // Non-primitive.
            if (!desiredType.isPrimitive())
               values[i] = desiredType.cast(values[i]);
            // Promote Numbers (Can't promote up to a Byte, the "lowest"
            // precision Number.
            else if (desiredType == Short.TYPE || desiredType == Short.class)
               values[i] = ((Number) values[i]).shortValue();
            else if (desiredType == Integer.TYPE || desiredType == Integer.class)
               values[i] = ((Number) values[i]).intValue();
            else if (desiredType == Long.TYPE || desiredType == Long.class)
               values[i] = ((Number) values[i]).longValue();
            else if (desiredType == Float.TYPE || desiredType == Float.class)
               values[i] = ((Number) values[i]).floatValue();
            else if (desiredType == Double.TYPE || desiredType == Double.class)
               values[i] = ((Number) values[i]).doubleValue();

         }
      }
   }

   /**
    * Determines whether the array of <code>Class</code> types given by
    * <code>types1</code> is more "specific" than that of <code>types2</code>.
    * Assumes that the lengths of the arrays are equal.
    * @param types1 An array of <code>Classes</code> representing a method
    *    signature.
    * @param types2 An array of <code>Classes</code> representing a method
    *    signature.
    * @return <code>true</code> if <code>types1</code> is more specific,
    *    <code>false<code> otherwise.
    */
   private boolean isMoreSpecific(Class<?>[] types1, Class<?>[] types2)
   {
      for (int i = 0; i < types1.length; i++)
      {
         // At least of 1 these 2 must be true, because the associated methods
         // both "apply".
         boolean leftParamAssignableToRight = isAssignable(types1[i], types2[i]);
         boolean rightParamAssignableToLeft = isAssignable(types2[i], types1[i]);
         if (leftParamAssignableToRight)
         {
            // Left side has its parameter type more specific than the right side.
            if (!rightParamAssignableToLeft)
               return true;
            // If both are assignable to each other, then they are the same.
            // Keep looping.
         }
         // Right side has its parameter type more specific than the left side.
         else
            return false;
      }
      // Shouldn't get here, but for completeness..
      return false;
   }

   /**
    * Determines if the given <code>Method</code> can be invoked using
    * arguments of the given types.
    * @param method The <code>Method</code>.
    * @param paramTypes The types of the arguments.
    * @return <code>true</code> if it can be invoked, <code>false</code>
    *    otherwise.
    */
   private boolean doesMethodApply(Method method, Class<?>[] paramTypes)
   {
      Class<?>[] methodParamTypes = method.getParameterTypes();
      if (methodParamTypes.length != paramTypes.length)
         return false;
      for (int i = 0; i < methodParamTypes.length; i++)
      {
         if (!isAssignable(paramTypes[i], methodParamTypes[i]))
            return false;
      }
      return true;
   }

   /**
    * Determines if an object of the given "from" <code>Class</code> can be
    * assigned to a parameter type of the given "to" <code>Class</code>.
    * @param fromClass The <code>Class</code> which might be assignable.
    * @param toClass The <code>Class</code> to which an object might be
    *    assigned.
    * @return <code>true</code> if assignable, <code>false</code> otherwise.
    */
   private boolean isAssignable(Class<?> fromClass, Class<?> toClass)
   {
      // The "from" class can't be primitive, because it already would have
      // been wrapped in an Object.
      if (!toClass.isPrimitive() && toClass.isAssignableFrom(fromClass))
         return true;

      // Primitive types are assignable to themselves and any "up the chain":
      // byte, short, int, long, float, double.
      if (toClass.isPrimitive())
      {
         if (toClass == Boolean.TYPE && fromClass == Boolean.class)
            return true;
         if (toClass == Byte.TYPE && fromClass == Byte.class)
            return true;
         if (toClass == Short.TYPE && (
              fromClass == Short.class || fromClass == Byte.class))
            return true;
         if (toClass == Integer.TYPE && (
              fromClass == Integer.class || fromClass == Short.class ||
              fromClass == Byte.class))
            return true;
         if (toClass == Long.TYPE && (
              fromClass == Long.class || fromClass == Integer.class ||
              fromClass == Short.class || fromClass == Byte.class))
            return true;
         if (toClass == Float.TYPE && (
              fromClass == Float.class || fromClass == Long.class ||
              fromClass == Integer.class || fromClass == Short.class ||
              fromClass == Byte.class))
            return true;
         // Currently, there is no way to tell whether a floating-point literal
         // found in a property should be a float or a double, so PropertyParser
         // just makes it a double.  This would disallow calling methods that
         // take a float, so allow a double to be assigned to a float.
         if (toClass == Float.TYPE && (
              fromClass == Double.class || fromClass == Float.class ||
              fromClass == Long.class || fromClass == Integer.class ||
              fromClass == Short.class || fromClass == Byte.class))
            return true;
         if (toClass == Double.TYPE && (
              fromClass == Double.class || fromClass == Float.class ||
              fromClass == Long.class || fromClass == Integer.class ||
              fromClass == Short.class || fromClass == Byte.class))
            return true;
      }

      return false;
   }
}
