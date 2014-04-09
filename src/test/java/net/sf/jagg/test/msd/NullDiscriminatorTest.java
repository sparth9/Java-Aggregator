package net.sf.jagg.test.msd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.*;

import net.sf.jagg.msd.CharSequenceDiscriminator;
import net.sf.jagg.msd.Discriminator;
import net.sf.jagg.msd.MsdWorkspace;
import net.sf.jagg.msd.NullDiscriminator;

/**
 * This tests <code>NullDiscriminators</code>.
 *
 * @author Randy Gettman
 * @since 0.5.0
 */
public class NullDiscriminatorTest
{
   private static final MsdWorkspace myWorkspace = new MsdWorkspace();

   /**
    * Tests discrimination by character sequences that include
    * <code>nulls</code>.
    */
   @Test
   public void testDiscrimination()
   {
      List<String> values = Arrays.asList(null, "Java", "", "JAVA", "java", "Mars", "Marsh", "java", "Mast", "Marshall",
         "", null, "JavaScript", "supercalifragilisticexpialadocius", null, "Mart", "Marsh", "Mars", "Marshall", "Java",
         "Master", "", "mast", "Math", null);
      Discriminator<String> discr = new NullDiscriminator<String>(new CharSequenceDiscriminator<String>());
      List<List<String>> results = discr.discriminate(values, myWorkspace);

      // Singletons first.
      List<List<String>> expected = new ArrayList<List<String>>();
      expected.add(Arrays.asList("supercalifragilisticexpialadocius"));
      expected.add(Arrays.asList("mast"));
      expected.add(Collections.nCopies(3, ""));
      expected.add(Arrays.asList("JAVA"));
      expected.add(Arrays.asList("Math"));
      expected.add(Arrays.asList("Mart"));
      expected.add(Arrays.asList("JavaScript"));
      expected.add(Collections.nCopies(2, "java"));
      expected.add(Arrays.asList("Mast"));
      expected.add(Arrays.asList("Master"));
      expected.add(Collections.nCopies(2, "Java"));
      expected.add(Collections.nCopies(2, "Mars"));
      expected.add(Collections.nCopies(2, "Marsh"));
      expected.add(Collections.nCopies(2, "Marshall"));
      expected.add(Collections.<String>nCopies(4, null));

      int size = results.size();
      assertEquals(expected.size(), size);
      for (int i = 0; i < expected.size(); i++)
      {
         List<String> expectedList = expected.get(i);
         List<String> resultList = results.get(i);
         for (int j = 0; j < expectedList.size(); j++)
            assertEquals(expectedList.get(j), resultList.get(j));
      }
   }

   /**
    * Tests discrimination when all elements are <code>null</code>.
    */
   @Test
   public void testAllNulls()
   {
      List<String> values = Collections.nCopies(4, (String) null);
      Discriminator<String> discr = new NullDiscriminator<String>(new CharSequenceDiscriminator<String>());
      List<List<String>> results = discr.discriminate(values, myWorkspace);

      List<List<String>> expected = new ArrayList<List<String>>();
      expected.add(Collections.<String>nCopies(4, null));

      int size = results.size();
      assertEquals(expected.size(), size);
      for (int i = 0; i < expected.size(); i++)
      {
         List<String> expectedList = expected.get(i);
         List<String> resultList = results.get(i);
         for (int j = 0; j < expectedList.size(); j++)
            assertEquals(expectedList.get(j), resultList.get(j));
      }
   }

   /**
    * Tests discrimination of an empty list.
    */
   @Test
   public void testEmptyList()
   {
      List<String> values = new ArrayList<String>(0);
      Discriminator<String> discr = new NullDiscriminator<String>(new CharSequenceDiscriminator<String>());
      List<List<String>> results = discr.discriminate(values, myWorkspace);

      List<List<String>> expected = Collections.nCopies(0, null);

      int size = results.size();
      assertEquals(expected.size(), size);
      for (int i = 0; i < expected.size(); i++)
      {
         List<String> expectedList = expected.get(i);
         List<String> resultList = results.get(i);
         for (int j = 0; j < expectedList.size(); j++)
            assertEquals(expectedList.get(j), resultList.get(j));
      }
   }
}