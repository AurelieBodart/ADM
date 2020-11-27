package be.henallux.ig3.adm;

import java.util.*;

public class JumpsTest {
    private String h0, h1;
    private double alpha;
    private int valueNbr;
    private ArrayList<Integer> jumps;
    private ArrayList<Jump> jumpsList;

    public JumpsTest(String h0, String h1, double alpha, int valueNbr) {
        this.h0 = h0;
        this.h1 = h1;
        this.alpha = alpha;
        this.valueNbr = valueNbr;
        jumps = new ArrayList<>();
        jumpsList = new ArrayList<>();
    }

    public ArrayList<Integer> getJumps() {
        return jumps;
    }
    public ArrayList<Jump> getJumpsList() { return jumpsList; }

    public Integer getV(){
        return jumpsList.size() - 1;
    }

    public Double getAlpha() {
        return alpha;
    }

    public String getH0() {
        return h0;
    }

    public String getH1() {
        return h1;
    }

    public void countJumps(ArrayList<Integer> suite) {
        int jumpCount = 0;
        int i = 0;

        while (i < suite.size() && suite.get(i) != valueNbr)
            i++;

        for (int iSuite = i + 1; iSuite < suite.size(); iSuite++) {
            if (suite.get(iSuite) == valueNbr) {
                jumps.add(jumpCount);
                jumpCount = 0;
            } else jumpCount++;
        }
    }

    public void generatedTab() {
        countRi();
        Collections.sort(jumpsList);

        fillGaps();
        Collections.sort(jumpsList);

        // calculer pi
        for (int i = 0; i < jumpsList.size(); i++) {
            jumpsList.get(i).setPi(Math.pow(0.9, i) * 0.1);
        }

        // réduire nombre de lignes si plus grand que a
        int a = calculateA();
        System.out.println("a = " + a);
        if (jumpsList.size() > a)
            sumWithA(a);

        // calculer npi
        int n = jumps.size();
        for(Jump jump : jumpsList)
            jump.setNpi(jump.getPi() * n);

    }

    private void countRi(){
        for (int jump : jumps) {

            int i = 0;
            while (i < jumpsList.size() && jumpsList.get(i).getSaut() != jump)
                i++;

            if (i == jumpsList.size()) {
                jumpsList.add(new Jump(jump, 1));
            } else {
                jumpsList.get(i).setRi(jumpsList.get(i).getRi() + 1);
            }
        }
    }

    private void fillGaps(){
        int maxSizeSaut = jumpsList.get(jumpsList.size() - 1).getSaut();
        for(int i = 0; i < maxSizeSaut; i++){
            int j = 0;
            while (j < jumpsList.size() && jumpsList.get(j).getSaut() != i)
                j++;

            if(j == jumpsList.size())
                jumpsList.add(new Jump(i, 0));
        }
    }

    private int calculateA() {
        return (int) Math.round(Math.log(5. / jumps.size()) / Math.log(0.9));
    }

    private void sumWithA(int a){
        int iA = a - 1;
        for (int i = jumpsList.size() - 1; i >= a; i--){
            int newRi = jumpsList.get(iA).getRi() + jumpsList.get(i).getRi();
            double newPi = jumpsList.get(iA).getPi() + jumpsList.get(i).getPi();

            jumpsList.get(iA).setRi(newRi);
            jumpsList.get(iA).setPi(newPi);
            jumpsList.remove(jumpsList.get(i));
        }
    }

    public void reduceTab(){
        for(int i = jumpsList.size()- 1; i > 0; i--){
            if(jumpsList.get(i).getNpi() < 5) {
                double newNPi = jumpsList.get(i).getNpi() + jumpsList.get(i - 1).getNpi();
                jumpsList.get(i - 1).setNpi(newNPi);
                jumpsList.remove(jumpsList.get(i));
            }
        }

        // calculer ((ri-n*pi)^2)/(n*pi)
        for(Jump jump : jumpsList)
            jump.setPartialX2Observable(Math.pow(jump.getRi()-jump.getNpi(), 2) / jump.getNpi());

    }

    public Double calculChiCarreObservable() {
        Double chiCarreObservable = 0.;

        for(Jump jump : jumpsList)
            chiCarreObservable += jump.getPartialX2Observable();

        return chiCarreObservable;
    }
}
