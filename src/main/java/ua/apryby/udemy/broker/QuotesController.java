package ua.apryby.udemy.broker;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import ua.apryby.udemy.broker.model.Quote;
import ua.apryby.udemy.broker.store.InMemoryStore;

import java.util.Optional;

@Controller("/quotes")
public class QuotesController {

    private final InMemoryStore store;


    public QuotesController(InMemoryStore store) {
        this.store = store;
    }

    @Get("/{symbol}")
    public HttpResponse getQuote(@PathVariable String symbol) {
        final Optional<Quote> quote = store.fetchQuote(symbol);
        return HttpResponse.ok(quote.get());
    }
}