package net.sf.jagg.test.msd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.*;

import net.sf.jagg.msd.CollectionDiscriminator;
import net.sf.jagg.msd.Discriminator;
import net.sf.jagg.msd.MsdWorkspace;

/**
 * This tests <code>CollectionDiscriminators</code>.
 *
 * @author Randy Gettman
 * @since 0.5.0
 */
public class CollectionDiscriminatorTest
{
   private static final MsdWorkspace myWorkspace = new MsdWorkspace();

   /**
    * Tests discrimination by collections.
    */
   @Test
   public void testDiscrimination()
   {
      List<Collection<String>> values = new ArrayList<Collection<String>>();
      LinkedList<String> list0 = new LinkedList<String>();
      values.add(list0);

      LinkedList<String> list1 = new LinkedList<String>();
      list1.addAll(Arrays.asList("Test", "Java"));
      values.add(list1);

      LinkedList<String> list2 = new LinkedList<String>();
      list2.addAll(Arrays.asList("Java", "Test"));
      values.add(list2);

      LinkedList<String> list3 = new LinkedList<String>();
      list3.add("");
      values.add(list3);

      values.add(list0);

      values.add(list1);

      LinkedList<String> list4 = new LinkedList<String>();
      list4.addAll(Arrays.asList("This", "is", "a", "really", "long", "complete", "sentence", "created", "just", "to", "test", "longer", "lists."));
      values.add(list4);

      LinkedList<String> list5 = new LinkedList<String>();
      list5.addAll(Arrays.asList("One", "Two", "Three", "Four"));
      values.add(list5);

      values.add(list2);

      LinkedList<String> list6 = new LinkedList<String>();
      list6.addAll(Arrays.asList("Java", "Test", "List"));
      values.add(list6);

      values.add(list3);

      values.add(list0);

      LinkedList<String> list7 = new LinkedList<String>();
      list7.addAll(Arrays.asList("This", "is", "a", "shorter", "sentence."));
      values.add(list7);

      Discriminator<Collection<String>> discr = new CollectionDiscriminator<String>();
      List<List<Collection<String>>> results = discr.discriminate(values, myWorkspace);

      // Singletons first.
      List<List<Collection<String>>> expected = new ArrayList<List<Collection<String>>>();
      expected.add(Collections.nCopies(3, (Collection<String>) list0));
      expected.add(Collections.nCopies(1, (Collection<String>) list5));
      expected.add(Collections.nCopies(2, (Collection<String>) list3));
      expected.add(Collections.nCopies(2, (Collection<String>) list1));
      expected.add(Collections.nCopies(2, (Collection<String>) list2));
      expected.add(Collections.nCopies(1, (Collection<String>) list6));
      expected.add(Collections.nCopies(1, (Collection<String>) list4));
      expected.add(Collections.nCopies(1, (Collection<String>) list7));

      int size = results.size();
      assertEquals(expected.size(), size);
      for (int i = 0; i < expected.size(); i++)
      {
         List<Collection<String>> expectedList = expected.get(i);
         List<Collection<String>> resultList = results.get(i);
         for (int j = 0; j < expectedList.size(); j++)
         {
            Collection<String> expectedStringCollection = expectedList.get(j);
            Collection<String> resultStringCollection = resultList.get(j);
            assertEquals(expectedStringCollection.size(), resultStringCollection.size());
            Iterator<String> expectedItr = expectedStringCollection.iterator();
            Iterator<String> resultItr = resultStringCollection.iterator();
            while (expectedItr.hasNext())
               assertEquals(expectedItr.next(), resultItr.next());
         }
      }
   }
}