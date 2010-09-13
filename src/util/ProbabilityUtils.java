package util;

import java.util.HashMap;

public class ProbabilityUtils {
    public static int factorial(int n) {
        int num = 1;
        for(int i=1; i<=n; i++) {
            num *= i;
        }
        return num;
    }
    
    public static int choose(int n, int k) {
        return factorial(n)/(factorial(k)*factorial(n-k));
    }
    
    public static HashMap<Integer, Double> binomialDistribution(int n, double p) {
        HashMap<Integer, Double> map = new HashMap<Integer, Double>();
        for(int k=0; k<=n; k++) {
            map.put(k, binomialProbability(n, k, p));
        }
        return map;
    }
    
    public static double binomialProbability(int n, int k, double p) {
        return choose(n, k)*Math.pow(p, k)*Math.pow(1-p, n-k);
    }
}
