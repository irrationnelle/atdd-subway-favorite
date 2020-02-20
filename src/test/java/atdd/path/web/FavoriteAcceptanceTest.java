package atdd.path.web;

import atdd.path.AbstractAcceptanceTest;
import atdd.path.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;

public class FavoriteAcceptanceTest extends AbstractAcceptanceTest {
    public static final String FAVORITE_URI = "/favorite";
    private String tokenInfo;

    private UserHttpTest userHttpTest;

    @BeforeEach()
    void setUp() {
        this.userHttpTest = new UserHttpTest(webTestClient);

        String userUri = "/users";
        String userInputJson = String.format("{\"email\": \"%s\", \"name\": \"%s\", \"password\": \"%s\"}",
                "boorwonie@email.com", "브라운", "subway");
        String signInUri = userUri + "/login";
        String signInJson = String.format("{\"email\": \"%s\", \"password\" : \"%s\"}", "boorwonie@email.com",
                "subway");

        userHttpTest.createUserSuccess(userUri, userInputJson);
        tokenInfo = userHttpTest.createAuthorizationTokenSuccess(signInUri, signInJson);
    }

    @DisplayName("지하철역 즐겨찾기 등록")
    @Test
    public void registerFavoriteForStation() {
        String inputJson = String.format("{\"name\": \"%s\"}", "강남역");

        webTestClient
                .post()
                .uri(FAVORITE_URI + "/stations")
                .header("Authorization", tokenInfo)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(inputJson), String.class)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectHeader()
                .contentType(MediaType.APPLICATION_JSON)
                .expectBody(FavoriteResponseView.class)
                .consumeWith(result -> {
                    assertThat(result.getResponseBody()).isNotNull();
                });
    }
}
