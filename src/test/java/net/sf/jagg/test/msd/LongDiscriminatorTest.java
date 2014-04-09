package net.sf.jagg.test.msd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.*;

import net.sf.jagg.msd.Discriminator;
import net.sf.jagg.msd.LongDiscriminator;
import net.sf.jagg.msd.MsdWorkspace;

/**
 * This tests <code>LongDiscriminators</code>.
 *
 * @author Randy Gettman
 * @since 0.5.0
 */
public class LongDiscriminatorTest
{
   private static final MsdWorkspace myWorkspace = new MsdWorkspace();

   /**
    * Tests discrimination by longs.
    */
   @Test
   public void testDiscrimination()
   {
      List<Long> values = Arrays.asList(7L, 0L, (1L << 16) + 7, (1L << 32) + 7, (1L << 32) + (1L << 16) + 7, (1L << 48) + 7,
         (1L << 48) + (1L << 16) + 7, (1L << 48) + (1L << 32) + 7, (1L << 48) + (1L << 32) + (1L << 16) + 7, 0L, 7L);
      Discriminator<Long> discr = new LongDiscriminator();
      List<List<Long>> results = discr.discriminate(values, myWorkspace);

      // Singletons first.
      List<List<Long>> expected = new ArrayList<List<Long>>();
      expected.add(Arrays.asList((1L << 48) + 7));
      expected.add(Arrays.asList((1L << 32) + 7));
      expected.add(Arrays.asList((1L << 48) + (1L << 32) + 7));
      expected.add(Arrays.asList((1L << 16) + 7));
      expected.add(Arrays.asList((1L << 48) + (1L << 16) + 7));
      expected.add(Arrays.asList((1L << 32) + (1L << 16) + 7));
      expected.add(Arrays.asList((1L << 48) + (1L << 32) + (1L << 16) + 7));
      expected.add(Collections.nCopies(2, 7L));
      expected.add(Collections.nCopies(2, 0L));

      int size = results.size();
      assertEquals(expected.size(), size);
      for (int i = 0; i < expected.size(); i++)
      {
         List<Long> expectedList = expected.get(i);
         List<Long> resultList = results.get(i);
         for (int j = 0; j < expectedList.size(); j++)
            assertEquals(expectedList.get(j), resultList.get(j));
      }
   }
}