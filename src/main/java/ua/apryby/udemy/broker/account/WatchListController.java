package ua.apryby.udemy.broker.account;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.apryby.udemy.broker.model.WatchList;
import ua.apryby.udemy.broker.store.InMemoryAccountStore;

import java.util.UUID;

@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller("/account/watchlist")
public class WatchListController {

    private static final Logger LOG = LoggerFactory.getLogger(WatchListController.class);

    public static final UUID ACCOUNT_ID = UUID.randomUUID();

    private final InMemoryAccountStore store;

    public WatchListController(final InMemoryAccountStore store) {
        this.store = store;
    }

    @Get(produces = MediaType.APPLICATION_JSON)
    public WatchList get() {
        LOG.debug("get - {}", Thread.currentThread().getName());
        return store.getWatchList(ACCOUNT_ID);
    }

    @Put(consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public WatchList update(@Body WatchList watchList) {
        return store.updateWatchList(ACCOUNT_ID, watchList);
    }

    @Delete(value = "/{accountId}", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public void delete(@PathVariable UUID accountId) {
        store.deleteWatchList(accountId);
    }
}
