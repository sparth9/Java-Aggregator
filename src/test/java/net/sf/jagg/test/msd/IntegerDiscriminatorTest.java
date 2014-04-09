package net.sf.jagg.test.msd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.*;

import net.sf.jagg.msd.Discriminator;
import net.sf.jagg.msd.IntegerDiscriminator;
import net.sf.jagg.msd.MsdWorkspace;

/**
 * This tests <code>IntegerDiscriminators</code>.
 *
 * @author Randy Gettman
 * @since 0.5.0
 */
public class IntegerDiscriminatorTest
{
   private static final MsdWorkspace myWorkspace = new MsdWorkspace();

   /**
    * Tests discrimination by integers.
    */
   @Test
   public void testDiscrimination()
   {
      List<Integer> values = Arrays.asList(7, 0, 1000000, 0, 0, 1000001, -2000000, 1000000, -99, 7, -2000000, 65543,
         7, 0, 1000001, 1065536, 7);
      Discriminator<Integer> discr = new IntegerDiscriminator();
      List<List<Integer>> results = discr.discriminate(values, myWorkspace);

      // Singletons first.
      List<List<Integer>> expected = new ArrayList<List<Integer>>();
      expected.add(Arrays.asList(-99));
      expected.add(Arrays.asList(65543));
      expected.add(Arrays.asList(1065536));
      expected.add(Collections.nCopies(4, 7));
      expected.add(Collections.nCopies(4, 0));
      expected.add(Collections.nCopies(2, 1000000));
      expected.add(Collections.nCopies(2, 1000001));
      expected.add(Collections.nCopies(2, -2000000));

      int size = results.size();
      assertEquals(expected.size(), size);
      for (int i = 0; i < expected.size(); i++)
      {
         List<Integer> expectedList = expected.get(i);
         List<Integer> resultList = results.get(i);
         for (int j = 0; j < expectedList.size(); j++)
            assertEquals(expectedList.get(j), resultList.get(j));
      }
   }
}