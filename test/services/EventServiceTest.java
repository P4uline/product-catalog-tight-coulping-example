package services;

import io.ebean.Finder;
import models.Event;
import models.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static models.Event.EventType.*;
import static models.Event.newEvent;
import static models.User.Role.*;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class EventServiceTest {
    
    static class EventServiceUnderTest extends EventService {
        
        @Inject
        private Finder<Object, Event> finder;
        
        @Override
        protected Finder<Object, Event> getEventFinder() {
            return finder;
        }
    }
    
    @InjectMocks
    private EventServiceUnderTest eventServiceUnderTest;
    
    @Mock
    private AuthenticatorService authenticatorServiceMock;
    
    @Mock
    private Finder<Object, Event> finder;

    @Test
    public void super_gestionnaire_should_see_all_product_events_but_no_CHANGE_USER_ACCESS_event() {
        Mockito.when(authenticatorServiceMock.getCurrentUser()).thenReturn(new User("George Abitbol", SUPER_GESTIONNAIRE));
        List<Event> events = new ArrayList<>();
        events.add(newEvent(CONSULT_ALL_PRODUCTS, "no-ean", "Jane Doe"));
        events.add(newEvent(CHANGE_USER_ACCESS, "no-ean", "Jane Doe"));
        events.add(newEvent(CONSULT_PRODUCT, "34283", "Jane Doe"));
        events.add(newEvent(EDIT_PRODUCT, "34283", "John Doe"));
        events.add(newEvent(CONSULT_PRODUCT, "34283", "John Doe"));
        events.add(newEvent(CONSULT_ALL_PRODUCTS, "34283", "John Doe"));
        events.add(newEvent(CREATE_PRODUCT, "1234", "John Doe"));

        Mockito.when(finder.all()).thenReturn(events);

        List<Event> actual = eventServiceUnderTest.findProuctEvents("34283");

        List<Event> expected = events.stream()
                .filter(e -> !e.type.equals(CHANGE_USER_ACCESS))
                .filter(e -> e.ean.equals("34283"))
                .collect(toList());
        assertThat(actual).isEqualTo(expected);
    }
    
    @Test
    public void admin_should_see_all_events() {
        // Given an admin user is authenticated
        Mockito.when(authenticatorServiceMock.getCurrentUser()).thenReturn(new User("Jane Doe", ADMIN));
        
        // Given the list of events
        List<Event> events = new ArrayList<>();
        events.add(newEvent(CONSULT_ALL_PRODUCTS, "no-ean", "Jane Doe"));
        events.add(newEvent(CHANGE_USER_ACCESS, "no-ean", "Jane Doe"));
        events.add(newEvent(CONSULT_PRODUCT, "34283", "Jane Doe"));
        events.add(newEvent(EDIT_PRODUCT, "34283", "John Doe"));
        events.add(newEvent(CONSULT_PRODUCT, "34283", "John Doe"));
        events.add(newEvent(CONSULT_ALL_PRODUCTS, "34283", "John Doe"));
        events.add(newEvent(CREATE_PRODUCT, "1234", "John Doe"));
        Mockito.when(finder.all()).thenReturn(events);

        // When Run
        List<Event> actual = eventServiceUnderTest.findEvents();
        
        // Then the admin has to access to all events
        List<Event> expected = new ArrayList<>(actual);
        
        assertThat(actual).isEqualTo(expected);
    }
    
    @Test
    public void gestionnaire_should_see_all_own_events_but_no_CHANGE_USER_ACCESS_event() {

        Mockito.when(authenticatorServiceMock.getCurrentUser()).thenReturn(new User("John Doe", GESTIONAIRE));
        List<Event> events = new ArrayList<>();
        events.add(newEvent(CONSULT_ALL_PRODUCTS, "no-ean", "Jane Doe"));
        events.add(newEvent(CHANGE_USER_ACCESS, "no-ean", "Jane Doe"));
        events.add(newEvent(CONSULT_PRODUCT, "34283", "Jane Doe"));
        events.add(newEvent(EDIT_PRODUCT, "34283", "John Doe"));
        events.add(newEvent(CONSULT_PRODUCT, "34283", "John Doe"));
        events.add(newEvent(CONSULT_ALL_PRODUCTS, "34283", "John Doe"));
        events.add(newEvent(CREATE_PRODUCT, "1234", "John Doe"));
        events.add(newEvent(CHANGE_USER_ACCESS, "no-ean", "John Doe"));

        Mockito.when(finder.all()).thenReturn(events);

        List<Event> actual = eventServiceUnderTest.findEvents();

        List<Event> expected = events.stream()
                .filter(e -> !e.type.equals(CHANGE_USER_ACCESS))
                .filter(e -> e.owner.equals("John Doe"))
                .collect(toList());
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void super_gestionnaire_should_see_all_events_but_no_CHANGE_USER_ACCESS_event() {
        Mockito.when(authenticatorServiceMock.getCurrentUser()).thenReturn(new User("George Abitbol", SUPER_GESTIONNAIRE));
        List<Event> events = new ArrayList<>();
        events.add(newEvent(CONSULT_ALL_PRODUCTS, "no-ean", "Jane Doe"));
        events.add(newEvent(CHANGE_USER_ACCESS, "no-ean", "Jane Doe"));
        events.add(newEvent(CONSULT_PRODUCT, "34283", "Jane Doe"));
        events.add(newEvent(EDIT_PRODUCT, "34283", "John Doe"));
        events.add(newEvent(CONSULT_PRODUCT, "34283", "John Doe"));
        events.add(newEvent(CONSULT_ALL_PRODUCTS, "34283", "John Doe"));
        events.add(newEvent(CREATE_PRODUCT, "1234", "John Doe"));

        Mockito.when(finder.all()).thenReturn(events);

        List<Event> actual = eventServiceUnderTest.findEvents();
        
        List<Event> expected = events.stream()
                .filter(e -> !e.type.equals(CHANGE_USER_ACCESS))
                .collect(toList());
        assertThat(actual).isEqualTo(expected);
    }

    
}
