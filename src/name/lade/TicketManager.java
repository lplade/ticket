package name.lade;

import java.util.*;

public class TicketManager {

    public static void main(String[] args) {

        LinkedList<Ticket> ticketQueue = new LinkedList<Ticket>();

        Scanner sc = new Scanner(System.in);

        //Ask for some ticket info, create tickets, store in ticketQueue

        String description;
        String reporter;
        //let's assume all tickets are created today, for testing. We can change this later.
        Date dateReported = new Date(); //Default constructor creates date with current date/time
        int priority;

        boolean moreProblems = true;
        while (moreProblems) {
            System.out.println("Enter problem");
            description = sc.nextLine();
            System.out.println("Who reported this issue?");
            reporter = sc.nextLine();
            System.out.println("Enter priority of " + description);
            priority = Integer.parseInt(sc.nextLine());

            Ticket t = new Ticket(description, priority, reporter, dateReported);
            ticketQueue.add(t);

            //To test, let's print out all of the currently stored tickets after a new ticket is entered
            printAllTickets(ticketQueue);

            System.out.println("More tickets?");
            String more = sc.nextLine();
            if (more.equalsIgnoreCase("N")) {
                moreProblems = false;
            }
        }

        sc.close();

    }

    protected static void printAllTickets(LinkedList<Ticket> tickets) {
        System.out.println(" ------- All tickets ----------");

        for (Ticket t : tickets ) {
            System.out.println(t); //Write a toString method in Ticket class
            //println will try to call toString on it's argument
        }
    }
}