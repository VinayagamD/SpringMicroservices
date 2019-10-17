package com.vinay.moviecatalogservices.resources;

import com.netflix.discovery.DiscoveryClient;
import com.vinay.moviecatalogservices.models.CatalogItem;
import com.vinay.moviecatalogservices.models.Movie;
import com.vinay.moviecatalogservices.models.Rating;
import com.vinay.moviecatalogservices.models.UserRating;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/catalog")
public class MovieCatalogResource {

    private final RestTemplate restTemplate;
    private final WebClient.Builder webClientBuilder;

    private DiscoveryClient discoveryClient;

    @Autowired
    public MovieCatalogResource(RestTemplate restTemplate, @Qualifier("getWebClientBuilder") WebClient.Builder webClientBuilder, DiscoveryClient discoveryClient) {
        this.restTemplate = restTemplate;
        this.webClientBuilder = webClientBuilder;
        this.discoveryClient = discoveryClient;
    }

    @GetMapping("/{userId}")
    public List<CatalogItem> getCatalog(@PathVariable("userId") String userId){

        // Get all rated movies id
        UserRating userRating = restTemplate.getForObject("http://ratings-data-service/ratingsData/users/"+userId, UserRating.class);

       return userRating.getRatings().stream().map(rating -> {
           Movie movie = restTemplate.getForObject("http://movie-info-service/movies/"+rating.getMovieId(), Movie.class);
          /* Movie movie = webClientBuilder.build()
                   .get()
                   .uri("http://localhost:8082/movies/"+rating.getMovieId())
                   .retrieve()
                   .bodyToMono(Movie.class)
                   .block();*/
           return new CatalogItem(movie.getName(), "Test", rating.getRating());

        }).collect(Collectors.toList());

    }
}
