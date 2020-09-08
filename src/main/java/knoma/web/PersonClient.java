package knoma.web;

import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.HttpStatus;

@Client("/person")
public interface PersonClient extends PersonAPI {
}