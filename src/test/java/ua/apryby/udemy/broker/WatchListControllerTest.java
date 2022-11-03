package ua.apryby.udemy.broker;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.client.RxHttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.security.authentication.UsernamePasswordCredentials;
import io.micronaut.security.token.jwt.render.BearerAccessRefreshToken;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.apryby.udemy.broker.account.WatchListController;
import ua.apryby.udemy.broker.model.Symbol;
import ua.apryby.udemy.broker.model.WatchList;
import ua.apryby.udemy.broker.store.InMemoryAccountStore;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.micronaut.http.HttpRequest.*;
import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
class WatchListControllerTest {

    private static final Logger LOG = LoggerFactory.getLogger(WatchListControllerTest.class);
    private static final UUID TEST_ACCOUNT_ID = WatchListController.ACCOUNT_ID;

    @Inject
    @Client("/")
    RxHttpClient client;

    @Inject
    InMemoryAccountStore store;

    @Test
    void wrongPasswordIsRejected() {
        try {
            final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("my-user", "wrong");
            var login = HttpRequest.POST("/login", credentials);
            client.toBlocking().exchange(login, BearerAccessRefreshToken.class);
        } catch (HttpClientResponseException e) {
            assertEquals(HttpStatus.UNAUTHORIZED, e.getStatus());
            assertEquals("Wrong username or password!", e.getMessage());
        }
    }

    @Test
    void wrongUserIsRejected() {
        try {
            final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("unknown-user", "secret");
            var login = HttpRequest.POST("/login", credentials);
            client.toBlocking().exchange(login, BearerAccessRefreshToken.class);
        } catch (HttpClientResponseException e) {
            assertEquals(HttpStatus.UNAUTHORIZED, e.getStatus());
            assertEquals("Wrong username or password!", e.getMessage());
        }
    }

    @Test
    void returnsEmptyWatchListForAccount() {
        BearerAccessRefreshToken token = givenMyUserIsLoggedIn();

        var request = GET("/account/watchlist")
                .accept(MediaType.APPLICATION_JSON)
                .bearerAuth(token.getAccessToken());
        final WatchList result = client.toBlocking().retrieve(request, WatchList.class);
        assertTrue(result.getSymbols().isEmpty());
        assertTrue(store.getWatchList(TEST_ACCOUNT_ID).getSymbols().isEmpty());
    }

    @Test
    void returnsWatchListForAccount() {
        BearerAccessRefreshToken token = givenMyUserIsLoggedIn();

        var request = GET("/account/watchlist")
                .accept(MediaType.APPLICATION_JSON)
                .bearerAuth(token.getAccessToken());

        final List<Symbol> symbols = Stream.of("APPL", "AMZN", "NFLX")
                .map(Symbol::new)
                .collect(Collectors.toList());
        WatchList watchList = new WatchList(symbols);
        store.updateWatchList(TEST_ACCOUNT_ID, watchList);

        final WatchList result = client.toBlocking().retrieve(request, WatchList.class);
        assertEquals(3, result.getSymbols().size());
        assertEquals(3, store.getWatchList(TEST_ACCOUNT_ID).getSymbols().size());
    }

    @Test
    void canUpdateWatchListForAccount() {
        BearerAccessRefreshToken token = givenMyUserIsLoggedIn();

        final List<Symbol> symbols = Stream.of("APPL", "AMZN", "NFLX")
                .map(Symbol::new)
                .collect(Collectors.toList());
        WatchList watchList = new WatchList(symbols);

        var request = PUT("/account/watchlist", watchList)
                .accept(MediaType.APPLICATION_JSON)
                .bearerAuth(token.getAccessToken());

        final HttpResponse<Object> added = client.toBlocking().exchange(request);
        assertEquals(HttpStatus.OK, added.getStatus());
        assertEquals(watchList, store.getWatchList(TEST_ACCOUNT_ID));
    }

    @Test
    void canDeleteWatchListForAccount() {
        final BearerAccessRefreshToken token = givenMyUserIsLoggedIn();

        final List<Symbol> symbols = Stream.of("APPL", "AMZN", "NFLX")
                .map(Symbol::new)
                .collect(Collectors.toList());
        WatchList watchList = new WatchList(symbols);
        store.updateWatchList(TEST_ACCOUNT_ID, watchList);
        assertFalse(store.getWatchList(TEST_ACCOUNT_ID).getSymbols().isEmpty());

        var request = DELETE("/account/watchlist/" + TEST_ACCOUNT_ID)
                .accept(MediaType.APPLICATION_JSON)
                .bearerAuth(token.getAccessToken());
        final HttpResponse<Object> deleted = client.toBlocking().exchange(request);
        assertEquals(HttpStatus.OK, deleted.getStatus());
        assertTrue(store.getWatchList(TEST_ACCOUNT_ID).getSymbols().isEmpty());
    }

    private BearerAccessRefreshToken givenMyUserIsLoggedIn() {
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("my-user", "secret");
        var login = HttpRequest.POST("/login", credentials);
        var response = client.toBlocking().exchange(login, BearerAccessRefreshToken.class);
        assertEquals(HttpStatus.OK, response.getStatus());
        BearerAccessRefreshToken token = response.body();
        assertNotNull(token);
        assertEquals("my-user", token.getUsername());
        LOG.debug("Login Bearer Token: {} expires in {}", token.getAccessToken(), token.getExpiresIn());
        return token;
    }
}
