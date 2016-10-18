package name.lade;

import java.util.Date;

class Ticket {

    int priority;
    String reporter; //Stores person or department who reported issue
    String description;
    Date dateReported;

    //STATIC Counter - accessible to all Ticket objects.
    //If any Ticket object modifies this counter, all Ticket objects will have the modified value
    //Make it private - only Ticket objects should have access

    //TODO Problem 8 set this to the next value upon file read
    //     New setter method?
    private static int staticTicketIDCounter = 1;

    //The ID for each ticket - instance variable. Each Ticket will have it's own ticketID variable
    int ticketID;


    Ticket(String desc, int p, String rep, Date date) {
        this.description = desc;
        this.priority = p;
        this.reporter = rep;
        this.dateReported = date;
        this.ticketID = staticTicketIDCounter;
        staticTicketIDCounter++;
    }

    //Overload the constructor so we can set a specific ID. Be careful with this!
    // Could break the list! It is used only for the subclass
    //TODO we can also use this on file read to rebuild the ticketQueue
    Ticket(String desc, int p, String rep, Date date, int ID){
        this.description = desc;
        this.priority = p;
        this.reporter = rep;
        this.dateReported = date;
        this.ticketID = ID;
    }

    int getPriority() {
        return priority;
    }

    int getTicketID() {
        return ticketID;
    }

    String getDescription() {
        return description;
    }

    String getReporter() {
        return reporter;
    }

    Date getDateReported() {
        return dateReported;
    }

    public String toString(){
        return("ID= " + this.ticketID + " Name: " + this.description + " Priority: " + this.priority +
                " Reported by: " + this.reporter + " Reported on: " + this.dateReported);
    }

    //TODO Problem 6 method to format for data storage (CSV? XML? JSON?)
}

class ResolvedTicket extends Ticket {
    private String resolution;
    private Date dateResolved;

    ResolvedTicket(String desc, int p, String rep, Date dateInit, int id, String res, Date dateRes){
        //use the special 5-argument invocation so we don't increment the counter
        super(desc, p, rep, dateInit, id);
        this.resolution = res;
        this.dateResolved = dateRes;
    }

    public String toString(){
        return("ID= " + this.ticketID + " Name: " + this.description + " Priority: " + this.priority +
                " Reported by: " + this.reporter + " Reported on: " + this.dateReported + "\n    Resolution: " +
                this.resolution + " Date resolved: " + this.dateResolved );
    }

    //TODO Problem 6 method to format for data storage (CSV? XML? JSON?)
}