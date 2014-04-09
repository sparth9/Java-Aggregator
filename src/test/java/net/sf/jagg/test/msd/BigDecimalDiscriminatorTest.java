package net.sf.jagg.test.msd;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.*;

import net.sf.jagg.msd.BigDecimalDiscriminator;
import net.sf.jagg.msd.Discriminator;
import net.sf.jagg.msd.MsdWorkspace;

/**
 * This tests <code>BigDecimalDiscriminators</code>.
 *
 * @author Randy Gettman
 * @since 0.5.0
 */
public class BigDecimalDiscriminatorTest
{
   private static final MsdWorkspace myWorkspace = new MsdWorkspace();

   /**
    * Tests discrimination by <code>BigDecimals</code>.
    */
   @Test
   public void testDiscrimination()
   {
      List<BigDecimal> values = Arrays.asList(new BigDecimal("1"), new BigDecimal("710"), new BigDecimal("0"),
         new BigDecimal("60.00"), new BigDecimal("60.0000"),  new BigDecimal("-60.00"), new BigDecimal("-1"),
         new BigDecimal("0"), new BigDecimal("710.0"), new BigDecimal("6E1"),
         new BigDecimal("89134.70195"), new BigDecimal("-1.00000000"), new BigDecimal("18000"), new BigDecimal("71E1"));
      Discriminator<BigDecimal> discr = new BigDecimalDiscriminator();
      List<List<BigDecimal>> results = discr.discriminate(values, myWorkspace);

      // Singletons first.
      List<List<BigDecimal>> expected = new ArrayList<List<BigDecimal>>();
      expected.add(Arrays.asList(new BigDecimal("89134.70195")));
      expected.add(Arrays.asList(new BigDecimal("18000")));
      expected.add(Arrays.asList(new BigDecimal("1")));
      expected.add(Arrays.asList(new BigDecimal("-60")));
      expected.add(Collections.nCopies(2, new BigDecimal("0")));
      expected.add(Collections.nCopies(2, new BigDecimal("-1")));
      expected.add(Collections.nCopies(3, new BigDecimal("710")));
      expected.add(Collections.nCopies(3, new BigDecimal("60")));

      int size = results.size();
      assertEquals(expected.size(), size);
      for (int i = 0; i < expected.size(); i++)
      {
         List<BigDecimal> expectedList = expected.get(i);
         List<BigDecimal> resultList = results.get(i);
         // Must use BigDecimal.compareTo instead of BigDecimal.equals, because
         // "equals" will treat equivalent quantities with different scale
         // different (e.g. "6" != "6.00").
         for (int j = 0; j < expectedList.size(); j++)
            assertTrue(expectedList.get(j).compareTo(resultList.get(j)) == 0);
      }
   }
}