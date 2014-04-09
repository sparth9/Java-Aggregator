package net.sf.jagg.test.msd;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.*;

import net.sf.jagg.msd.BigIntegerDiscriminator;
import net.sf.jagg.msd.Discriminator;
import net.sf.jagg.msd.MsdWorkspace;

/**
 * This tests <code>BigIntegerDiscriminators</code>.
 *
 * @author Randy Gettman
 * @since 0.5.0
 */
public class BigIntegerDiscriminatorTest
{
   private static final MsdWorkspace myWorkspace = new MsdWorkspace();

   /**
    * Tests discrimination by <code>BigIntegers</code>.
    */
   @Test
   public void testDiscrimination()
   {
      List<BigInteger> values = Arrays.asList(new BigInteger("1"), new BigInteger("710"), new BigInteger("0"),
         new BigInteger("8913470195846812458162347670891237488901234615"),
         new BigInteger("8913470195846812458162347670891237488901234616"),
         new BigInteger("-8913470195846812458162347670891237488901234615"), new BigInteger("-1"), new BigInteger("0"),
         new BigInteger("710"), new BigInteger("8913470195846812458162347670891237488901234616"),
         new BigInteger("8913470195"), new BigInteger("-1"), new BigInteger("18000"), new BigInteger("710"));
      Discriminator<BigInteger> discr = new BigIntegerDiscriminator();
      List<List<BigInteger>> results = discr.discriminate(values, myWorkspace);

      // Singletons first.
      List<List<BigInteger>> expected = new ArrayList<List<BigInteger>>();
      expected.add(Arrays.asList(new BigInteger("-8913470195846812458162347670891237488901234615")));
      expected.add(Arrays.asList(new BigInteger("18000")));
      expected.add(Arrays.asList(new BigInteger("1")));
      expected.add(Arrays.asList(new BigInteger("8913470195")));
      expected.add(Collections.nCopies(2, new BigInteger("0")));
      expected.add(Collections.nCopies(2, new BigInteger("-1")));
      expected.add(Collections.nCopies(3, new BigInteger("710")));
      expected.add(Arrays.asList(new BigInteger("8913470195846812458162347670891237488901234615")));
      expected.add(Collections.nCopies(2, new BigInteger("8913470195846812458162347670891237488901234616")));

      int size = results.size();
      assertEquals(expected.size(), size);
      for (int i = 0; i < expected.size(); i++)
      {
         List<BigInteger> expectedList = expected.get(i);
         List<BigInteger> resultList = results.get(i);
         for (int j = 0; j < expectedList.size(); j++)
            assertTrue(expectedList.get(j).equals(resultList.get(j)));
      }
   }
}