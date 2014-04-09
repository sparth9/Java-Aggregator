package net.sf.jagg.test.msd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.*;

import net.sf.jagg.msd.Discriminator;
import net.sf.jagg.msd.EnumDiscriminator;
import net.sf.jagg.msd.MsdWorkspace;

/**
 * This tests <code>EnumDiscriminators</code>.
 *
 * @author Randy Gettman
 * @since 0.5.0
 */
public class EnumDiscriminatorTest
{
   private static final MsdWorkspace myWorkspace = new MsdWorkspace();

   /**
    * A test enumerated datatype.
    */
   enum Direction
   {
      NORTH,
      NORTHEAST,
      EAST,
      SOUTHEAST,
      SOUTH,
      SOUTHWEST,
      WEST,
      NORTHWEST
   }

   /**
    * Tests discrimination by enums.
    */
   @Test
   public void testDiscrimination()
   {
      List<Direction> values = Arrays.asList(Direction.SOUTHEAST, Direction.WEST, Direction.SOUTH, Direction.WEST,
         Direction.NORTH, Direction.SOUTHWEST, Direction.NORTHWEST, Direction.EAST, Direction.SOUTHWEST,
         Direction.NORTH, Direction.WEST, Direction.NORTHEAST);
      Discriminator<Direction> discr = new EnumDiscriminator<Direction>();
      List<List<Direction>> results = discr.discriminate(values, myWorkspace);

      // Singletons first.
      List<List<Direction>> expected = new ArrayList<List<Direction>>();
      expected.add(Arrays.asList(Direction.SOUTHEAST));
      expected.add(Arrays.asList(Direction.SOUTH));
      expected.add(Arrays.asList(Direction.NORTHWEST));
      expected.add(Arrays.asList(Direction.EAST));
      expected.add(Arrays.asList(Direction.NORTHEAST));
      expected.add(Collections.nCopies(3, Direction.WEST));
      expected.add(Collections.nCopies(2, Direction.NORTH));
      expected.add(Collections.nCopies(2, Direction.SOUTHWEST));

      int size = results.size();
      assertEquals(expected.size(), size);
      for (int i = 0; i < expected.size(); i++)
      {
         List<Direction> expectedList = expected.get(i);
         List<Direction> resultList = results.get(i);
         for (int j = 0; j < expectedList.size(); j++)
            assertTrue(expectedList.get(j) == resultList.get(j));
      }
   }
}