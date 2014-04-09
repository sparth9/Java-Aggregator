package net.sf.jagg.test.msd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.*;

import net.sf.jagg.msd.BooleanDiscriminator;
import net.sf.jagg.msd.Discriminator;
import net.sf.jagg.msd.MsdWorkspace;

/**
 * This tests <code>BooleanDiscriminators</code>.
 *
 * @author Randy Gettman
 * @since 0.5.0
 */
public class BooleanDiscriminatorTest
{
   private static final MsdWorkspace myWorkspace = new MsdWorkspace();

   /**
    * Tests discrimination by booleans.
    */
   @Test
   public void testDiscrimination()
   {
      List<Boolean> values = Arrays.asList(false, true, true, true, false, false, true, false, true, true, false,
         false, false, false, false, true, true, true, true);
      Discriminator<Boolean> discr = new BooleanDiscriminator();
      List<List<Boolean>> results = discr.discriminate(values, myWorkspace);

      // Singletons first.
      List<List<Boolean>> expected = new ArrayList<List<Boolean>>();
      expected.add(Collections.nCopies(10, true));
      expected.add(Collections.nCopies(9, false));

      int size = results.size();
      assertEquals(expected.size(), size);
      for (int i = 0; i < expected.size(); i++)
      {
         List<Boolean> expectedList = expected.get(i);
         List<Boolean> resultList = results.get(i);
         for (int j = 0; j < expectedList.size(); j++)
            assertEquals(expectedList.get(j), resultList.get(j));
      }
   }
}