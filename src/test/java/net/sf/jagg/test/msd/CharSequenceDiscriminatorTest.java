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

/**
 * This tests <code>CharSequenceDiscriminators</code>.
 *
 * @author Randy Gettman
 * @since 0.5.0
 */
public class CharSequenceDiscriminatorTest
{
   private static final MsdWorkspace myWorkspace = new MsdWorkspace();

   /**
    * Tests discrimination by character sequences.
    */
   @Test
   public void testDiscrimination()
   {
      List<String> values = Arrays.asList("Java", "", "JAVA", "java", "Mars", "Marsh", "java", "Mast", "Marshall",
         "", "JavaScript", "supercalifragilisticexpialadocius", "Mart", "Marsh", "Mars", "Marshall", "Java",
         "Master", "", "mast", "Math");
      Discriminator<String> discr = new CharSequenceDiscriminator<String>();
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