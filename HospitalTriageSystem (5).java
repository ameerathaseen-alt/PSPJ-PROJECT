import java.util.Scanner;

class HospitalTriageSystem {

    // Constants and lookup arrays (index 1 = Critical, 2 = Serious, 3 = Normal)
    static final int MAX = 20, CRITICAL = 1, SERIOUS = 2, NORMAL = 3;
    static final String[] LEVEL_NAMES = {"", "Critical", "Serious", "Normal"};
    static final String[] DOCTORS = {"", "Emergency Specialist", "Senior Doctor",
            "General Physician"};
    static final String[] MESSAGES = {"", "Patient needs immediate treatment.",
            "Patient needs urgent treatment.",
            "Patient can wait for normal treatment."};
    static final String[] COMPLAINTS = {"Chest pain", "Difficulty in breathing",
            "Severe bleeding", "Broken bone", "High fever", "Minor cut or injury",
            "General checkup"};
    static final int[] BEDS = {0, 1, 2, 3};   // beds available for each level

    // Arrays to store the patient records
    static String[] names = new String[MAX], complaints = new String[MAX];
    static int[] ages = new int[MAX], levels = new int[MAX];
    static double[] temps = new double[MAX];
    static Scanner sc = new Scanner(System.in);

    // Read a number in a range; try-catch handles wrong input
    static double readNumber(String msg, double min, double max) {
        while (true) {
            System.out.print(msg);
            try {
                double value = Double.parseDouble(sc.nextLine().trim());
                if (value >= min && value <= max) { return value; }
                System.out.println("Enter a value from " + (int) min
                        + " to " + (int) max);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

    // Decide the emergency level from the complaint number
    static int levelFromComplaint(int choice) {
        switch (choice) {
            case 1: case 2: case 3: return CRITICAL;
            case 4: case 5:         return SERIOUS;
            default:                return NORMAL;
        }
    }

    // Raise the level for a very high fever, small children and elderly patients
    static int adjustLevel(int level, int age, double temp) {
        if (temp >= 104.0) { return CRITICAL; }
        else if ((age < 5 || age >= 65) && level == NORMAL) { return SERIOUS; }
        else { return level; }
    }

    static String feverStatus(double temp) {
        if (temp < 97.0) { return "Low temperature"; }
        else if (temp <= 99.5) { return "Normal temperature"; }
        else if (temp <= 102.0) { return "Fever"; }
        else { return "High fever"; }
    }

    // Time needed to treat one patient of the given level
    static int treatmentMinutes(int level) {
        switch (level) {
            case CRITICAL: return 30;
            case SERIOUS:  return 20;
            default:       return 10;
        }
    }

    static void registerPatient(int i) {
        System.out.print("Enter patient name: ");
        names[i] = sc.nextLine().trim();
        if (names[i].isEmpty()) { names[i] = "Patient " + (i + 1); }
        ages[i] = (int) readNumber("Enter age (0-120): ", 0, 120);
        temps[i] = readNumber("Enter temperature in F (90-110): ", 90, 110);
        for (int k = 0; k < COMPLAINTS.length; k++) {
            System.out.println((k + 1) + ". " + COMPLAINTS[k]);
        }
        int choice = (int) readNumber("Select main complaint (1-7): ", 1, 7);
        complaints[i] = COMPLAINTS[choice - 1];
        levels[i] = adjustLevel(levelFromComplaint(choice), ages[i], temps[i]);
        System.out.println("Emergency Status: " + LEVEL_NAMES[levels[i]]);
    }

    // Bubble sort on an index array: Critical patients come first
    static int[] sortByPriority(int n) {
        int[] order = new int[n];
        for (int i = 0; i < n; i++) { order[i] = i; }
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - 1 - i; j++) {
                if (levels[order[j]] > levels[order[j + 1]]) {
                    int temp = order[j];
                    order[j] = order[j + 1];
                    order[j + 1] = temp;
                }
            }
        }
        return order;
    }

    static void showTreatmentList(int[] order, int n) {
        int[] bedsUsed = new int[4];
        int waitMinutes = 0;
        System.out.println("\n===== TREATMENT ORDER (MOST URGENT FIRST) =====");
        for (int k = 0; k < n; k++) {
            int p = order[k];
            int level = levels[p];
            System.out.println("\nPriority " + (k + 1) + " : " + names[p]
                    + " (Age " + ages[p] + ")");
            System.out.println("Complaint       : " + complaints[p]);
            System.out.println("Temperature     : " + temps[p] + " F ("
                    + feverStatus(temps[p]) + ")");
            System.out.println("Emergency Level : " + LEVEL_NAMES[level]);
            System.out.println("Assigned Doctor : " + DOCTORS[level]);
            System.out.println("Estimated Wait  : " + waitMinutes + " minutes");
            if (bedsUsed[level] < BEDS[level]) {
                bedsUsed[level]++;
                System.out.println("Bed             : Bed allotted");
            } else {
                System.out.println("Bed             : No free bed, please wait");
            }
            System.out.println(MESSAGES[level]);
            waitMinutes += treatmentMinutes(level);
        }
    }

    static void showSummary(int n) {
        int[] levelCount = new int[4];
        int oldest = 0, hottest = 0;
        double total = 0;
        for (int i = 0; i < n; i++) {
            levelCount[levels[i]]++;
            total += temps[i];
            if (ages[i] > ages[oldest]) { oldest = i; }
            if (temps[i] > temps[hottest]) { hottest = i; }
        }
        System.out.println("\n===== EMERGENCY ROOM SUMMARY =====");
        System.out.println("Total patients      : " + n);
        System.out.println("Critical patients   : " + levelCount[CRITICAL]);
        System.out.println("Serious patients    : " + levelCount[SERIOUS]);
        System.out.println("Normal patients     : " + levelCount[NORMAL]);
        System.out.printf("Average temperature : %.1f F%n", total / n);
        System.out.println("Highest temperature : " + names[hottest]
                + " (" + temps[hottest] + " F)");
        System.out.println("Oldest patient      : " + names[oldest]
                + " (" + ages[oldest] + " years)");
        if (levelCount[CRITICAL] > 0) {
            System.out.println("ALERT: Critical patients need immediate attention!");
        }
    }

    public static void main(String[] args) {
        System.out.println("===== HOSPITAL EMERGENCY ROOM TRIAGE SYSTEM =====");
        int n = (int) readNumber("Number of patients (1-" + MAX + "): ", 1, MAX);

        for (int i = 0; i < n; i++) {
            System.out.println("\n--- Patient " + (i + 1) + " of " + n + " ---");
            registerPatient(i);
        }

        int[] order = sortByPriority(n);
        showTreatmentList(order, n);
        showSummary(n);

        System.out.println("\n=================================================");
        System.out.println("   TRIAGE COMPLETED. THANK YOU. PROGRAM ENDED.");
        System.out.println("=================================================");
        sc.close();
    }
}
