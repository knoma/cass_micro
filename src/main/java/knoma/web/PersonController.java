package knoma.web;

import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import knoma.web.dao.PersonDAO;
import knoma.web.dao.PersonMapper;
import knoma.web.dao.PersonMapperBuilder;
import knoma.web.pojo.Person;

import java.net.URI;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import javax.inject.Inject;

import static io.micronaut.http.HttpResponse.created;
import static io.micronaut.http.HttpResponse.noContent;
import static io.micronaut.http.HttpResponse.notFound;
import static io.micronaut.http.HttpResponse.ok;

@Controller("/person")
public class PersonController implements PersonAPI{

    private final PersonDAO personDAO;

    @Inject
    public PersonController(CqlSession session) {
        PersonMapper personMapper = new PersonMapperBuilder(session).build();
        this.personDAO = personMapper.personDao(CqlIdentifier.fromCql("cass_drop"));
    }

    @Get(uri = "/all", produces = MediaType.APPLICATION_JSON)
    public Single<HttpResponse<Iterable<Person>>> getAll() {

        return Observable
                .fromFuture(personDAO.getAll().toCompletableFuture())
                .map(p -> (HttpResponse<Iterable<Person>>) ok(p.currentPage()))
                .singleOrError();
    }

    @Get(uri = "/count", produces = MediaType.APPLICATION_JSON)
    public Single<HttpResponse<Long>> getCount() {
        return Observable
                .fromFuture(personDAO.getCount().toCompletableFuture())
                .map(p -> (HttpResponse<Long>) ok(p))
                .singleOrError();
    }

    @Get(uri = "/{id}", produces = MediaType.APPLICATION_JSON)
    public Maybe<HttpResponse<Person>> get(UUID id){

        return Observable
                .fromFuture(personDAO.getById(id).toCompletableFuture())
                .firstElement().map(person -> (HttpResponse<Person>) ok(person))
                .onErrorReturnItem(noContent());
    }

    @Post(uri = "/", produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON)
    public Maybe<HttpResponse<Void>> save(Person person) {

        Observable
                .fromFuture(personDAO.saveAsync(person).toCompletableFuture())
                .subscribe(unused -> {}, throwable -> {}, () -> {});

        return Maybe.just(created(URI.create("/person/"+ person.getId())));
    }

    @Delete(uri = "/{id}", produces = MediaType.APPLICATION_JSON)
    public Maybe<HttpResponse<Void>> delete(UUID id) {

         Observable
                .fromFuture(personDAO.delete(id).toCompletableFuture())
        .subscribe(unused -> {}, throwable -> {}, () -> {});

        return Maybe.just(noContent());
    }
}