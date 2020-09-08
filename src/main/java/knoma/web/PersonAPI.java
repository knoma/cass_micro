package knoma.web;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.reactivex.Maybe;
import io.reactivex.Single;
import knoma.web.pojo.Person;

import java.util.UUID;

public interface PersonAPI {

    @Get(uri = "/all", produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON)
    Single<HttpResponse<Iterable<Person>>> getAll();

    @Get(uri = "/count", produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON)
    Single<HttpResponse<Long>> getCount();

    @Get(uri = "/{id}", produces = MediaType.APPLICATION_JSON)
    Maybe<HttpResponse<Person>> get(UUID id);

    @Post(uri = "/", produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON)
    Maybe<HttpResponse<Void>> save(Person person);


    @Delete(uri = "/{id}", produces = MediaType.APPLICATION_JSON)
    Maybe<HttpResponse<Void>> delete(UUID id);
}
