package net.sf.jagg.test.msd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.*;

import net.sf.jagg.msd.ByteDiscriminator;
import net.sf.jagg.msd.Discriminator;
import net.sf.jagg.msd.MsdWorkspace;

/**
 * This tests <code>ByteDiscriminators</code>.
 *
 * @author Randy Gettman
 * @since 0.5.0
 */
public class ByteDiscriminatorTest
{
   private static final MsdWorkspace myWorkspace = new MsdWorkspace();

   /**
    * Tests discrimination by bytes.
    */
   @Test
   public void testDiscrimination()
   {
      List<Byte> values = Arrays.asList((byte) 29, (byte) 7, (byte) 0, (byte) 0, (byte) 29, (byte) 14,
         (byte) 7, (byte) 0, (byte) 0, (byte) 7, (byte) 29, (byte) -99, (byte) 29);
      Discriminator<Byte> discr = new ByteDiscriminator();
      List<List<Byte>> results = discr.discriminate(values, myWorkspace);

      // Singletons first.
      List<List<Byte>> expected = new ArrayList<List<Byte>>();
      expected.add(Arrays.asList((byte) 14));
      expected.add(Arrays.asList((byte) -99));
      expected.add(Collections.nCopies(4, (byte) 29));
      expected.add(Collections.nCopies(3, (byte) 7));
      expected.add(Collections.nCopies(4, (byte) 0));

      int size = results.size();
      assertEquals(expected.size(), size);
      for (int i = 0; i < expected.size(); i++)
      {
         List<Byte> expectedList = expected.get(i);
         List<Byte> resultList = results.get(i);
         for (int j = 0; j < expectedList.size(); j++)
            assertEquals(expectedList.get(j), resultList.get(j));
      }
   }
}
