package net.sf.jagg.test.msd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.*;

import net.sf.jagg.msd.CalendarDiscriminator;
import net.sf.jagg.msd.Discriminator;
import net.sf.jagg.msd.MsdWorkspace;

/**
 * This tests <code>CalendarDiscriminators</code>.
 *
 * @author Randy Gettman
 * @since 0.5.0
 */
public class CalendarDiscriminatorTest
{
   private static final long MS_IN_DAY = 60 * 60 * 24 * 1000;

   private static final MsdWorkspace myWorkspace = new MsdWorkspace();

   /**
    * Tests discrimination by <code>Calendar</code>.
    */
   @Test
   public void testDiscrimination()
   {
      long ms = System.currentTimeMillis();
      Calendar today = Calendar.getInstance();
      today.setTimeInMillis(ms);
      Calendar tomorrow = Calendar.getInstance();
      tomorrow.setTimeInMillis(ms + MS_IN_DAY);
      Calendar yesterday = Calendar.getInstance();
      yesterday.setTimeInMillis(ms - MS_IN_DAY);

      List<Calendar> values = Arrays.asList(tomorrow, yesterday, tomorrow, tomorrow,
         today, today, tomorrow, yesterday, today, yesterday,
         tomorrow, tomorrow, today);
      Discriminator<Calendar> discr = new CalendarDiscriminator<Calendar>();
      List<List<Calendar>> results = discr.discriminate(values, myWorkspace);

      // Singletons first.
      List<List<Calendar>> expected = new ArrayList<List<Calendar>>();
      expected.add(Collections.nCopies(6, tomorrow));
      expected.add(Collections.nCopies(3, yesterday));
      expected.add(Collections.nCopies(4, today));

      int size = results.size();
      assertEquals(expected.size(), size);
      for (int i = 0; i < expected.size(); i++)
      {
         List<Calendar> expectedList = expected.get(i);
         List<Calendar> resultList = results.get(i);
         for (int j = 0; j < expectedList.size(); j++)
            assertEquals(expectedList.get(j).getTimeInMillis(), resultList.get(j).getTimeInMillis());
      }
   }
}