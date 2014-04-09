package net.sf.jagg.test.msd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.*;

import net.sf.jagg.msd.CharacterDiscriminator;
import net.sf.jagg.msd.Discriminator;
import net.sf.jagg.msd.MsdWorkspace;

/**
 * This tests <code>CharacterDiscriminators</code>.
 *
 * @author Randy Gettman
 * @since 0.5.0
 */
public class CharacterDiscriminatorTest
{
   private static final MsdWorkspace myWorkspace = new MsdWorkspace();

   /**
    * Tests discrimination by characters.
    */
   @Test
   public void testDiscrimination()
   {
      List<Character> values = Arrays.asList('A', 'Z', ' ', 'A', 'z', 'Z', 'z', '\n', '\r', '\b', '\f', '\r', '\t',
         '¢', '$', '\0', '\u03C0', '\u215B', '\u03C0', '\n', '!', 'Z', '¢');
      Discriminator<Character> discr = new CharacterDiscriminator();
      List<List<Character>> results = discr.discriminate(values, myWorkspace);

      // Singletons first.
      List<List<Character>> expected = new ArrayList<List<Character>>();
      expected.add(Collections.nCopies(2, 'A'));
      expected.add(Collections.nCopies(3, 'Z'));
      expected.add(Arrays.asList(' '));
      expected.add(Collections.nCopies(2, 'z'));
      expected.add(Collections.nCopies(2, '\n'));
      expected.add(Collections.nCopies(2, '\r'));
      expected.add(Arrays.asList('\b'));
      expected.add(Arrays.asList('\f'));
      expected.add(Arrays.asList('\t'));
      expected.add(Collections.nCopies(2, '¢'));
      expected.add(Arrays.asList('$'));
      expected.add(Arrays.asList('\0'));
      expected.add(Collections.nCopies(2, '\u03C0'));
      expected.add(Arrays.asList('\u215B'));
      expected.add(Arrays.asList('!'));

      int size = results.size();
      assertEquals(expected.size(), size);
      for (int i = 0; i < expected.size(); i++)
      {
         List<Character> expectedList = expected.get(i);
         List<Character> resultList = results.get(i);
         for (int j = 0; j < expectedList.size(); j++)
            assertEquals(expectedList.get(j), resultList.get(j));
      }
   }
}