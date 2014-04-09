package net.sf.jagg.test.msd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.*;

import net.sf.jagg.msd.Discriminator;
import net.sf.jagg.msd.MsdWorkspace;
import net.sf.jagg.msd.RandomAccessListDiscriminator;

/**
 * This tests <code>RandomAccessListDiscriminators</code>.
 *
 * @author Randy Gettman
 * @since 0.5.0
 */
public class RandomAccessListDiscriminatorTest
{
   private static final MsdWorkspace myWorkspace = new MsdWorkspace();

   /**
    * Tests discrimination by random access lists.
    */
   @Test
   @SuppressWarnings("unchecked")
   public void testDiscrimination()
   {
      List<List<String>> values = Arrays.asList(new ArrayList<String>(), Arrays.<String>asList("Test", "Java"),
         Arrays.<String>asList("Java", "Test"), Arrays.<String>asList(""), new ArrayList<String>(), Arrays.<String>asList("Test", "Java"),
         Arrays.<String>asList("This", "is", "a", "really", "long", "complete", "sentence", "created", "just", "to", "test", "longer", "lists."),
         Arrays.<String>asList("One", "Two", "Three", "Four"), Arrays.<String>asList("Java", "Test"), Arrays.<String>asList("Java", "Test", "List"),
         Arrays.<String>asList(""), new ArrayList<String>(), Arrays.<String>asList("This", "is", "a", "shorter", "sentence."));
      Discriminator<List<String>> discr = new RandomAccessListDiscriminator<String>();
      List<List<List<String>>> results = discr.discriminate(values, myWorkspace);

      // Singletons first.
      List<List<List<String>>> expected = new ArrayList<List<List<String>>>();
      expected.add(Collections.nCopies(3, Collections.<String>emptyList()));
      expected.add(Collections.nCopies(1, Arrays.asList("One", "Two", "Three", "Four")));
      expected.add(Collections.nCopies(2, Arrays.asList("")));
      expected.add(Collections.nCopies(2, Arrays.asList("Test", "Java")));
      expected.add(Collections.nCopies(2, Arrays.asList("Java", "Test")));
      expected.add(Collections.nCopies(1, Arrays.asList("Java", "Test", "List")));
      expected.add(Collections.nCopies(1, Arrays.asList("This", "is", "a", "really", "long", "complete", "sentence", "created", "just", "to", "test", "longer", "lists.")));
      expected.add(Collections.nCopies(1, Arrays.asList("This", "is", "a", "shorter", "sentence.")));

      int size = results.size();
      assertEquals(expected.size(), size);
      for (int i = 0; i < expected.size(); i++)
      {
         List<List<String>> expectedList = expected.get(i);
         List<List<String>> resultList = results.get(i);
         for (int j = 0; j < expectedList.size(); j++)
         {
            List<String> expectedStringList = expectedList.get(j);
            List<String> resultStringList = resultList.get(j);
            assertEquals(expectedStringList.size(), resultStringList.size());
            for (int k = 0; k < expectedStringList.size(); k++)
               assertEquals(expectedStringList.get(k), resultStringList.get(k));
         }
      }
   }
}