package net.sf.jagg.test.msd;

/**
 * Just to run MsdPerformanceTest in the command line.
 */
public class MsdPerfTestMain
{
   public static void main(String[] args)
   {
      MsdPerformanceTest mpt = new MsdPerformanceTest();
      mpt.testPerformanceSortFirst();
      mpt.testPerformanceMsdFirst();
   }
}
