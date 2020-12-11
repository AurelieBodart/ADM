package be.henallux.ig3.adm;

import org.apache.commons.math3.distribution.ChiSquaredDistribution;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.*;

public class Main {
    public static final double EXPRESS_CLIENT_COST_PER_MINUTE = 35 / 60.;
    public static final double ORDINARY_CLIENT_COST_PER_MINUTE = 25 / 60.;
    public static final double ABSOLUTE_CLIENT_COST_PER_MINUTE = 45 / 60.;
    public static final double EXPRESS_STATION_OCCUPATION_COST_PER_MINUTE = 32 / 60.;
    public static final double ORDINARY_STATION_OCCUPATION_COST_PER_MINUTE = 30 / 60.;
    public static final double VACANT_STATION_COST_PER_MINUTE = 18 / 60.;
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

    public static double simulation(int minimumStationsNumber, int maximumStationsNumber, int simulationTime) {
        int stationsNumber = minimumStationsNumber;
        CostPerStationsNumber[] totalCosts = new CostPerStationsNumber[maximumStationsNumber];

        //Ajout de Max
        // int changingQueueCost = 15; // il est déjà au dessus dans les constantes // AH


        while (stationsNumber <= maximumStationsNumber) {
            Client[] expressStations = new Client[2];
            Client[] ordinaryStations = new Client[stationsNumber - 2];


            Client[] expressQueue = new Client[10];
            ArrayList<Client> ordinaryQueue = new ArrayList<>();
            ArrayList<Client> vipQueue = new ArrayList<>();

            int cumulatedExpressQueueDuration = 0;
            int cumulatedOrdinaryQueueDuration = 0;
            int cumulatedVIPQueueDuration = 0;

            int cumulatedExpressStationDuration = 0;
            int cumulatedOrdinaryStationDuration = 0;
            int cumulatedVIPStationDuration = 0;
            int vacancyDuration = 0;

            int totalClientEjectionCost = 0;
            int totalChangingQueueCost = 0;

            //Ajout de Max
            int iQueueExpress = 0;

            // passe la en argument des 2 fonctions init comme ça tu ne dois pas en créer 2 autres (pour ne pas repartir au X0) // FAIT
            GenerationSuite suite = new GenerationSuite(); //TODO: Rajouter x0, c, a, m

            for (int time = 1; time <= simulationTime; time++) {
                // Placement en file
                int arrivalsNumber = generateArrivals(suite);
                ArrayList<Client> clients = initializeClientDurations(suite,arrivalsNumber);


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
                            totalChangingQueueCost += QUEUE_CHANGING_COST;
                            ordinaryQueue.add(client);
                        }
                    } else {
                        double un = suite.generateUn(suite.generateXn());

                        if (un < 0.10) {
                            client.setType("prioritaire abslolu"); // absolu ;) // FAIT
                            vipQueue.add(client);
                        } else {
                            client.setType("ordinaire");
                            ordinaryQueue.add(client);
                        }
                    }
                }

                // Placement en station + décrémentation
                // Partie de Christophe
                int iVIP = 0;
                boolean canAddVIP = true;
                
                while (!vipQueue.isEmpty() && canAddVIP) {
                    int iFreeStation = -1;
                    int dsMax = 0;
                    int iDsMax = -1;

                    for (int iOrdinaryStation = 0; iOrdinaryStation < ordinaryStations.length; iOrdinaryStation++) {
                        if (ordinaryStations[iOrdinaryStation] == null)
                            iFreeStation = iOrdinaryStation;
                        else {
                            if (iFreeStation == -1
                                    && ordinaryStations[iOrdinaryStation].getType().equals("Ordinary")
                                    && ordinaryStations[iOrdinaryStation].getServiceDuration() > dsMax) {
                                dsMax = ordinaryStations[iOrdinaryStation].getServiceDuration();
                                iDsMax = iOrdinaryStation;
                            }
                        }
                    }

                    if (iFreeStation != -1) {
                        cumulatedVIPQueueDuration += time - vipQueue.get(iVIP).getSystemEntry();
                        cumulatedVIPStationDuration += vipQueue.get(iVIP).getServiceDuration();

                        ordinaryStations[iFreeStation] = vipQueue.remove(iVIP);
                        ordinaryStations[iFreeStation].decrementServiceDuration();
                    } else {
                        if (iDsMax != -1) {
                            totalClientEjectionCost += CLIENT_EJECTION_COST;

                            ordinaryStations[iDsMax].setSystemEntry(time);
                            ordinaryStations[iDsMax].setIsEjected(true);

                            ordinaryQueue.add(0, ordinaryStations[iDsMax]);

                            cumulatedVIPQueueDuration += time - vipQueue.get(iVIP).getSystemEntry();
                            cumulatedVIPStationDuration += vipQueue.get(iVIP).getServiceDuration();

                            ordinaryStations[iDsMax] = vipQueue.remove(iVIP);
                            ordinaryStations[iDsMax].decrementServiceDuration();
                        } else
                            canAddVIP = false;
                    }
                    iVIP++;
                }

                // Placer les clients express
                for (int iExpress = 0; iExpress < expressStations.length; iExpress++) {
                    int expressQueueLength = 0;

                    while (expressQueue[expressQueueLength] != null)
                        expressQueueLength++;

                    if (expressQueueLength > 0) {
                        cumulatedExpressQueueDuration += time - expressQueue[0].getSystemEntry();
                        cumulatedExpressStationDuration++;

                        expressStations[iExpress] = expressQueue[0];

                        // Retrait de la liste et shifting vers la gauche THE JAVA WAY
                        expressQueue[0] = null;

                        List<Client> expressQueueAsList = Arrays.asList(expressQueue);
                        Collections.rotate(expressQueueAsList, 1);
                        expressQueue = (Client[]) expressQueueAsList.toArray();
                        iQueueExpress--;
                    }
                }

                // Placer les clients ordinaires nuls
                for (int iOrdinary = 0; iOrdinary < ordinaryStations.length; iOrdinary++) {
                    if (ordinaryStations[iOrdinary] == null || ordinaryStations[iOrdinary].getServiceDuration() == 0) {
                        if (ordinaryQueue.get(0) != null) {
                            ordinaryStations[iOrdinary] = ordinaryQueue.remove(0);
                            if (!ordinaryStations[iOrdinary].isEjected())
                                cumulatedOrdinaryStationDuration += ordinaryStations[iOrdinary].getServiceDuration();

                            cumulatedOrdinaryQueueDuration += time - ordinaryStations[iOrdinary].getSystemEntry();
                            ordinaryStations[iOrdinary].decrementServiceDuration();
                        }
                    } else
                        ordinaryStations[iOrdinary].decrementServiceDuration();
                }

                // Chercher les stations inoccupées
                for (Client client : expressStations) {
                    if (client == null)
                        vacancyDuration++;
                }

                for (Client client: ordinaryStations) {
                    if (client == null)
                        vacancyDuration++;
                }
            }

            // Calcul des coûts
            totalCosts[stationsNumber - minimumStationsNumber] =
                    new CostPerStationsNumber(
                            (((cumulatedOrdinaryQueueDuration + cumulatedOrdinaryStationDuration) * ORDINARY_CLIENT_COST_PER_MINUTE)
                                    + ((cumulatedExpressQueueDuration + cumulatedExpressQueueDuration) * EXPRESS_CLIENT_COST_PER_MINUTE)
                                    + ((cumulatedVIPQueueDuration + cumulatedVIPStationDuration) * ABSOLUTE_CLIENT_COST_PER_MINUTE)
                                    + (cumulatedExpressStationDuration * EXPRESS_STATION_OCCUPATION_COST_PER_MINUTE)
                                    + ((cumulatedOrdinaryStationDuration + cumulatedVIPStationDuration) * ORDINARY_STATION_OCCUPATION_COST_PER_MINUTE)
                                    + (vacancyDuration * VACANT_STATION_COST_PER_MINUTE)
                                    + totalChangingQueueCost
                                    + totalClientEjectionCost),
                            stationsNumber);

            stationsNumber++;
        }

        return Collections.min(Arrays.asList(totalCosts)).getCost();
    }

    private static int generateArrivals(GenerationSuite suite) {
        // tu as déjà créé ta suite avec la boucle sur le temps
        // passe la en argument ici au lieu d'en créer une autre // FAIT
        double un = suite.generateUn(suite.generateXn());

        // je chipote pour ça mais j'aurais préféré des if else pour plus de lisibilité mais on peut laiser comme ça
        // (c'était pour faire plaiz à Christophe ahah Mais j'aime bien comme ça mois :D)
        int x = (un < 0.1353) ? 0 : (un < 0.4060) ? 1 : (un < 0.6767) ? 2 : (un < 0.8571) ? 3 :
                (un < 0.9473) ? 4 : (un < 0.9834) ? 5 : (un < 0.9955) ? 6 : (un < 0.9989) ? 7 : (un < 0.9998) ? 8 : 9;

        return x;
    }

    private static ArrayList<Client> initializeClientDurations(GenerationSuite suite, int arrivalsNumber) {

        // idem que pour l'autre fonction, tu peux passer la suite en argument de cette fonction  // FAIT

        ArrayList<Client> clients = new ArrayList<>();
        int iArrival = 0;

        while (iArrival < arrivalsNumber) {

            double un = suite.generateUn(suite.generateXn());

            int x = (un < 0.4) ? 1 : (un < 0.7) ? 2 : (un < 0.8667) ? 3 : (un < 0.9167) ? 4 : (un < 0.9667) ? 5 : 6;

            // ici on peut mettre null pour le type et systemEntry vu qu'on les set plus tard. Ok pour x et false // OK
            clients.add(new Client(null, x,0,false));

            iArrival++;
        }

        return clients;
    }
}
