package ua.apryby.udemy.broker;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import ua.apryby.udemy.broker.model.Symbol;
import ua.apryby.udemy.broker.store.InMemoryStore;

import java.util.List;

@Controller("/markets")
public class MarketsController {

    private final InMemoryStore store;

    public MarketsController(final InMemoryStore store) {
        this.store = store;
    }

    @Get("/")
    public List<Symbol> all() {
        return store.getAllSymbols();
    }

}
