package be.henallux.ig3.adm;

import java.util.ArrayList;

public class GenerationSuite {
    private int x0, c, a, m;


    public GenerationSuite(int x0, int c, int a, int m) {
        this.x0 = x0;
        this.c = c;
        this.a = a;
        this.m = m;
    }

    public boolean isHullDobellProof() {
        return cAndMAreCoprime() && everyPPrimeOfMHasAMinus1AsMultiple() && mAndAMinus1AreMultipleOf4();
    }

    private boolean cAndMAreCoprime() {
        for (int i = 2; i <= c; i++) {
            if (m % i == 0 && c % i == 0)
                return false;
        }
        return true;
    }

    private boolean everyPPrimeOfMHasAMinus1AsMultiple() {
        return primesRecursive(m, 2)
                .stream()
                .allMatch(prime -> (a - 1) % prime == 0);
    }

    private static ArrayList<Integer> primesRecursive(int n, int f) {
        if (n == 1) return new ArrayList<>();

        if (n % f == 0) {
            ArrayList<Integer> factors = primesRecursive(n/f, f);
            factors.add(f);

            return factors;
        }

        return primesRecursive(n, f+1);
    }

    private boolean mAndAMinus1AreMultipleOf4() {
        return m % 4 != 0 || (a - 1) % 4 == 0;
    }

    public int generateXn() {
        int xtemp = (a * x0 + c) % m;

        x0 = xtemp;
        return xtemp;
    }

    public ArrayList<Integer> generateXnList() {
        ArrayList<Integer> xnList = new ArrayList<>();

        for (int i = 0; i < m; i++)
            xnList.add(generateXn());

        return xnList;
    }

    public double generateUn(int xn) {
        return xn / (double) m;
    }

    public ArrayList<Double> generateUnList(ArrayList<Integer> xnList) {
        ArrayList<Double> unList = new ArrayList<>();

        for (int xn : xnList)
            unList.add(generateUn(xn));

        return unList;
    }

    public int generateYn(double un) {
        return (int) (un * 10);
    }

    public ArrayList<Integer> generateYnList(ArrayList<Double> unList) {
        ArrayList<Integer> ynList = new ArrayList<>();

        for (double un : unList)
            ynList.add(generateYn(un));

        return ynList;
    }
}
