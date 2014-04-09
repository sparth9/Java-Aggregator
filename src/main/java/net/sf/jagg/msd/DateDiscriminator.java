package net.sf.jagg.msd;

import java.util.Date;
import java.util.List;

/**
 * A <code>DateDiscriminator</code> discriminates <code>Dates</code>.
 *
 * @author Randy Gettman
 * @since 0.6.0
 */
public class DateDiscriminator<T extends Date> extends ChainedDiscriminator<T>
{
   /**
    * Returns an appropriate <code>ChainedExtractor</code>.
    * @param elements The <code>List</code> of elements.
    * @param extractor A <code>ChainedExtractor</code> that returns appropriate
    *    labels.
    * @return An appropriate <code>ChainedExtractor</code>.
    */
   protected <E> ChainedExtractor<E, Long, T> getChainedExtractor(List<E> elements, Extractor<E, T> extractor)
   {
      return new DateChainedExtractor<E>(extractor);
   }

   /**
    * A <code>DateChainedExtractor</code> extracts the milliseconds value of
    * the <code>Date</code>.
    * @param <E> The type of element.
    */
   protected class DateChainedExtractor<E> extends ChainedExtractor<E, Long, T>
   {
      /**
       * Create an <code>EnumChainedExtractor</code> that uses the given
       * <code>Extractor</code> to retrieve the array.
       * @param extractor An <code>Extractor</code> whose label is a
       *    <code>long</code>.
       */
      public DateChainedExtractor(Extractor<E, T> extractor)
      {
         super(extractor);
      }

      /**
       * The label is the milliseconds value of the <code>Date</code>.
       * @param element The element.
       * @return The milliseconds value of the <code>Date</code>.
       */
      public Long getLabel(E element)
      {
         return myExtractor.getLabel(element).getTime();
      }

      /**
       * The discrimination is complete when the chained <code>Extractor</code>
       * is complete.
       * @param element The element.
       * @return <code>true</code> if complete, <code>false</code> otherwise.
       */
      public boolean isComplete(E element)
      {
         // Only check the ordinal once!
         return myExtractor.isComplete(element) || myIndex >= 1;
      }
   }

   /**
    * Returns the <code>Discriminator</code> that discriminates on the
    * milliseconds value of the <code>Date</code>.
    * @param elements The list of elements.
    * @param extractor The <code>ChainedExtractor</code> that was obtained from
    *    <code>getChainedExtractor</code>.
    * @param index The index of the loop.
    * @return A <code>Discriminator</code> that discriminates on the
    *    milliseconds value of the <code>Date</code>.
    */
   protected <E> Discriminator<Long> getDiscriminator(List<E> elements, ChainedExtractor<E, ?, T> extractor,
      int index)
   {
      return new LongDiscriminator();
   }
}
