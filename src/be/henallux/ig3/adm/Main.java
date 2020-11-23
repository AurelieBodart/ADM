package be.henallux.ig3.adm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner keyboard = new Scanner(System.in);
        ArrayList<Integer> suite = askForSuiteData(keyboard);
        JumpsTest jumpsTest = askForJumpsTestData(keyboard);

        System.out.println("Suite : " + suite);
        jumpsTest.countJumps(suite);
        System.out.println("Sauts : " + jumpsTest.getJumps());

        jumpsTest.generatedTab();
        for(Jump j : jumpsTest.getJumpsList())
            System.out.println(" [Saut = " + j.getSaut() + " ri = " + j.getRi() + " pi = " + j.getPi() + " npi = " + j.getNpi() + "]");

        // Etape 4 : regrouper les npi à partir du bas du tableau si < 5
        //           calculer khi carré observé

        // Etape 5 : clavier pour khi carré théorique

        // rejeter h0 ou non en comparant khi carré théorique et observé

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
        System.out.println("Quelle est votre hypothèse h0 ?");
        h0 = keyboard.next();
        System.out.println("Quelle est votre hypothèse h1 ?");
        h1 = keyboard.next();

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
