package net.sf.jagg.test.msd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.*;

import net.sf.jagg.msd.ArrayDiscriminator;
import net.sf.jagg.msd.Discriminator;
import net.sf.jagg.msd.MsdWorkspace;

/**
 * This tests <code>ArrayDiscriminators</code>.
 *
 * @author Randy Gettman
 * @since 0.5.0
 */
public class ArrayDiscriminatorTest
{
   private static final MsdWorkspace myWorkspace = new MsdWorkspace();

   /**
    * Tests discrimination by arrays.
    */
   @Test
   public void testDiscrimination()
   {
      List<Integer[]> values = Arrays.asList(new Integer[] {1, 4, 9, 16}, new Integer[] {1, 4, 9, 17},
         new Integer[] {}, new Integer[] {1, 4, 9, 16}, new Integer[] {}, new Integer[] {1, 4, 9},
         new Integer[] {2, 5, 10, 18}, new Integer[] {-1, -4, -9, -16}, new Integer[] {1, 9, 17, 4},
         new Integer[] {16, 9, 4, 1}, new Integer[] {}, new Integer[] {1000}, new Integer[] {1, 4, 9, 17},
         new Integer[] {1, 4, 10, 16}, new Integer[] {1, 4, 9, 16, 25, 36, 49, 64, 81, 100, 121, 144, 169},
         new Integer[] {0, 0, 0, 0}, new Integer[] {0});
      Discriminator<Integer[]> discr = new ArrayDiscriminator<Integer>();
      List<List<Integer[]>> results = discr.discriminate(values, myWorkspace);

      // Singletons first.
      List<List<Integer[]>> expected = new ArrayList<List<Integer[]>>();
      expected.add(Collections.nCopies(3, new Integer[] {}));
      expected.add(Collections.nCopies(1, new Integer[] {2, 5, 10, 18}));
      expected.add(Collections.nCopies(1, new Integer[] {-1, -4, -9, -16}));
      expected.add(Collections.nCopies(1, new Integer[] {16, 9, 4, 1}));
      expected.add(Collections.nCopies(1, new Integer[] {1000}));
      expected.add(Collections.nCopies(1, new Integer[] {1, 9, 17, 4}));
      expected.add(Collections.nCopies(1, new Integer[] {0}));
      expected.add(Collections.nCopies(1, new Integer[] {0, 0, 0, 0}));
      expected.add(Collections.nCopies(1, new Integer[] {1, 4, 10, 16}));
      expected.add(Collections.nCopies(1, new Integer[] {1, 4, 9}));
      expected.add(Collections.nCopies(2, new Integer[] {1, 4, 9, 16}));
      expected.add(Collections.nCopies(1, new Integer[] {1, 4, 9, 16, 25, 36, 49, 64, 81, 100, 121, 144, 169}));
      expected.add(Collections.nCopies(2, new Integer[] {1, 4, 9, 17}));

      int size = results.size();
      assertEquals(expected.size(), size);
      for (int i = 0; i < expected.size(); i++)
      {
         List<Integer[]> expectedList = expected.get(i);
         List<Integer[]> resultList = results.get(i);
         for (int j = 0; j < expectedList.size(); j++)
            assertTrue(Arrays.deepEquals(expectedList.get(j), resultList.get(j)));
      }
   }
}