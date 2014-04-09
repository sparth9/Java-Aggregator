package net.sf.jagg.test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.*;

import net.sf.jagg.Aggregations;

/**
 * Tests the private <code>indexOfLastMatching</code> method.
 *
 * @author Randy Gettman
 * @since 0.4.0
 */
public class IndexOfLastMatchingTest
{
   /**
    * Tests the private <code>indexOfLastMatching</code> method.
    * @throws NoSuchMethodException If (somehow) the
    *    <code>indexOfLastMatching</code> didn't exist.  Of course, that would
    *    be considered a test failure.
    * @throws IllegalAccessException If (somehow)
    *    <code>setAccessible(true)</code> failed and we can't execute the
    *    <code>indexOfLastMatching</code> method.
    * @throws InvocationTargetException If the invoked <code>Method</code>
    *    throws an Exception during its execution.
    */
   @Test
   public void testIndexOfLastMatching()
      throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
   {
      List<Integer> intList = Arrays.asList(23, 23, 1, 1, 3, 3, 3, 3, 3, 710);
      int listsize = intList.size();

      Collections.sort(intList);
      int startIndex = 0;
      int endIndex;

      List<Integer> valuesList = Arrays.asList(1, 6, 8, 9);
      Iterator<Integer> itr = valuesList.iterator();

      Method m = Aggregations.class.getDeclaredMethod("indexOfLastMatching", List.class, Comparator.class, Integer.TYPE);
      Method m2 = Aggregations.class.getDeclaredMethod("indexOfLastMatching", List.class, Comparator.class, Integer.TYPE, Integer.TYPE);
      m.setAccessible(true);  // Allows access to the private method.
      m2.setAccessible(true);
      Comparator<Integer> comparator = new Comparator<Integer>() {
         public int compare(Integer o1, Integer o2)
         {
            return o1 - o2;
         }
      };
      while (startIndex < listsize)
      {
         endIndex = (Integer) m.invoke(null, intList, comparator, startIndex);
         //System.out.println("indexOfLastMatching(" + startIndex + ") is " + endIndex);
         assertEquals(itr.next().intValue(), endIndex);

         startIndex = endIndex + 1;
      }

      endIndex = (Integer) m2.invoke(null, intList, comparator, 2, 3);
      assertEquals(3, endIndex);
   }
}
