package TripExpense;

import java.util.*;

/**
 * Console Trip Expense Splitter - Maven version
 * Paste this file at src/main/java/com/example/tripsplitter/TripExpenseSplitter.java
 */
public class Main {

    static class Person {
        String name;
        double paid;
        double net; // paid - share

        Person(String name, double paid) {
            this.name = name;
            this.paid = paid;
            this.net = 0.0;
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("=== Trip Expense Splitter (Maven) ===");

        System.out.print("Kitne log hain? ");
        int n;
        while (true) {
            try {
                String ln = sc.nextLine().trim();
                n = Integer.parseInt(ln);
                if (n <= 0) {
                    System.out.print("1 se bada number dalo: ");
                    continue;
                }
                break;
            } catch (Exception e) {
                System.out.print("Valid number dalo: ");
            }
        }

        List<Person> people = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            System.out.printf("Name of person %d: ", i + 1);
            String name = sc.nextLine().trim();
            if (name.isEmpty()) {
                name = "Person" + (i + 1);
            }
            double paid;
            while (true) {
                System.out.printf("Kitna rupaye %s ne diya? ", name);
                String line = sc.nextLine().trim();
                try {
                    paid = Double.parseDouble(line);
                    if (paid < 0) {
                        System.out.println("Negative allowed nahi hai. Dobara try karo.");
                        continue;
                    }
                    break;
                } catch (Exception e) {
                    System.out.println("Valid number dalo (jaise 2500 ya 1200.50)");
                }
            }
            people.add(new Person(name, paid));
        }

        // Calculate totals
        double total = 0.0;
        for (Person p : people) total += p.paid;
        double share = total / n;

        System.out.println("\n--- Summary ---");
        System.out.printf("Total expense: %.2f\n", total);
        System.out.printf("Each person's fair share: %.2f\n\n", share);

        for (Person p : people) {
            p.net = p.paid - share; // positive: should receive
            System.out.printf("%s paid: %.2f    net: %+.2f\n", p.name, p.paid, p.net);
        }

        // Prepare creditors and debtors
        PriorityQueue<Person> creditors = new PriorityQueue<>((a, b) -> Double.compare(b.net, a.net));
        PriorityQueue<Person> debtors = new PriorityQueue<>((a, b) -> Double.compare(a.net, b.net));

        for (Person p : people) {
            if (Math.abs(p.net) < 0.005) continue;
            if (p.net > 0) creditors.add(p);
            else debtors.add(p);
        }

        System.out.println("\n--- Settlements (who pays whom) ---");
        if (creditors.isEmpty() && debtors.isEmpty()) {
            System.out.println("Already settled — sabka balance zero hai.");
        } else {
            List<String> transactions = new ArrayList<>();
            while (!creditors.isEmpty() && !debtors.isEmpty()) {
                Person cred = creditors.poll();
                Person debt = debtors.poll();

                double amount = Math.min(cred.net, -debt.net);
                amount = Math.round(amount * 100.0) / 100.0;

                transactions.add(String.format("%s pays %.2f to %s", debt.name, amount, cred.name));

                cred.net = Math.round((cred.net - amount) * 100.0) / 100.0;
                debt.net = Math.round((debt.net + amount) * 100.0) / 100.0;

                if (Math.abs(cred.net) > 0.004) creditors.add(cred);
                if (Math.abs(debt.net) > 0.004) debtors.add(debt);
            }

            for (String t : transactions) {
                System.out.println(t);
            }

            double leftover = 0.0;
            for (Person p : people) leftover += p.net;
            leftover = Math.round(leftover * 100.0) / 100.0;
            if (Math.abs(leftover) > 0.01) {
                System.out.printf("\nNote: small leftover (rounding) = %.2f\n", leftover);
            }
        }

        System.out.println("\nDone. Program exit.");
        sc.close();
    }
}
