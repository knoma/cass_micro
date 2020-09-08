package knoma.web;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.annotation.MicronautTest;
import knoma.web.dao.PersonDAO;
import knoma.web.dao.PersonMapper;
import knoma.web.dao.PersonMapperBuilder;
import knoma.web.pojo.Person;
import org.apache.commons.collections.IteratorUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@MicronautTest
public class PersonControllerTest {

    public static final UUID USER_ID1 = UUID.randomUUID();
    @Inject
    @Client("/")
    RxHttpClient client;

    @Inject
    PersonClient personClient;

    @BeforeAll
    static void beforeAll(CqlSession session) {
        PersonMapper personMapper = new PersonMapperBuilder(session).build();
        session.execute("truncate TABLE cass_drop.person");
        final PersonDAO dao = personMapper.personDao(CqlIdentifier.fromCql("cass_drop"));
        dao.saveAsync(new Person(USER_ID1, "test1", "tets2", "test1@"));
        dao.saveAsync(new Person(UUID.randomUUID(), "test2", "tets2", "test2@"));
        dao.saveAsync(new Person(UUID.randomUUID(), "test3", "tets3", "test3@"));
        dao.saveAsync(new Person(UUID.randomUUID(), "test4", "tets4", "test4@"));
        dao.saveAsync(new Person(UUID.randomUUID(), "test5", "tets5", "test5@"));
        dao.saveAsync(new Person(UUID.randomUUID(), "test6", "tets6", "test6@"));
        dao.saveAsync(new Person(UUID.randomUUID(), "test7", "tets7", "test7@"));
        dao.saveAsync(new Person(UUID.randomUUID(), "test8", "tets8", "test8@"));
        dao.saveAsync(new Person(UUID.randomUUID(), "test9", "tets9", "test9@"));
    }

    @Test
    public void testGetCount() throws Exception {
        final HttpResponse<Long> response = personClient.getCount().blockingGet();
        assertEquals(HttpStatus.OK, response.status());
        assertEquals(9, response.body());
    }

    @Test
    public void testGetAll() throws Exception {
        final HttpResponse<Iterable<Person>> exchange = personClient.getAll().blockingGet();
        assertEquals(HttpStatus.OK, exchange.status());
        assertEquals(10, IteratorUtils.toList(exchange.body().iterator()).size());
    }

    @Test
    public void testGetById() {
        final HttpResponse<Person> response = personClient.get(USER_ID1).blockingGet();
        assertEquals(HttpStatus.OK, response.status());
        assertNotNull(response.body());
    }

    @Test
    public void testSave() {
        UUID USER_ID2 = UUID.randomUUID();
        final Person person = new Person(USER_ID2, "test11", "tets11", "test11@");
        final HttpResponse<Void> response = personClient.save(person).blockingGet();
        assertEquals(HttpStatus.CREATED, response.status());

        final HttpResponse<Person> responsePerson = personClient.get(USER_ID2).blockingGet();
        assertEquals(HttpStatus.OK, responsePerson.status());
        assertEquals(person, responsePerson.body());
    }

    @Test
    public void testDeleteById() {
        UUID USER_ID2 = UUID.randomUUID();
        final Person person = new Person(USER_ID2, "test1", "tets2", "test1@");

        final HttpResponse<Void> exchange = personClient.save(person).blockingGet();
        assertEquals(HttpStatus.CREATED, exchange.status());

        final HttpResponse<Person> personHttpResponse = personClient.get(USER_ID2).blockingGet();
        final Person actual = personHttpResponse.body();
        assertNotNull(actual);
        assertEquals(USER_ID2, person.getId());

        final HttpResponse<Void> exchange2 = personClient.delete(USER_ID2).blockingGet();
        assertEquals(HttpStatus.NO_CONTENT, exchange2.status());

        assertNull(personClient.get(USER_ID2).blockingGet().body());
    }


    @Test
    public void testGetByIdNotFound() {
        final HttpResponse<Person> exchange = personClient.get(UUID.fromString("16992983-af17-43ad-9fc9-b9a654a42d36")).blockingGet();
        assertEquals(HttpStatus.NO_CONTENT, exchange.status());
        assertNull(exchange.body());
    }
}
