package models;

import io.ebean.Finder;
import io.ebean.Model;
import play.data.validation.Constraints;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.List;

@Entity
public class Event extends Model {


    public static Event newEvent(EventType eventType, String ean, String owner) {
        return new Event(eventType, ean, owner);
    }

    public static Finder<Long, Event> find = new Finder<>(Event.class);

    public static List<Event> findAll() {
        return find.all();
    }

    public enum EventType {
        CREATE_PRODUCT,
        EDIT_PRODUCT, 
        UPDATE_PRODUCT, 
        FLUSH_DATABASE, 
        POPULATE_DATABASE_FROM_DATAFILE, 
        CONSULT_ALL_PRODUCTS,
        CONSULT_PRODUCT,
        CHANGE_USER_ACCESS,
        //CONNECTION_USER
        ;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long id;

    @Constraints.Required
    public EventType type;

    @Constraints.Required
    private final String ean;

    @Constraints.Required
    public final String owner;
    
    private Event(EventType eventType, String ean, String owner) {
        this.type = eventType;
        this.ean = ean;
        this.owner = owner;
    }
    
    public String toString() {
        return "[" + owner + " - " + ean + " - " + type.name() + "]";
    }
}
