package atdd.path.web;

import atdd.path.application.dto.CreateStationRequestView;
import atdd.path.auth.JwtTokenProvider;
import atdd.path.domain.Station;
import atdd.path.domain.User;
import atdd.path.repository.StationRepository;
import atdd.path.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/favorites")
public class FavoriteController {
    private StationRepository stationRepository;
    private UserRepository userRepository;

    private JwtTokenProvider jwtTokenProvider;

    public FavoriteController(StationRepository stationRepository, UserRepository userRepository, JwtTokenProvider jwtTokenProvider) {
        this.stationRepository = stationRepository;
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping
    public ResponseEntity createUser(@RequestHeader(value = "Authorization") String authorization,
                                     @RequestBody CreateStationRequestView view) {
        String token = authorization.split(" ")[1];
        String parsedEmail = jwtTokenProvider.parseToken(token);
        Optional<User> optionalUser = userRepository.findByEmail(parsedEmail);

        if (optionalUser.isPresent()) {
            Long userId = optionalUser.orElse(null).getId();

            Optional<Favorite> favorite = favoriteRepository.findByUserId(userId);
            Optional<Station> station = stationRepository.findByName(view.getName());

            if (station.isPresent()) {
                if (favorite.isPresent()) {
                    List<Station> stations = favorite.getStations();

                    stations.add(view.toStation());

                    favoriteRepository.save(favorite);

                    return ResponseEntity.ok().body(FavoriteResponseView.of(favorite));
                }

                List<Station> newStations = new ArrayList<Station>();
                newStations.add(view.toStation());

                Favorite newFavorite = new Favorite(optionalUser.get(), newStations);
                return ResponseEntity.ok().body(FavoriteResponseView.of(newFavorite));
            }
            return ResponseEntity.noContent().build();
        }
        return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
    }
}
