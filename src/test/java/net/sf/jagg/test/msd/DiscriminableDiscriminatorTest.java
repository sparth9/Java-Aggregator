package net.sf.jagg.test.msd;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.*;

import net.sf.jagg.msd.DiscriminableDiscriminator;
import net.sf.jagg.msd.Discriminator;
import net.sf.jagg.msd.MsdWorkspace;
import net.sf.jagg.test.model.Record;

/**
 * This tests <code>DiscriminableDiscriminators</code>.
 *
 * @author Randy Gettman
 * @since 0.5.0
 */
public class DiscriminableDiscriminatorTest
{
   private static final MsdWorkspace myWorkspace = new MsdWorkspace();

   /**
    * Tests discrimination by discriminable properties.
    */
   @Test
   @SuppressWarnings("unchecked")
   public void testDiscrimination()
   {
      Record aOneAb = new Record("A", 1, "alpha", "baseball", 1, 1);
      Record aTwoAb = new Record("A", 2, "alpha", "baseball", 1, 2);
      Record bOneBb = new Record("B", 1, "beta", "baseball", 1, 3);
      Record aOneBb = new Record("A", 1, "beta", "baseball", 1, 4);
      Record aOneAf = new Record("A", 1, "alpha", "football", 1, 5);
      Record aOneAb6 = new Record("A", 1, "alpha", "baseball", 1, 6);
      Record bOneBb7 = new Record("B", 1, "beta", "baseball", 1, 7);
      List<Record> values = Arrays.asList(aOneAb, aTwoAb, bOneBb,
         aOneBb, aOneAf, aOneAb6, bOneBb7);
      Discriminator<Record> discr = new DiscriminableDiscriminator<Record>();
      List<List<Record>> results = discr.discriminate(values, myWorkspace);

      // Singletons first.
      List<List<Record>> expected = Arrays.asList(Arrays.<Record>asList(aTwoAb),
                                    Arrays.<Record>asList(aOneBb),
                                    Arrays.<Record>asList(aOneAf),
                                    Arrays.<Record>asList(aOneAb, aOneAb6),
                                    Arrays.<Record>asList(bOneBb, bOneBb7));

      int size = results.size();
      assertEquals(expected.size(), size);
      for (int i = 0; i < expected.size(); i++)
      {
         List<Record> expectedList = expected.get(i);
         List<Record> resultList = results.get(i);
         for (int j = 0; j < expectedList.size(); j++)
         {
            Record expectedRec = expectedList.get(j);
            Record resultRec = resultList.get(j);
            // Test property equivalency.
            assertEquals(expectedRec.getCategory1(), resultRec.getCategory1());
            assertEquals(expectedRec.getCategory2(), resultRec.getCategory2());
            assertEquals(expectedRec.getCategory3(), resultRec.getCategory3());
            assertEquals(expectedRec.getCategory4(), resultRec.getCategory4());
         }
      }
   }

   /**
    * Tests to ensure <code>ClassCastException</code> is thrown when attempting
    * to discriminate objects that aren't <code>Discriminable</code>.
    */
   @Test(expected=ClassCastException.class)
   public void testNotDiscriminable()
   {
      // Classes can be discriminated with a ClassDiscriminator, but not a
      // DiscriminableDiscriminator.
      List<Class> values = Arrays.<Class>asList(String.class, Integer.class, Short.TYPE, Class.class,
         Integer.class);
      Discriminator<Class> discr = new DiscriminableDiscriminator<Class>();
      discr.discriminate(values, myWorkspace);
   }
}