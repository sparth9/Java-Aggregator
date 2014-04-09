package net.sf.jagg.test.msd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.*;

import net.sf.jagg.msd.Discriminator;
import net.sf.jagg.msd.MsdWorkspace;
import net.sf.jagg.msd.ShortDiscriminator;

/**
 * This tests <code>ShortDiscriminators</code>.
 *
 * @author Randy Gettman
 * @since 0.5.0
 */
public class ShortDiscriminatorTest
{
   private static final MsdWorkspace myWorkspace = new MsdWorkspace();

   /**
    * Tests discrimination by shorts.
    */
   @Test
   public void testDiscrimination()
   {
      List<Short> values = Arrays.asList((short) 29, (short) 10007, (short) 0, (short) 0, (short) 29, (short) 14,
         (short) 10007, (short) 0, (short) 0, (short) 10007, (short) 29, (short) -99, (short) 29);
      Discriminator<Short> discr = new ShortDiscriminator();
      List<List<Short>> results = discr.discriminate(values, myWorkspace);

      // Singletons first.
      List<List<Short>> expected = new ArrayList<List<Short>>();
      expected.add(Arrays.asList((short) 14));
      expected.add(Arrays.asList((short) -99));
      expected.add(Collections.nCopies(4, (short) 29));
      expected.add(Collections.nCopies(3, (short) 10007));
      expected.add(Collections.nCopies(4, (short) 0));

      int size = results.size();
      assertEquals(expected.size(), size);
      for (int i = 0; i < expected.size(); i++)
      {
         List<Short> expectedList = expected.get(i);
         List<Short> resultList = results.get(i);
         for (int j = 0; j < expectedList.size(); j++)
            assertEquals(expectedList.get(j), resultList.get(j));
      }
   }
}