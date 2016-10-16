package name.lade;

import java.util.Date;

public class Ticket {

    private int priority;
    private String reporter; //Stores person or department who reported issue
    private String description;
    private Date dateReported;

    //We can autogenerate get and set methods if and when we need

    //A constructor would be useful

    public Ticket(String desc, int p, String rep, Date date) {
        this.description = desc;
        this.priority = p;
        this.reporter = rep;
        this.dateReported = date;
    }

    //Called automatically if a Ticket object is an argument to System.out.println
    public String toString(){
        return(this.description + " Priority: " + this.priority + " Reported by: "
                + this.reporter + " Reported on: " + this.dateReported);
    }

}