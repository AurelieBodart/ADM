package be.henallux.ig3.adm;

import org.apache.commons.math3.distribution.ChiSquaredDistribution;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        Scanner keyboard = new Scanner(System.in);

        ArrayList<Integer> suite = askForSuiteData(keyboard);
        JumpsTest jumpsTest = askForJumpsTestData(keyboard);

        System.out.println("Suite size : " + suite.size());
        jumpsTest.countJumps(suite);
        System.out.println("Sauts size : " + jumpsTest.getJumps().size());

        jumpsTest.generatedTab();
        for(Jump j : jumpsTest.getJumpsList())
            System.out.println(" [Saut = " + j.getSaut() +
                    " ri = " + j.getRi() +
                    " pi = " + j.getPi() +
                    " npi = " + j.getNpi());




        // Etape 4 : regrouper les npi à partir du bas du tableau si < 5
        //           calculer khi carré observé

        System.out.println("Etape 4 - npi < 5 ?");
        jumpsTest.reduceTab();
        for(Jump j : jumpsTest.getJumpsList())
            System.out.println(" [Saut = " + j.getSaut() +
                    " ri = " + j.getRi() +
                    " pi = " + j.getPi() +
                    " npi = " + j.getNpi() +
                    " (ri - npi)^2 / npi = " + j.getPartialX2Observable() + "]");






        // Etape 5 : clavier pour khi carré théorique

        System.out.println("Etape 5 - établissement de la zone de non rejet");

        Double chiCarreObservable = jumpsTest.calculChiCarreObservable();

        ChiSquaredDistribution x2 = new ChiSquaredDistribution( jumpsTest.getV() );
        double chiCarreTheorique = x2.inverseCumulativeProbability(jumpsTest.getAlpha());

        System.out.println("Chi carré observable = " + chiCarreObservable);
        System.out.println("Chi carré théorique = " + chiCarreTheorique);

        // Etape 6 - rejeter h0 ou non en comparant khi carré théorique et observé

        System.out.println("Etape 6 - Rejet ou non de H0");
        System.out.println("Rappel :" +
                "\nH0 = " + jumpsTest.getH0() +
                "\nH1 = " + jumpsTest.getH1());

        if(chiCarreObservable > chiCarreTheorique)
            System.out.println("H0 est rejeté");
        else
            System.out.println("H0 est n'est pas rejeté avec un degré d'incertitude de " + jumpsTest.getAlpha());
    }

    public static ArrayList<Integer> askForSuiteData(Scanner keyboard) {
        boolean isSuiteGood;
        int x0, c, a, m;
        GenerationSuite suite;

        System.out.println("Génération de la suite");
        System.out.println("-----------------------------------------------");

        do {
            System.out.print("Quel est votre a ? ");
            a = keyboard.nextInt();
            System.out.print("Quel est votre c ? ");
            c = keyboard.nextInt();
            System.out.print("Quel est votre m ? ");
            m = keyboard.nextInt();
            System.out.print("Quel est votre x0 ? ");
            x0 = keyboard.nextInt();

            suite = new GenerationSuite(x0, c, a, m);
            isSuiteGood = suite.isHullDobellProof();

            if (!isSuiteGood)
                System.out.println("Erreur : la suite ne respecte pas les critères de Hull-Dobell.\nChoisissez d'autres valeurs !");
        } while (!isSuiteGood);

        // TODO: console clear

        return suite.generateYnList(suite.generateUnList(suite.generateXnList()));
    }

    public static JumpsTest askForJumpsTestData(Scanner keyboard) {
        String h0, h1;
        double alpha;
        int valueNbr;

        System.out.println("-----------------------------------------------");
        System.out.println("Test des sauts");
        System.out.println("Etape 1 - hypothèses");

        keyboard.nextLine();

        System.out.println("Quelle est votre hypothèse h0 ?");
        h0 = keyboard.nextLine();

        System.out.println("Quelle est votre hypothèse h1 ?");
        h1 = keyboard.nextLine();

        System.out.println("\nEtape 2 - niveau d'incertitude / alpha");
        System.out.println("Quelle est votre alpha ?");
        alpha = keyboard.nextDouble();

        System.out.println("\nEtape 3 - Génération des tableaux de fréquence");
        do {
            System.out.println("Quelle valeur voulez-vous utiliser (entre 0 et 9) ?");
            valueNbr = keyboard.nextInt();
            if (valueNbr < 0 || valueNbr > 9)
                System.out.println("Erreur : Vous devez choisir une valeur entre 0 et 9 !");
        } while (valueNbr < 0 || valueNbr > 9);

        return new JumpsTest(h0, h1, alpha, valueNbr);
    }
}
