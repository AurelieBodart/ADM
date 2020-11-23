package be.henallux.ig3.adm;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class JumpsTest {
    private String h0, h1;
    private double alpha;
    private int valueNbr;
    private ArrayList<Integer> jumps;

    public JumpsTest(String h0, String h1, double alpha, int valueNbr) {
        this.h0 = h0;
        this.h1 = h1;
        this.alpha = alpha;
        this.valueNbr = valueNbr;
        jumps = new ArrayList<>();
    }

    public ArrayList<Integer> getJumps() {
        return jumps;
    }

    public HashMap<Integer, List<Double>> generatedTab() {
        HashMap<Integer, List<Double>> hashMap = new HashMap<>();
        HashMap<Integer, Double> temp = new HashMap<>();

        for (int jump : jumps) {
            if (temp.containsKey(jump)) {
                double nbrOccurrences = temp.get(jump);

                temp.remove(jump);
                temp.put(jump, nbrOccurrences + 1);
            } else {
                temp.put(jump, 1.);
            }
        }

        int i = 0;
        ArrayList<Integer> integersList = new ArrayList<>(temp.keySet());
        // integersList.get(integersList.size() - 1);
        for (Map.Entry<Integer, Double> entry : temp.entrySet()) {
            ArrayList<Double> list = new ArrayList<>();

            list.add(i == entry.getKey() ? entry.getValue() : 0.);
            hashMap.put(i, list);
            i++;
        }

        i = 0;

        for (Map.Entry<Integer, List<Double>> integerListEntry : hashMap.entrySet()) {
            integerListEntry.getValue().add(Math.pow(0.9, i) * 0.1);
            i++;
        }

        int a = calculateA();
        if (hashMap.size() > a) {
            // for (i = hashMap.size() - 1; i > )
        }

        return hashMap;
    }

    public void countJumps(ArrayList<Integer> suite) {
        int jumpCount = 0;
        int i = 0;

        while (i < suite.size() && suite.get(i) != valueNbr) i++;

        for (int iSuite = i + 1; iSuite < suite.size(); iSuite++) {
            if (suite.get(iSuite) == valueNbr) {
                jumps.add(jumpCount);
                jumpCount = 0;
            } else jumpCount++;
        }
    }

    public int calculateA() {
        return (int) Math.round(Math.log(5. / jumps.size()) / Math.log(0.9));
    }
}
