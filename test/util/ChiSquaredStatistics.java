/*
 * Created on Nov 7, 2004 by mcphee
 */
package util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Runs a basic Chi Squared test comparing an observed distribution against an expected distribution.
 * 
 * TODO We currently only support 95% confidence level and we only support a limited set of degrees of freedom (those
 * having the form n*(n-1)/2-1) and it would be good to eventually generalize both those.
 * 
 * @author mcphee; last changed by $Author: ohsbw $ on $Date: 2005/01/31 22:48:25 $
 * @version $Revision: 1.2 $
 */
public final class ChiSquaredStatistics
{
    /**
     * Our logger.
     */
    private static final Logger LOGGER = Logger.getLogger("ChiSquaredTestLogger");

    /**
     * Maps from degrees of freedom to the corresponding Chi Squared values for the 95% confidence level.
     */
    private static Map<Integer, Double> _chiSquaredThreshholds;
    static
    {
        ChiSquaredStatistics.initializeChiSquaredThreshholds();
    }

    /**
     * Set up the Chi Squared threshholds for the 95% confidence level for a specific subset of the number of degrees of
     * freedom appropriate for the s-locus research.
     */
    private static void initializeChiSquaredThreshholds()
    {
        ChiSquaredStatistics._chiSquaredThreshholds = new HashMap<Integer, Double>();
        // ESCA-JAVA0076:
        ChiSquaredStatistics._chiSquaredThreshholds.put(new Integer(1), new Double(3.841));
        ChiSquaredStatistics._chiSquaredThreshholds.put(new Integer(2), new Double(5.99146));
        ChiSquaredStatistics._chiSquaredThreshholds.put(new Integer(3), new Double(7.815));
        ChiSquaredStatistics._chiSquaredThreshholds.put(new Integer(5), new Double(11.0705));
        ChiSquaredStatistics._chiSquaredThreshholds.put(new Integer(9), new Double(16.9189));
        ChiSquaredStatistics._chiSquaredThreshholds.put(new Integer(14), new Double(23.6848));
        ChiSquaredStatistics._chiSquaredThreshholds.put(new Integer(20), new Double(31.4104));
        ChiSquaredStatistics._chiSquaredThreshholds.put(new Integer(27), new Double(40.1133));
        ChiSquaredStatistics._chiSquaredThreshholds.put(new Integer(35), new Double(49.8018));
        ChiSquaredStatistics._chiSquaredThreshholds.put(new Integer(44), new Double(60.4809));
        ChiSquaredStatistics._chiSquaredThreshholds.put(new Integer(54), new Double(72.1532));
        ChiSquaredStatistics._chiSquaredThreshholds.put(new Integer(65), new Double(84.8206));
        ChiSquaredStatistics._chiSquaredThreshholds.put(new Integer(77), new Double(98.4844));
        ChiSquaredStatistics._chiSquaredThreshholds.put(new Integer(90), new Double(113.145));
        ChiSquaredStatistics._chiSquaredThreshholds.put(new Integer(104), new Double(128.804));
        ChiSquaredStatistics._chiSquaredThreshholds.put(new Integer(119), new Double(145.461));
        ChiSquaredStatistics._chiSquaredThreshholds.put(new Integer(135), new Double(163.116));
        ChiSquaredStatistics._chiSquaredThreshholds.put(new Integer(152), new Double(181.77));
        ChiSquaredStatistics._chiSquaredThreshholds.put(new Integer(170), new Double(201.423));
        ChiSquaredStatistics._chiSquaredThreshholds.put(new Integer(189), new Double(222.076));
        ChiSquaredStatistics._chiSquaredThreshholds.put(new Integer(209), new Double(243.727));
        ChiSquaredStatistics._chiSquaredThreshholds.put(new Integer(230), new Double(266.378));
        ChiSquaredStatistics._chiSquaredThreshholds.put(new Integer(252), new Double(290.028));
        ChiSquaredStatistics._chiSquaredThreshholds.put(new Integer(275), new Double(314.678));
        ChiSquaredStatistics._chiSquaredThreshholds.put(new Integer(299), new Double(340.328));
        ChiSquaredStatistics._chiSquaredThreshholds.put(new Integer(324), new Double(366.977));
        ChiSquaredStatistics._chiSquaredThreshholds.put(new Integer(350), new Double(394.626));
        ChiSquaredStatistics._chiSquaredThreshholds.put(new Integer(377), new Double(423.274));
        ChiSquaredStatistics._chiSquaredThreshholds.put(new Integer(405), new Double(452.923));
        ChiSquaredStatistics._chiSquaredThreshholds.put(new Integer(434), new Double(483.571));
        ChiSquaredStatistics._chiSquaredThreshholds.put(new Integer(464), new Double(515.218));
        ChiSquaredStatistics._chiSquaredThreshholds.put(new Integer(495), new Double(547.866));
        ChiSquaredStatistics._chiSquaredThreshholds.put(new Integer(527), new Double(581.513));
        ChiSquaredStatistics._chiSquaredThreshholds.put(new Integer(560), new Double(616.161));
        ChiSquaredStatistics._chiSquaredThreshholds.put(new Integer(594), new Double(651.808));
        ChiSquaredStatistics._chiSquaredThreshholds.put(new Integer(629), new Double(688.455));
        ChiSquaredStatistics._chiSquaredThreshholds.put(new Integer(665), new Double(726.102));
        ChiSquaredStatistics._chiSquaredThreshholds.put(new Integer(702), new Double(764.749));
        ChiSquaredStatistics._chiSquaredThreshholds.put(new Integer(740), new Double(804.395));
        ChiSquaredStatistics._chiSquaredThreshholds.put(new Integer(779), new Double(845.042));
        ChiSquaredStatistics._chiSquaredThreshholds.put(new Integer(819), new Double(886.688));
        ChiSquaredStatistics._chiSquaredThreshholds.put(new Integer(860), new Double(929.335));
        ChiSquaredStatistics._chiSquaredThreshholds.put(new Integer(902), new Double(972.981));
        ChiSquaredStatistics._chiSquaredThreshholds.put(new Integer(945), new Double(1017.63));
        ChiSquaredStatistics._chiSquaredThreshholds.put(new Integer(989), new Double(1063.27));
        ChiSquaredStatistics._chiSquaredThreshholds.put(new Integer(1034), new Double(1109.92));
        ChiSquaredStatistics._chiSquaredThreshholds.put(new Integer(1080), new Double(1157.57));
        ChiSquaredStatistics._chiSquaredThreshholds.put(new Integer(1127), new Double(1206.21));
        ChiSquaredStatistics._chiSquaredThreshholds.put(new Integer(1175), new Double(1255.86));
        ChiSquaredStatistics._chiSquaredThreshholds.put(new Integer(1224), new Double(1306.5));
        ChiSquaredStatistics._chiSquaredThreshholds.put(new Integer(1274), new Double(1358.15));
    }

    /**
     * We'll make the default constructor private since all the methods in this class are static and we shouldn't be
     * constructing instances of it.
     */
    private ChiSquaredStatistics()
    {
        /*
         * We'll make the default constructor private since all the methods in this class are static and we shouldn't be
         * constructing instances of it.
         */
    }

    /**
     * Returns the threshhold for the Chi Squared test for the 95% confidence level for the specified number of degrees
     * of freedom. In our s-locus work we require that the number of degrees of freedom be of the form n*(n-1)/2-1 for
     * some n>=2 since the number of degrees of freedom is a function of the number of possible genotypes, which is in
     * turn always n*(n-1)/2 for some n. If I want to generalize this into sutherland.util, I'll need to include a more
     * complete Chi Squared table and drop this assumption.
     * 
     * @param degreesOfFreedom
     *            the number of degrees of freedom for this test
     * @return the Chi Squared value for the given degrees of freedom
     */
    public static double chiSquaredThreshhold(final int degreesOfFreedom)
    {
        Integer dof = new Integer(degreesOfFreedom);
        Double result = ChiSquaredStatistics._chiSquaredThreshholds.get(dof);
        if (result == null)
        {
            throw new IllegalArgumentException(degreesOfFreedom
                    + " degrees of freedom not yet supported in Chi Squared tests");
        }
        return result.doubleValue();
    }

    /**
     * Run a simple Chi Squared test to verify that the observed frequency of genotypes comes from the desired
     * distribution with a 95% confidence.
     * 
     * @param counter
     *            the <CODE>FrequencyCounter</CODE> that contains the observed data.
     * @param expectedProportions
     *            the <CODE>Map</CODE> containing the expected proportions.
     * @return true if the Chi Squared test passes, and false otherwise
     */
    public static <T> boolean chiSquaredTest(final FrequencyCounter<T> counter, final Map<T, Double> expectedProportions)
    {
        /**
         * Compute the total number of elements in the "population" by added up all the counts in the counter. We'll
         * also assert that every observed key is in the expectedProportions map; note that it's possible for there to
         * be values in the map that were never observed so we don't require that every key in the map be in the
         * counter.
         */
        int popSize = 0;
        for (T key : counter)
        {
            popSize += counter.getCount(key);
            assert (expectedProportions.containsKey(key));
        }
        LOGGER.fine("Pop size = " + popSize);

        double chiSquared = 0;
        Iterator<T> expectedIterator = expectedProportions.keySet().iterator();
        for (Iterator<T> iter = expectedIterator; iter.hasNext();)
        {
            T key = iter.next();
            int observed = counter.getCount(key);
            double expectedProportion = expectedProportions.get(key).doubleValue();
            double expected = expectedProportion * popSize;
            double term = sqr(observed - expected) / expected;
            chiSquared += term;
            LOGGER.fine("Key = " + key + "\tExpected = " + expected + "\tObserved = " + observed);
        }
        /*
         * This uses the Chi Squared test to check that our observed distribution matches the expected distribution at
         * the 95% confidence level.
         */
        final double chiSquaredThreshhold = chiSquaredThreshhold(expectedProportions.size() - 1);
        final String valueString = "ChiSquared = " + chiSquared + " and threshhold is " + chiSquaredThreshhold;
        LOGGER.fine(valueString);
        boolean passed = chiSquared < chiSquaredThreshhold;
        LOGGER.fine("Passed = " + passed);
        return passed;
    }

    /**
     * Run a simple Chi Squared test to verify that the observed frequency of genotypes comes from a uniform
     * distribution with a 95% confidence.
     * 
     * @param counter
     *            the <CODE>FrequencyCounter</CODE> that contains the observed data.
     * @return true if the Chi Squared test passes, and false otherwise
     */
    public static <T> boolean chiSquaredTest(FrequencyCounter<T> counter)
    {
        Map<T, Double> expectedProportions = new HashMap<T, Double>();
        int numEntries = counter.numKeys();
        double uniformProbability = 1.0 / numEntries;
        for (T key : counter)
        {
            expectedProportions.put(key, uniformProbability);
        }
        return chiSquaredTest(counter, expectedProportions);
    }

    /**
     * Run a simple Chi Squared test to verify that the observed frequency of genotypes comes from the desired
     * distribution with a 95% confidence.
     * 
     * @param counts
     *            the observed counts of the various data elements
     * @param expected
     *            the expected proportions of the data elements
     * @return true if the Chi Squared test passes, and false otherwise
     */
    public static boolean chiSquaredTest(final int[] counts, final double[] expected)
    {
        FrequencyCounter<Integer> counter = new FrequencyCounter<Integer>();
        Map<Integer, Double> expectedProportions = new HashMap<Integer, Double>();
        for (int i = 0; i < counts.length; i++)
        {
            Integer index = new Integer(i);
            counter.addItem(index, counts[i]);
            expectedProportions.put(index, new Double(expected[i]));
        }
        LOGGER.fine("Count = " + counter);
        LOGGER.fine("Expected proportions = " + (expectedProportions));
        boolean result = chiSquaredTest(counter, expectedProportions);
        LOGGER.fine("Passed = " + result);
        return result;
    }

    /**
     * Square a value.
     * 
     * @param value
     *            the value to be squared
     * @return the square
     */
    public static double sqr(final double value)
    {
        return value * value;
    }

}