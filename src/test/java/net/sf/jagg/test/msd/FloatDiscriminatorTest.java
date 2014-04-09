package net.sf.jagg.test.msd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.*;

import net.sf.jagg.msd.Discriminator;
import net.sf.jagg.msd.FloatDiscriminator;
import net.sf.jagg.msd.MsdWorkspace;

/**
 * This tests <code>FloatDiscriminators</code>.
 *
 * @author Randy Gettman
 * @since 0.5.0
 */
public class FloatDiscriminatorTest
{
   private static final MsdWorkspace myWorkspace = new MsdWorkspace();

   /**
    * Tests discrimination by floats.
    */
   @Test
   public void testDiscrimination()
   {
      List<Float> values = Arrays.asList(7.0f, 0.0f, 3.14159f, 6.28318f, 2.71828f, -2.71828f, 3.14159f, -2.71828f, -99.0f,
         0.0f, 7.0f, 6.0f, 6.28318f, 5.43656f, 2.71828f, 3.14159f, -7.0f);
      Discriminator<Float> discr = new FloatDiscriminator();
      List<List<Float>> results = discr.discriminate(values, myWorkspace);

      // Singletons first.
      List<List<Float>> expected = new ArrayList<List<Float>>();
      expected.add(Arrays.asList(-99.0f));
      expected.add(Arrays.asList(6.0f));
      expected.add(Arrays.asList(-7.0f));
      expected.add(Arrays.asList(5.43656f));
      expected.add(Collections.nCopies(2, 7.0f));
      expected.add(Collections.nCopies(2, 0.0f));
      expected.add(Collections.nCopies(3, 3.14159f));
      expected.add(Collections.nCopies(2, 6.28318f));
      expected.add(Collections.nCopies(2, 2.71828f));
      expected.add(Collections.nCopies(2, -2.71828f));

      int size = results.size();
      assertEquals(expected.size(), size);
      for (int i = 0; i < expected.size(); i++)
      {
         List<Float> expectedList = expected.get(i);
         List<Float> resultList = results.get(i);
         for (int j = 0; j < expectedList.size(); j++)
            assertEquals(expectedList.get(j), resultList.get(j));
      }
   }
}