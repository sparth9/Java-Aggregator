package net.sf.jagg.test.msd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.*;

import net.sf.jagg.msd.Discriminator;
import net.sf.jagg.msd.MsdWorkspace;
import net.sf.jagg.msd.PropertiesDiscriminator;
import net.sf.jagg.test.model.Record;

/**
 * This tests <code>PropertiesDiscriminators</code>.
 *
 * @author Randy Gettman
 * @since 0.5.0
 */
public class PropertiesDiscriminatorTest
{
   private static final MsdWorkspace myWorkspace = new MsdWorkspace();

   /**
    * Tests discrimination by properties.
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
      List<Discriminator<Record>> discrs = Arrays.<Discriminator<Record>>asList(
         new PropertiesDiscriminator<Record>(),
         new PropertiesDiscriminator<Record>("category1"),
         new PropertiesDiscriminator<Record>("category1", "category2"),
         new PropertiesDiscriminator<Record>("category1", "category2", "category3"),
         new PropertiesDiscriminator<Record>("category1", "category2", "category3", "category4"));
      List<List<List<Record>>> resultSets = new ArrayList<List<List<Record>>>();
      for (Discriminator<Record> discr : discrs)
      {
         List<List<Record>> resultSet = discr.discriminate(values, myWorkspace);
         resultSets.add(resultSet);
      }

      // Singletons first.
      List<List<List<Record>>> expectedSets = new ArrayList<List<List<Record>>>();
      // No categories.
      expectedSets.add(Arrays.<List<Record>>asList(Arrays.<Record>asList(aOneAb, aTwoAb, bOneBb, aOneBb, aOneAf, aOneAb6, bOneBb7)));
      // cat1
      expectedSets.add(Arrays.<List<Record>>asList(Arrays.<Record>asList(aOneAb, aTwoAb, aOneBb, aOneAf, aOneAb6),
                                     Arrays.<Record>asList(bOneBb, bOneBb7)));
      // cats 1 and 2
      expectedSets.add(Arrays.<List<Record>>asList(Arrays.<Record>asList(aTwoAb),
                                     Arrays.<Record>asList(aOneAb, aOneBb, aOneAf, aOneAb6),
                                     Arrays.<Record>asList(bOneBb, bOneBb7)));
      // cats 1, 2, and 3
      expectedSets.add(Arrays.<List<Record>>asList(Arrays.<Record>asList(aTwoAb),
                                     Arrays.<Record>asList(aOneBb),
                                     Arrays.<Record>asList(aOneAb, aOneAf, aOneAb6),
                                     Arrays.<Record>asList(bOneBb, bOneBb7)));
      // cats 1, 2, 3, and 4
      expectedSets.add(Arrays.<List<Record>>asList(Arrays.<Record>asList(aTwoAb),
                                     Arrays.<Record>asList(aOneBb),
                                     Arrays.<Record>asList(aOneAf),
                                     Arrays.<Record>asList(aOneAb, aOneAb6),
                                     Arrays.<Record>asList(bOneBb, bOneBb7)));

      for (int t = 0; t < 5; t++)
      {
         List<List<Record>> results = resultSets.get(t);
         List<List<Record>> expected = expectedSets.get(t);
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
               // Test property equivalency for each level.
               // E.g. Discriminator at index t discriminates the properties
               // of categories 1 through t.
               if (t >= 1)
                  assertEquals(expectedRec.getCategory1(), resultRec.getCategory1());
               if (t >= 2)
                  assertEquals(expectedRec.getCategory2(), resultRec.getCategory2());
               if (t >= 3)
                  assertEquals(expectedRec.getCategory3(), resultRec.getCategory3());
               if (t >= 4)
                  assertEquals(expectedRec.getCategory4(), resultRec.getCategory4());
            }
         }
      }
   }
}