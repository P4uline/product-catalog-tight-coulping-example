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
    public void admin_should_see_all_events() {
        Mockito.when(authenticatorServiceMock.getCurrentUser()).thenReturn(new User("Jane Doe", ADMIN));
        List<Event> events = new ArrayList<>();
        events.add(newEvent(CONSULT_ALL_PRODUCTS, "no-ean", "Jane Doe"));
        events.add(newEvent(CHANGE_USER_ACCESS, "no-ean", "Jane Doe"));
        events.add(newEvent(CONSULT_PRODUCT, "34283", "Jane Doe"));
        events.add(newEvent(EDIT_PRODUCT, "34283", "John Doe"));
        events.add(newEvent(CONSULT_PRODUCT, "34283", "John Doe"));
        events.add(newEvent(CONSULT_ALL_PRODUCTS, "34283", "John Doe"));
        events.add(newEvent(CREATE_PRODUCT, "1234", "John Doe"));
        
        Mockito.when(finder.all()).thenReturn(events);

        List<Event> result = eventServiceUnderTest.findEvents();
        assertThat(result).hasSize(7);
        assertThat(result).isEqualTo(events);
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

        Mockito.when(finder.all()).thenReturn(events);

        List<Event> result = eventServiceUnderTest.findEvents();
        assertThat(result).hasSize(4);
        assertThat(result).doesNotContain(newEvent(CHANGE_USER_ACCESS, "no-ean", "John Doe"));
        assertThat(result).isEqualTo(events.stream().filter(e -> !e.type.equals(CHANGE_USER_ACCESS) && e.owner.equals("John Doe")).collect(toList()));
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

        List<Event> result = eventServiceUnderTest.findEvents();
        assertThat(result).hasSize(6);
        assertThat(result).doesNotContain(newEvent(CHANGE_USER_ACCESS, "no-ean", "John Doe"));
        assertThat(result).isEqualTo(events.stream().filter(e -> !e.type.equals(CHANGE_USER_ACCESS)).collect(toList()));
    }
}
