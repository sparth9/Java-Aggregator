package net.sf.jagg.test.msd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.*;

import net.sf.jagg.msd.DateDiscriminator;
import net.sf.jagg.msd.Discriminator;
import net.sf.jagg.msd.MsdWorkspace;

/**
 * This tests <code>DateDiscriminators</code>.
 *
 * @author Randy Gettman
 * @since 0.5.0
 */
public class DateDiscriminatorTest
{
   private static final long MS_IN_DAY = 60 * 60 * 24 * 1000;

   private static final MsdWorkspace myWorkspace = new MsdWorkspace();

   /**
    * Tests discrimination by <code>Dates</code>.
    */
   @Test
   public void testDiscrimination()
   {
      long ms = System.currentTimeMillis();
      Date today = new Date(ms);
      Date tomorrow = new Date(ms + MS_IN_DAY);
      Date yesterday = new Date(ms - MS_IN_DAY);

      List<Date> values = Arrays.asList(tomorrow, yesterday, tomorrow, tomorrow,
         today, today, tomorrow, yesterday, today, yesterday,
         tomorrow, tomorrow, today);
      Discriminator<Date> discr = new DateDiscriminator<Date>();
      List<List<Date>> results = discr.discriminate(values, myWorkspace);

      // Singletons first.
      List<List<Date>> expected = new ArrayList<List<Date>>();
      expected.add(Collections.nCopies(6, tomorrow));
      expected.add(Collections.nCopies(3, yesterday));
      expected.add(Collections.nCopies(4, today));

      int size = results.size();
      assertEquals(expected.size(), size);
      for (int i = 0; i < expected.size(); i++)
      {
         List<Date> expectedList = expected.get(i);
         List<Date> resultList = results.get(i);
         for (int j = 0; j < expectedList.size(); j++)
            assertEquals(expectedList.get(j).getTime(), resultList.get(j).getTime());
      }
   }
}