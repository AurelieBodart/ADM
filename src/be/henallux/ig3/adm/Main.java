package be.henallux.ig3.adm;

import org.apache.commons.math3.distribution.ChiSquaredDistribution;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static final int EXPRESS_CLIENT_COST = 35;
    public static final int ORDINARY_CLIENT_COST = 25;
    public static final int ABSOLUTE_CLIENT_COST = 45;
    public static final int EXPRESS_STATION_OCCUPATION_COST = 32;
    public static final int ORDINARY_STATION_OCCUPATION_COST = 30;
    public static final int EMPTY_STATION_COST = 18;
    public static final int QUEUE_CHANGING_COST = 15;
    public static final int CLIENT_EJECTION_COST = 20;

    public static void main(String[] args) {
        try {
            Scanner keyboard = new Scanner(System.in);
            FileOutputStream resultStream = new FileOutputStream("Résultats.txt");
            OutputRedirection redirection = new OutputRedirection(System.out, resultStream);
            PrintStream out = new PrintStream(redirection);

            System.setOut(out);

            ArrayList<Integer> suite = askForSuiteData(keyboard);
            JumpsTest jumpsTest = askForJumpsTestData(keyboard);

            System.out.println("Suite size : " + suite.size());
            jumpsTest.countJumps(suite);
            System.out.println("Sauts size : " + jumpsTest.getJumps().size());

            jumpsTest.generatedTab();
            for (Jump j : jumpsTest.getJumpsList())
                System.out.println(" [Saut = " + j.getSaut() +
                        " ri = " + j.getRi() +
                        " pi = " + j.getPi() +
                        " npi = " + j.getNpi());


            // Etape 4 : regrouper les npi à partir du bas du tableau si < 5
            //           calculer khi carré observé

            System.out.println("Etape 4 - npi < 5 ?");
            jumpsTest.reduceTab();
            for (Jump j : jumpsTest.getJumpsList())
                System.out.println(" [Saut = " + j.getSaut() +
                        " ri = " + j.getRi() +
                        " pi = " + j.getPi() +
                        " npi = " + j.getNpi() +
                        " (ri - npi)^2 / npi = " + j.getPartialX2Observable() + "]");


            // Etape 5 : clavier pour khi carré théorique

            System.out.println("Etape 5 - établissement de la zone de non rejet");

            Double chiCarreObservable = jumpsTest.calculChiCarreObservable();

            ChiSquaredDistribution x2 = new ChiSquaredDistribution(jumpsTest.getV());
            double chiCarreTheorique = x2.inverseCumulativeProbability(jumpsTest.getAlpha());

            System.out.println("Chi carré observable = " + chiCarreObservable);
            System.out.println("Chi carré théorique = " + chiCarreTheorique);

            // Etape 6 - rejeter h0 ou non en comparant khi carré théorique et observé

            System.out.println("Etape 6 - Rejet ou non de H0");
            System.out.println("Rappel :" +
                    "\nH0 = " + jumpsTest.getH0() +
                    "\nH1 = " + jumpsTest.getH1());

            if (chiCarreObservable > chiCarreTheorique)
                System.out.println("H0 est rejeté");
            else
                System.out.println("H0 est n'est pas rejeté avec un degré d'incertitude de " + jumpsTest.getAlpha());
        } catch (FileNotFoundException ignored) {}
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

    public static int simulation(int minimumStationsNumber, int maximumStationsNumber, int simulationTime) {
        int stationsNumber = minimumStationsNumber;
        
        // max - min --> 54 - 5 pour avoir juste la taile qu'il faut (peut être +1)
        // là pour le coup c'est moi qui me suis trompée dans le DA :(
        int[] totalCosts = new int[maximumStationsNumber];

        //Ajout de Max //TODO: A vérifier
        int changingQueueCost = 15; // il est déjà au dessus dans les constantes


        while (stationsNumber <= maximumStationsNumber) {
            Client[] expressStations = new Client[2];
            Client[] ordinaryStations = new Client[stationsNumber - 2];


            Client[] expressQueue = new Client[10];
            ArrayList<Client> ordinaryQueue = new ArrayList<>();
            ArrayList<Client> vipQueue = new ArrayList<>();

            // il manque les duréeFileCumulée express, ordinaire et VIP

            int cumulatedExpressStationDuration = 0;
            int cumulatedOrdinaryStationDuration = 0;
            int cumulatedVIPStationDuration = 0;
            int vacancyDuration = 0;

            int totalClientEjectionCost = 0;
            int totalChangingQueueCost = 0;

            //Ajout de Max //TODO: A vérifier
            int iQueueExpress = 0;



            GenerationSuite suite = new GenerationSuite(); //TODO: Rajouter x0, c, a, m

            int time = 1;
            while (time <= simulationTime) {
                // Placement en file
                int arrivalsNumber = generateArrivals();
                ArrayList<Client> clients = initializeClientDurations(arrivalsNumber);


                // Partie de Maxime //TODO: A vérifier
                for (Client client : clients) {
                    client.setSystemEntry(time);

                    if (client.getServiceDuration() == 1) {
                        if (iQueueExpress < 10) {
                            client.setType("express");
                            expressQueue[iQueueExpress] = client;
                            iQueueExpress++;
                        } else {
                            client.setType("ordinaire");
                            totalChangingQueueCost += changingQueueCost;
                            ordinaryQueue.add(client);
                        }
                    } else {
                        double un = suite.generateUn(suite.generateXn());

                        if (un < 0.10) {
                            client.setType("prioritaire abslolut"); // absolu ;)
                            vipQueue.add(client);
                        } else {
                            client.setType("ordinaire");
                            ordinaryQueue.add(client);
                        }
                    }
                }


                // Placement en station + décrémentation
                // Partie de Christophe
            }

            stationsNumber++;
        }

        return totalCosts[0];
    }

    private static int generateArrivals() {
        // tu as déjà créé ta suite avec la boucle sur le temps
        // passe la en argument ici au lieu d'en créer une autre
        //GenerationSuite suite = new GenerationSuite(); //TODO: Rajouter x0, c, a, m
        double un = suite.generateUn(suite.generateXn());

        // je chipote pour ça mais j'aurais préféré des if else pour plus de lisibilité mais on peut laiser comme ça
        int x = (un < 0.1353) ? 0 : (un < 0.4060) ? 1 : (un < 0.6767) ? 2 : (un < 0.8571) ? 3 :
                (un < 0.9473) ? 4 : (un < 0.9834) ? 5 : (un < 0.9955) ? 6 : (un < 0.9989) ? 7 : (un < 0.9998) ? 8 : 9;

        return x;
    }

    private static ArrayList<Client> initializeClientDurations(int arrivalsNumber) {

        // idem que pour l'autre fonction, tu peux passer la suite en argument de cette fonction
        //GenerationSuite suite = new GenerationSuite(); //TODO: Rajouter x0, c, a, m

        ArrayList<Client> clients = new ArrayList<>();
        int iArrival = 0;

        while (iArrival < arrivalsNumber) {

            double un = suite.generateUn(suite.generateXn());

            int x = (un < 0.4) ? 1 : (un < 0.7) ? 2 : (un < 0.8667) ? 3 : (un < 0.9167) ? 4 : (un < 0.9667) ? 5 : 6;

            // ici on peut mettre null pour le type et systemEntry vu qu'on les set plus tard. Ok pour x et false
            clients.add(new Client(null, x,0,false)); // ?? je met quoi dans les autre variable ? //TODO: A vérifier

            iArrival++;
        }

        return clients;
    }
}
