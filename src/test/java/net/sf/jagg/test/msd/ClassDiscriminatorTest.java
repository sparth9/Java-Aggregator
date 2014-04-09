package net.sf.jagg.test.msd;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.*;

import net.sf.jagg.msd.ClassDiscriminator;
import net.sf.jagg.msd.Discriminator;
import net.sf.jagg.msd.MsdWorkspace;

/**
 * This tests <code>ClassDiscriminators</code>.
 *
 * @author Randy Gettman
 * @since 0.5.0
 */
public class ClassDiscriminatorTest
{
   private static final MsdWorkspace myWorkspace = new MsdWorkspace();

   /**
    * Tests discrimination by class names.
    */
   @Test
   public void testDiscrimination()
   {
      List<Object> values = Arrays.<Object>asList("Java", "", 'j', 1, 1.0, 2.0, 2, 1.0f, (byte) 3,
         (short) 4, 5L, Class.class, new BigInteger("6"), new BigDecimal("7.0"), "Mars", String.class, 'k');
      Discriminator<Object> discr = new ClassDiscriminator<Object>();
      List<List<Object>> results = discr.discriminate(values, myWorkspace);

      // Singletons first.
      List<List<Object>> expected = new ArrayList<List<Object>>();
      expected.add(Arrays.<Object>asList(1.0f));
      expected.add(Arrays.<Object>asList((byte) 3));
      expected.add(Arrays.<Object>asList(5L));
      expected.add(Arrays.<Object>asList((short) 4));
      expected.add(Arrays.<Object>asList(new BigInteger("6")));
      expected.add(Arrays.<Object>asList(new BigDecimal("7.0")));
      expected.add(Arrays.<Object>asList(Class.class, String.class));
      expected.add(Arrays.<Object>asList("Java", "", "Mars"));
      expected.add(Arrays.<Object>asList(1.0, 2.0));
      expected.add(Arrays.<Object>asList(1, 2));
      expected.add(Arrays.<Object>asList('j', 'k'));

      int size = results.size();
      assertEquals(expected.size(), size);
      for (int i = 0; i < expected.size(); i++)
      {
         List<Object> expectedList = expected.get(i);
         List<Object> resultList = results.get(i);
         for (int j = 0; j < expectedList.size(); j++)
            assertEquals(expectedList.get(j).getClass().getName(), resultList.get(j).getClass().getName());
      }
   }
}