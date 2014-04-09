package net.sf.jagg.test.msd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import net.sf.jagg.PropertiesComparator;
import net.sf.jagg.msd.Discriminators;
import net.sf.jagg.msd.MsdWorkspace;
import net.sf.jagg.msd.PropertiesDiscriminator;
import net.sf.jagg.test.model.Record;

/**
 * This will compare the performance of MSDs vs. <code>Collections.sort</code>.
 *
 * @author Randy Gettman
 * @since 0.5.0
 */
public class MsdPerformanceTest
{
   /**
    * Test MSDs vs. <code>Collections.sort</code>.  Runs sort first.
    */
   @Test
   public void testPerformanceSortFirst()
   {
      List<Record> recordsToSort = new ArrayList<Record>(115000);
      for (int i = 0; i < 115000; i++)
         recordsToSort.add(new Record("A", i / 10000, "B", "C", i, (double) i / 100 + 1));
      List<Record> recordsToMsd = new ArrayList<Record>(recordsToSort);
      MsdWorkspace workspace = new MsdWorkspace();

      List<String> properties = Arrays.asList("category1", "category2");
      PropertiesComparator<Record> propComp = new PropertiesComparator<Record>(properties);
      PropertiesDiscriminator<Record> propDiscr = new PropertiesDiscriminator<Record>(properties);

      long beginSortNanos, endSortNanos, beginMsdNanos, endMsdNanos;
      beginSortNanos = System.nanoTime();
      Collections.sort(recordsToSort, propComp);
      endSortNanos = System.nanoTime();

      //System.out.println("Collections.sort: " + (endSortNanos - beginSortNanos));

      beginMsdNanos = System.nanoTime();
      Discriminators.getFlattenedList(propDiscr.discriminate(recordsToMsd, workspace));
      endMsdNanos = System.nanoTime();

      //System.out.println("PD.discriminate : " + (endMsdNanos - beginMsdNanos));
   }

   /**
    * Test MSDs vs. <code>Collections.sort</code>.  Runs MSD first.
    */
   @Test
   public void testPerformanceMsdFirst()
   {
      List<Record> recordsToMsd = new ArrayList<Record>(115000);
      for (int i = 0; i < 115000; i++)
         recordsToMsd.add(new Record("A", i / 10000, "B", "C", i, (double) i / 100 + 1));
      List<Record> recordsToSort = new ArrayList<Record>(recordsToMsd);
      MsdWorkspace workspace = new MsdWorkspace();

      List<String> properties = Arrays.asList("category1", "category2");
      PropertiesComparator<Record> propComp = new PropertiesComparator<Record>(properties);
      PropertiesDiscriminator<Record> propDiscr = new PropertiesDiscriminator<Record>(properties);

      long beginSortNanos, endSortNanos, beginMsdNanos, endMsdNanos;
      beginMsdNanos = System.nanoTime();
      Discriminators.getFlattenedList(propDiscr.discriminate(recordsToMsd, workspace));
      endMsdNanos = System.nanoTime();

      //System.out.println("PD.discriminate : " + (endMsdNanos - beginMsdNanos));

      beginSortNanos = System.nanoTime();
      Collections.sort(recordsToSort, propComp);
      endSortNanos = System.nanoTime();

      //System.out.println("Collections.sort: " + (endSortNanos - beginSortNanos));
   }
}
