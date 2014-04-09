package acceptancetest;

import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;

import net.sf.jagg.test.model.CountyData;
import net.sf.jagg.test.model.Record;

/**
 * A utility class to provide data to test the <code>net.sf.jagg</code>
 * package.
 *
 * @author Randy Gettman
 * @since 0.3.0
 */
@Ignore
public class TestUtility
{
   /**
    * Tests using decimals must be off by less than this factor to be
    * considered correct.  This is necessary due to the inexact nature of
    * floating-point operations.
    */
   public static final double DELTA = Math.ulp(1);

   /**
    * Retrieve a standard <code>List</code> of <code>Records</code> with which
    * to test.
    * @return A <code>List</code> of <code>Records</code>.
    */
   public static List<Record> getTestData()
   {
      List<Record> recList = new ArrayList<Record>();
      recList.add(new Record("AAA", 1, "zzz", "baseball", 10, 6.5));
      recList.add(new Record("AAA", 1, "zzz", "basketball", 11, 1.338));
      recList.add(new Record("AAA", 1, "yyy", "baseball", 23, 3.14159));
      recList.add(new Record("AAA", 1, "yyy", "basketball", 7, 2.71828));
      recList.add(new Record("AAA", 2, "zzz", "baseball", 1, 201.5));
      recList.add(new Record("AAA", 2, "zzz", "basketball", 3, 68.67));
      recList.add(new Record("AAA", 2, "yyy", "baseball", 323, 92.15));
      recList.add(new Record("AAA", 2, "yyy", "basketball", 710, 2.25));
      recList.add(new Record("BBB", 1, "zzz", "baseball", 1111, 6.25));
      recList.add(new Record("BBB", 1, "zzz", "basketball", 1001, 12.25));
      recList.add(new Record("BBB", 1, "yyy", "baseball", 99, 20.25));  // 10
      recList.add(new Record("BBB", 1, "yyy", "basketball", 68, 30.25));
      recList.add(new Record("BBB", 2, "zzz", "baseball", 405, 42.25));
      recList.add(new Record("BBB", 2, "zzz", "basketball", 80, 56.25));
      recList.add(new Record("BBB", 2, "yyy", "baseball", 55, 72.25));
      recList.add(new Record("BBB", 2, "yyy", "basketball", 5, 90.25));
      recList.add(new Record("BBB", 2, "yyy", "basketball", 1005, 110.25));
      recList.add(new Record("CCC", 1, "abcd", "football", 1, 2));
      recList.add(new Record("CCC", 1, "abcd", "football", 2, 5));
      recList.add(new Record("CCC", 1, "abcd", "football", 3, 7));
      recList.add(new Record("CCC", 1, "abcd", "football", 4, 10));  // 20
      recList.add(new Record("CCC", 2, "efgh", "football", 1, 1));
      recList.add(new Record("CCC", 2, "efgh", "football", 2, 3));
      recList.add(new Record("CCC", 2, "efgh", "football", 3, 7));
      recList.add(new Record("CCC", 2, "efgh", "football", 4, 10));
      recList.add(new Record("CCC", 2, "efgh", "football", 5, 11));
      recList.add(new Record("CCC", 2, "efgh", "football", 6, 23));
      recList.add(new Record("CCC", 3, "ijk", "football", 1, 2));
      recList.add(new Record("CCC", 3, "ijk", "football", 2, 3));
      recList.add(new Record("CCC", 3, "ijk", "football", 3, 4));
      recList.add(new Record("CCC", 3, "ijk", "football", 4, 6));  // 30
      recList.add(new Record("CCC", 3, "ijk", "football", 5, 54));

      return recList;
   }

   /**
    * Retrieve a small <code>List</code> of <code>Records</code> with which
    * to test, some <code>Records</code> of which contain <code>null</code>
    * data.
    * @return A <code>List</code> of <code>Records</code>.
    */
   public static List<Record> getSomeNullData()
   {
      List<Record> records = new ArrayList<Record>();
      records.add(new Record("abcd", 0, "not null", "efgh", 2, 2.3));
      records.add(new Record("efgh", 18, null, "efgh", 3, 3.4));
      records.add(new Record("wxyz", 1, null, "efgh", 1, 1.2));
      records.add(new Record("abcd", 1, "not null", "efgh", 4, 4.5));
      records.add(new Record("wxyz", 0, "not null", "efgh", 5, 5.6));
      records.add(new Record("efgh", 18, null, "efgh", 6, 6.7));
      return records;
   }

   /**
    * Retrieve a <code>List</code> of <code>Integers</code> with which to test.
    * @return A <code>List</code> of <code>Integers</code>.
    */
   public static List<Integer> getSomeNumericData()
   {
      List<Integer> integers = new ArrayList<Integer>();
      integers.add(35);
      integers.add(89);
      integers.add(41);
      integers.add(94);
      integers.add(7);
      integers.add(76);
      integers.add(42);
      integers.add(93);
      integers.add(76);
      integers.add(87);
      return integers;
   }

   /**
    * Retrieve a <code>List</code> of <code>CountyDatas</code> with which to
    * test.
    * @return A <code>List</code> of <code>CountyDatas</code>.
    */
   public static List<CountyData> getSomeCountyData()
   {
      List<CountyData> countyDataList = new ArrayList<CountyData>();

      CountyData losAngeles = new CountyData();
      losAngeles.setCountyName("Los Angeles");
      losAngeles.setStateCode("CA");
      losAngeles.setFipsCode("06037");
      losAngeles.setValue(CountyData.MonthCode.CURR_MONTH, 4 /* searches */, 2996627);
      losAngeles.setValue(CountyData.MonthCode.CURR_MONTH, 1 /* rpts billed */, 1862887);
      losAngeles.setValue(CountyData.MonthCode.CURR_MONTH, 25 /* srches + bills */, 4859514);
      losAngeles.setValue(CountyData.MonthCode.LAST_MONTH, 4 /* searches */, 2698701);
      losAngeles.setValue(CountyData.MonthCode.LAST_MONTH, 1 /* rpts billed */, 1802169);
      losAngeles.setValue(CountyData.MonthCode.LAST_MONTH, 25 /* srches + bills */, 4500870);
      losAngeles.setValue(CountyData.MonthCode.YEAR_AGO_MONTH, 4 /* searches */, 2184042);
      losAngeles.setValue(CountyData.MonthCode.YEAR_AGO_MONTH, 1 /* rpts billed */, 1758396);
      losAngeles.setValue(CountyData.MonthCode.YEAR_AGO_MONTH, 25 /* srches + bills */, 3942438);
      countyDataList.add(losAngeles);

      CountyData orangeCa = new CountyData();
      orangeCa.setCountyName("Orange");
      orangeCa.setStateCode("CA");
      orangeCa.setFipsCode("06059");
      orangeCa.setValue(CountyData.MonthCode.CURR_MONTH, 4 /* searches */, 1028003);
      orangeCa.setValue(CountyData.MonthCode.CURR_MONTH, 1 /* rpts billed */, 727273);
      orangeCa.setValue(CountyData.MonthCode.CURR_MONTH, 25 /* srches + bills */, 1755276);
      orangeCa.setValue(CountyData.MonthCode.LAST_MONTH, 4 /* searches */, 956921);
      orangeCa.setValue(CountyData.MonthCode.LAST_MONTH, 1 /* rpts billed */, 669895);
      orangeCa.setValue(CountyData.MonthCode.LAST_MONTH, 25 /* srches + bills */, 1626816);
      orangeCa.setValue(CountyData.MonthCode.YEAR_AGO_MONTH, 4 /* searches */, 944794);
      orangeCa.setValue(CountyData.MonthCode.YEAR_AGO_MONTH, 1 /* rpts billed */, 772655);
      orangeCa.setValue(CountyData.MonthCode.YEAR_AGO_MONTH, 25 /* srches + bills */, 1717449);
      countyDataList.add(orangeCa);

      CountyData sanDiego = new CountyData();
      sanDiego.setCountyName("San Diego");
      sanDiego.setStateCode("CA");
      sanDiego.setFipsCode("06073");
      sanDiego.setValue(CountyData.MonthCode.CURR_MONTH, 4 /* searches */, 1190452);
      sanDiego.setValue(CountyData.MonthCode.CURR_MONTH, 1 /* rpts billed */, 764024);
      sanDiego.setValue(CountyData.MonthCode.CURR_MONTH, 25 /* srches + bills */, 1954476);
      sanDiego.setValue(CountyData.MonthCode.LAST_MONTH, 4 /* searches */, 1144618);
      sanDiego.setValue(CountyData.MonthCode.LAST_MONTH, 1 /* rpts billed */, 752559);
      sanDiego.setValue(CountyData.MonthCode.LAST_MONTH, 25 /* srches + bills */, 1897177);
      sanDiego.setValue(CountyData.MonthCode.YEAR_AGO_MONTH, 4 /* searches */, 1226714);
      sanDiego.setValue(CountyData.MonthCode.YEAR_AGO_MONTH, 1 /* rpts billed */, 892998);
      sanDiego.setValue(CountyData.MonthCode.YEAR_AGO_MONTH, 25 /* srches + bills */, 2119712);
      countyDataList.add(sanDiego);

      CountyData palmBeach = new CountyData();
      palmBeach.setCountyName("Palm Beach");
      palmBeach.setStateCode("FL");
      palmBeach.setFipsCode("12099");
      palmBeach.setValue(CountyData.MonthCode.CURR_MONTH, 4 /* searches */, 318482);
      palmBeach.setValue(CountyData.MonthCode.CURR_MONTH, 1 /* rpts billed */, 530492);
      palmBeach.setValue(CountyData.MonthCode.CURR_MONTH, 25 /* srches + bills */, 848974);
      palmBeach.setValue(CountyData.MonthCode.LAST_MONTH, 4 /* searches */, 314194);
      palmBeach.setValue(CountyData.MonthCode.LAST_MONTH, 1 /* rpts billed */, 557727);
      palmBeach.setValue(CountyData.MonthCode.LAST_MONTH, 25 /* srches + bills */, 871921);
      palmBeach.setValue(CountyData.MonthCode.YEAR_AGO_MONTH, 4 /* searches */, 289068);
      palmBeach.setValue(CountyData.MonthCode.YEAR_AGO_MONTH, 1 /* rpts billed */, 470647);
      palmBeach.setValue(CountyData.MonthCode.YEAR_AGO_MONTH, 25 /* srches + bills */, 759715);
      countyDataList.add(palmBeach);

      CountyData dade = new CountyData();
      dade.setCountyName("Miami-Dade");
      dade.setStateCode("FL");
      dade.setFipsCode("12086");
      dade.setValue(CountyData.MonthCode.CURR_MONTH, 4 /* searches */, 370951);
      dade.setValue(CountyData.MonthCode.CURR_MONTH, 1 /* rpts billed */, 473108);
      dade.setValue(CountyData.MonthCode.CURR_MONTH, 25 /* srches + bills */, 844059);
      dade.setValue(CountyData.MonthCode.LAST_MONTH, 4 /* searches */, 302736);
      dade.setValue(CountyData.MonthCode.LAST_MONTH, 1 /* rpts billed */, 309255);
      dade.setValue(CountyData.MonthCode.LAST_MONTH, 25 /* srches + bills */, 611991);
      dade.setValue(CountyData.MonthCode.YEAR_AGO_MONTH, 4 /* searches */, 280993);
      dade.setValue(CountyData.MonthCode.YEAR_AGO_MONTH, 1 /* rpts billed */, 532281);
      dade.setValue(CountyData.MonthCode.YEAR_AGO_MONTH, 25 /* srches + bills */, 813274);
      countyDataList.add(dade);

      CountyData broward = new CountyData();
      broward.setCountyName("Broward");
      broward.setStateCode("FL");
      broward.setFipsCode("12011");
      broward.setValue(CountyData.MonthCode.CURR_MONTH, 4 /* searches */, 252086);
      broward.setValue(CountyData.MonthCode.CURR_MONTH, 1 /* rpts billed */, 243507);
      broward.setValue(CountyData.MonthCode.CURR_MONTH, 25 /* srches + bills */, 495593);
      broward.setValue(CountyData.MonthCode.LAST_MONTH, 4 /* searches */, 210444);
      broward.setValue(CountyData.MonthCode.LAST_MONTH, 1 /* rpts billed */, 204912);
      broward.setValue(CountyData.MonthCode.LAST_MONTH, 25 /* srches + bills */, 415356);
      broward.setValue(CountyData.MonthCode.YEAR_AGO_MONTH, 4 /* searches */, 186512);
      broward.setValue(CountyData.MonthCode.YEAR_AGO_MONTH, 1 /* rpts billed */, 293974);
      broward.setValue(CountyData.MonthCode.YEAR_AGO_MONTH, 25 /* srches + bills */, 480486);
      countyDataList.add(broward);

      return countyDataList;
   }
}
