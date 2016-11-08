package name.lade;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class TicketManager {

    public static void main(String[] args) throws IOException {


        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        Date todaysDate = new Date();

        String filename = "open_tickets.txt";
        File f = new File(filename);
        String filename2 = "Resolved_tickets_as_of_" + dateFormat.format(todaysDate) + ".txt";
        //File f2 = new File(filename2);

        LinkedList<Ticket> ticketQueue = new LinkedList<Ticket>();
        LinkedList<ResolvedTicket> resolvedList = new LinkedList<ResolvedTicket>();

        //read open_tickets.txt
        if (f.isFile()){
            ticketQueue = readTickets(filename);
        } else {
            System.out.println("No file found. Starting new issue tracker.");
        }

        Scanner scan = new Scanner(System.in);

        label:
        while (true) {

            System.out.println(
                    "1. Enter Ticket\n" +
                    "2. Delete Ticket by ID\n" +
                    "3. Delete Ticket by Issue\n" +
                    "4. Search by Issue Name\n" +
                    "5. Display All Tickets\n" +
                    "6. Quit"
            );
            String inputString = scan.nextLine();
            int task;
            if (isInteger(inputString)) {
                task = Integer.parseInt(inputString);
            } else {
                System.out.println("Please enter a valid integer");
                continue; //loop back to asking
            }

            switch (task) {
                case 1:
                    //Call addTickets, which will let us enter any number of new tickets
                    addTickets(ticketQueue);

                    break;
                case 2:
                    //delete a ticket by ID
                    deleteTicketByID(ticketQueue, resolvedList);

                    break;
                case 3:
                    //delete a ticket by searching for a substring of the issue
                    deleteTicketByIssue(ticketQueue, resolvedList);

                    break;
                case 4:
                    //Search for a ticket by name
                    searchByName(ticketQueue);

                    break;
                case 6:
                    //Quit. Future prototype may want to save all tickets to a file
                    System.out.println("Quitting program");
                    break label;
                default:
                    //this will happen for 5 or any other selection that is a valid int

                    //Default will be print all tickets
                    printAllTickets(ticketQueue);
                    printResolvedTickets(resolvedList);
                    break;
            }
        }

        //     Use new method to set up data for storage
        storeTickets(ticketQueue, filename);

        //     Use new method to set up data for storage
        storeResolvedTickets(resolvedList, filename2);


        scan.close();

    }

    //read the tickets in from a file
    private static LinkedList<Ticket> readTickets(String filename) throws IOException {

        int highestInt = 1; //keep track of where we are in the Ticket global index

        //initialize a LinkedList
        LinkedList<Ticket> ticketList = new LinkedList<Ticket>();

        //open the file specified
        try(BufferedReader bufferedReader = new BufferedReader(new FileReader(filename))){
            String delim = Character.toString(Ticket.DELIM);

            //read the first line
            String line = bufferedReader.readLine();

            //System.out.println("DEBUG: line is " + line);

            //if not null, parse it
            while (line != null) {
                // 1|Fire|5|Bob|1478583669804
                String tokens[] = line.split(delim);

                //TODO more validation in case user tampers with input file
                int ticketID = Integer.parseInt(tokens[0]);
                String description = tokens[1];
                int priority = Integer.parseInt(tokens[2]);
                String reporter = tokens[3];
                //http://stackoverflow.com/questions/535004/unix-epoch-time-to-java-date-object
                Date dateReported = new Date(Long.parseLong(tokens[4]));

                highestInt = ticketID;

                //Store these in the LinkedList
                //Note second form of constructor used
                ticketList.add(new Ticket(description, priority, reporter, dateReported, ticketID));

                //then read the next line
                line = bufferedReader.readLine();

            }

            bufferedReader.close(); //close the file when done
        } catch (IOException ioe) {
            System.out.println("Error reading " + filename);
            System.out.println(ioe.toString());
            System.exit(1);
        }

        //write directly to the Ticket class to update the counter
        //maybe not the greatest practice in the world
        Ticket.setTicketIDCounter(highestInt + 1);
        return ticketList;
    }

    // *** Helper methods ***

    private static void storeTickets(LinkedList<Ticket> ticketQueue, String filename) throws IOException {
        System.out.print("Writing tickets to " + filename + "... ");

        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filename))) {

            for (Ticket t : ticketQueue ) {
                //call the method that formats a ticket. remember to add a newline
                String outString = t.toStringDelimited() + "\n";
                //write the line
                bufferedWriter.write(outString);
            }

            //close the writer when done
            bufferedWriter.close();
            System.out.println("Done!");
        } catch (IOException e) {
            System.out.println("Error writing to " + filename);
            System.out.println(e.toString());
            e.printStackTrace();
        }
    }

    private static void storeResolvedTickets(LinkedList<ResolvedTicket> ticketQueue, String filename) throws IOException {
        System.out.print("Archiving resolved tickets to " + filename + "... ");

        //Is there any way to not duplicate above code here? Need to cast method to Ticket OR ResolvedTicket, but all else is identical.

        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filename))) {

            for (Ticket t : ticketQueue ) {
                //call the method that formats a ticket. remember to add a newline
                String outString = t.toStringDelimited() + "\n";
                //write the line
                bufferedWriter.write(outString);
            }

            //close the writer when done
            bufferedWriter.close();
            System.out.println("Done!");
        } catch (IOException e) {
            System.out.println("Error writing to " + filename);
            System.out.println(e.toString());
            e.printStackTrace();
        }

    }


    private static void deleteTicketByID(LinkedList<Ticket> ticketQueue, LinkedList<ResolvedTicket> resolvedList) {
        printAllTickets(ticketQueue);   //display list for user

        if (ticketQueue.size() == 0) {    //no tickets!
            System.out.println("No tickets to delete!\n");
            return;
        }

        userInteractiveDeleteByID(ticketQueue, resolvedList);

        printAllTickets(ticketQueue);  //print updated list

    }

    private static void deleteTicketByIssue(LinkedList<Ticket> ticketQueue, LinkedList<ResolvedTicket> resolvedList){
        if (ticketQueue.isEmpty()) {
            System.out.println("No tickets to delete!");
            return;
        }

        Scanner deleteScanner = new Scanner(System.in);

        System.out.println("Enter a string to search for");
        String searchString = deleteScanner.nextLine();

        LinkedList<Ticket> matchQueue = searchTicketList(ticketQueue, searchString);

        if (matchQueue == null) {
            System.out.println("No matching tickets!");
            return;
        }

        System.out.println(" ------- Matching tickets ----------");

        for (Ticket t : matchQueue) {
            System.out.println(t); //uses toString method in Ticket class
        }
        System.out.println(" ------- End of ticket list ----------");


        //Now let the user choose one of the results to delete

        //IntelliJ throws a duplicate code warning here, so we put that into another method
        userInteractiveDeleteByID(ticketQueue, resolvedList);


        printAllTickets(ticketQueue);  //print updated list

    }

    private static void searchByName(LinkedList<Ticket> ticketQueue) {
        Scanner searchScanner = new Scanner(System.in);

        System.out.println("Enter a string to search for");
        String searchString = searchScanner.nextLine();

        LinkedList<Ticket> foundQueue = searchTicketList(ticketQueue, searchString);

        if (foundQueue == null) {
            System.out.println("No matching tickets!");
            return;
        }

        System.out.println(" ------- Matching tickets ----------");

        for (Ticket t : foundQueue) {
            System.out.println(t); //uses toString method in Ticket class
        }
        System.out.println(" ------- End of ticket list ----------");

    }

    //search a given list of tickets for a certain string in the description
    private static LinkedList<Ticket> searchTicketList(LinkedList<Ticket> ticketQueue, String searchString){
        if (ticketQueue.size() == 0) {
            //System.out.println("No tickets!\n");
            return null;
        }

        LinkedList<Ticket> foundQueue = new LinkedList<Ticket>();

        //loop through all the tickets. if the substring is found, add that ticket to the new LinkedList
        for (Ticket ticket: ticketQueue) {
            if (ticket.getDescription().contains(searchString)) {
                foundQueue.add(ticket);
            }
        }

        //send the new LinkedList back to the caller
        return foundQueue;
    }

    //Move the adding ticket code to a method
    private static void addTickets(LinkedList<Ticket> ticketQueue) {
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

    private static void addTicketInPriorityOrder(LinkedList<Ticket> tickets, Ticket newTicket) {

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

    private static void printAllTickets(LinkedList<Ticket> tickets) {
        System.out.println(" ------- All open tickets ----------");

        for (Ticket t : tickets) {
            System.out.println(t); //Write a toString method in Ticket class
            //println will try to call toString on its argument
        }

        System.out.println(" ------- End of ticket list ----------");

    }

    private static void printResolvedTickets(LinkedList<ResolvedTicket> resolvedList) {
        System.out.println(" ------- Resolved tickets ------------");

        for (Ticket rt : resolvedList) {
            System.out.println(rt); //subclass toString method is used
        }

        System.out.println(" ------- End of ticket list ----------");
    }

    //from http://learn-java-by-example.com/java/check-java-string-integer/
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static boolean isInteger(String s) {
        boolean isValidInteger = false;
        try {
            Integer.parseInt(s);

            // s is a valid integer

            isValidInteger = true;
        }
        catch (NumberFormatException ex) {
            // s is not an integer
        }

        return isValidInteger;
    }

    //common code shared between delete routines
    private static void userInteractiveDeleteByID(LinkedList<Ticket> ticketQueue, LinkedList<ResolvedTicket> resolvedList){
        Scanner deleteScanner = new Scanner(System.in);

        boolean found = false;
        do {
            System.out.println("Enter ID of ticket to delete");
            String input = deleteScanner.nextLine();
            int deleteID;

            // Check if input is a valid integer
            if (isInteger(input)) {
                deleteID = Integer.valueOf(input);
            } else {
                System.out.println("Please enter an integer");
                continue; //stop here and re-ask for input
            }

            //Loop over all tickets. Delete the one with this ticket ID

            for (Ticket ticket : ticketQueue) {
                if (ticket.getTicketID() == deleteID) {
                    found = true;

                    //interactive prompt for resolution info
                    //TODO split into own method
                    System.out.println("What was the resolution to this issue?");
                    String resInput = deleteScanner.nextLine();
                    Date dateResolved = new Date(); //defaults to today

                    //Instance a new resolved ticket
                    //String desc, int p, String rep, Date dateInit, int id, String res, Date dateRes
                    ResolvedTicket rt = new ResolvedTicket(
                            ticket.getDescription(),
                            ticket.getPriority(),
                            ticket.getReporter(),
                            ticket.getDateReported(),
                            ticket.getTicketID(),
                            resInput,
                            dateResolved
                    );

                    //add the old ticket into the resolved list
                    resolvedList.add(rt);

                    //then purge the ticket from the active list
                    ticketQueue.remove(ticket);
                    System.out.println(String.format("Ticket %d deleted", deleteID));
                    break; //don't need loop any more.
                }
            }
            if (!found) {
                System.out.println("Ticket ID not found, no ticket deleted");
            }
        } while (!found); //keep asking until we have found a valid ID

    }

}
