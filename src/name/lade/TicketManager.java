package name.lade;

import java.util.*;

public class TicketManager {

    public static void main(String[] args) {

        LinkedList<Ticket> ticketQueue = new LinkedList<Ticket>();

        Scanner scan = new Scanner(System.in);

        while (true) {

            System.out.println("1. Enter Ticket\n2. Delete Ticket\n3. Display All Tickets\n4. Quit");
            int task = Integer.parseInt(scan.nextLine());

            if (task == 1) {
                //Call addTickets, which will let us enter any number of new tickets
                addTickets(ticketQueue);

            } else if (task == 2) {
                //delete a ticket
                deleteTicket(ticketQueue);

            } else if (task == 4) {
                //Quit. Future prototype may want to save all tickets to a file
                System.out.println("Quitting program");
                break;
            } else {
                //this will happen for 3 or any other selection that is a valid int
                //TODO Program crashes if you enter anything else - please fix
                //Default will be print all tickets
                printAllTickets(ticketQueue);
            }
        }

        scan.close();

    }

    protected static void deleteTicket(LinkedList<Ticket> ticketQueue) {
        printAllTickets(ticketQueue);   //display list for user

        if (ticketQueue.size() == 0) {    //no tickets!
            System.out.println("No tickets to delete!\n");
            return;
        }

        Scanner deleteScanner = new Scanner(System.in);

        boolean found = false;
        do {
            System.out.println("Enter ID of ticket to delete");
            int deleteID = deleteScanner.nextInt();

            //Loop over all tickets. Delete the one with this ticket ID

            for (Ticket ticket : ticketQueue) {
                if (ticket.getTicketID() == deleteID) {
                    found = true;
                    ticketQueue.remove(ticket);
                    System.out.println(String.format("Ticket %d deleted", deleteID));
                    break; //don't need loop any more.
                }
            }
            if (!found) {
                System.out.println("Ticket ID not found, no ticket deleted");
            }
        } while (!found);

        printAllTickets(ticketQueue);  //print updated list

    }

    //Move the adding ticket code to a method
    protected static void addTickets(LinkedList<Ticket> ticketQueue) {
        Scanner sc = new Scanner(System.in);

        boolean moreProblems = true;
        String description;
        String reporter;
        //let's assume all tickets are created today, for testing. We can change this later if needed
        Date dateReported = new Date(); //Default constructor creates date with current date/time
        int priority;

        while (moreProblems) {
            System.out.println("Enter problem");
            description = sc.nextLine();
            System.out.println("Who reported this issue?");
            reporter = sc.nextLine();
            System.out.println("Enter priority of " + description);
            priority = Integer.parseInt(sc.nextLine());

            Ticket t = new Ticket(description, priority, reporter, dateReported);
            //ticketQueue.add(t);
            addTicketInPriorityOrder(ticketQueue, t);

            //To test, let's print out all of the currently stored tickets
            printAllTickets(ticketQueue);

            System.out.println("More tickets to add?");
            String more = sc.nextLine();
            if (more.equalsIgnoreCase("N")) {
                moreProblems = false;
            }
        }
    }


    protected static void addTicketInPriorityOrder(LinkedList<Ticket> tickets, Ticket newTicket) {

        //Logic: assume the list is either empty or sorted

        if (tickets.size() == 0) {//Special case - if list is empty, add ticket and return
            tickets.add(newTicket);
            return;
        }

        //Tickets with the HIGHEST priority number go at the front of the list. (e.g. 5=server on fire)
        //Tickets with the LOWEST value of their priority number (so the lowest priority) go at the end

        int newTicketPriority = newTicket.getPriority();

        for (int x = 0; x < tickets.size(); x++) {    //use a regular for loop so we know which element we are looking at

            //if newTicket is higher or equal priority than the this element, add it in front of this one, and return
            if (newTicketPriority >= tickets.get(x).getPriority()) {
                tickets.add(x, newTicket);
                return;
            }
        }

        //Will only get here if the ticket is not added in the loop
        //If that happens, it must be lower priority than all other tickets. So, add to the end.
        tickets.addLast(newTicket);
    }

    protected static void printAllTickets(LinkedList<Ticket> tickets) {
        System.out.println(" ------- All open tickets ----------");

        for (Ticket t : tickets) {
            System.out.println(t); //Write a toString method in Ticket class
            //println will try to call toString on its argument
        }
        System.out.println(" ------- End of ticket list ----------");

    }
}
