package net.sf.jagg.test.msd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.*;

import net.sf.jagg.msd.Discriminator;
import net.sf.jagg.msd.DoubleDiscriminator;
import net.sf.jagg.msd.MsdWorkspace;

/**
 * This tests <code>DoubleDiscriminators</code>.
 *
 * @author Randy Gettman
 * @since 0.5.0
 */
public class DoubleDiscriminatorTest
{
   private static final MsdWorkspace myWorkspace = new MsdWorkspace();

   /**
    * Tests discrimination by doubles.
    */
   @Test
   public void testDiscrimination()
   {
      List<Double> values = Arrays.asList(7.0, 0.0, Math.PI, 2 * Math.PI, Math.E, -Math.E, Math.PI, -Math.E, -99.0,
         0.0, 7.0, 6.0, 2 * Math.PI, 2 * Math.E, Math.E, Math.PI, -7.0);
      Discriminator<Double> discr = new DoubleDiscriminator();
      List<List<Double>> results = discr.discriminate(values, myWorkspace);

      // Singletons first.
      List<List<Double>> expected = new ArrayList<List<Double>>();
      expected.add(Arrays.asList(-99.0));
      expected.add(Arrays.asList(6.0));
      expected.add(Arrays.asList(-7.0));
      expected.add(Arrays.asList(2 * Math.E));
      expected.add(Collections.nCopies(2, 7.0));
      expected.add(Collections.nCopies(2, 0.0));
      expected.add(Collections.nCopies(3, Math.PI));
      expected.add(Collections.nCopies(2, 2 * Math.PI));
      expected.add(Collections.nCopies(2, Math.E));
      expected.add(Collections.nCopies(2, -Math.E));

      int size = results.size();
      assertEquals(expected.size(), size);
      for (int i = 0; i < expected.size(); i++)
      {
         List<Double> expectedList = expected.get(i);
         List<Double> resultList = results.get(i);
         for (int j = 0; j < expectedList.size(); j++)
            assertEquals(expectedList.get(j), resultList.get(j));
      }
   }
}