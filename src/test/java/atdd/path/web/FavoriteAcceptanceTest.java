package atdd.path.web;

import atdd.path.AbstractAcceptanceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

public class FavoriteAcceptanceTest extends AbstractAcceptanceTest {
    public static final String FAVORITE_URI = "/favorites";

    @DisplayName("지하철역 즐겨찾기 등록")
    @Test
    public void registerFavoriteForStation() {
        String inputJson = String.format("{\"name\": \"%s\"", "강남역");

        webTestClient
                .post()
                .uri(FAVORITE_URI + "/stations")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(inputJson), String.class)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectHeader()
                .contentType(MediaType.APPLICATION_JSON)
                .expectHeader()
                .exists("Location");
    }

}
