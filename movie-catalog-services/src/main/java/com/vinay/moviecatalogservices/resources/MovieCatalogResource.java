package com.vinay.moviecatalogservices.resources;

import com.netflix.discovery.DiscoveryClient;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
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



    @Autowired
    public MovieCatalogResource(RestTemplate restTemplate, @Qualifier("getWebClientBuilder") WebClient.Builder webClientBuilder) {
        this.restTemplate = restTemplate;
        this.webClientBuilder = webClientBuilder;
    }

    @GetMapping("/{userId}")
    @HystrixCommand(fallbackMethod = "getFallbackCatalog")
    public List<CatalogItem> getCatalog(@PathVariable("userId") String userId){

        // Get all rated movies id
        UserRating userRating = getUserRating(userId);

        return userRating.getRatings().stream().map(this::getCatalogItem).collect(Collectors.toList());

    }


    @HystrixCommand(fallbackMethod = "getFallbackUserRating")
    private UserRating getUserRating(@PathVariable("userId") String userId) {
        return restTemplate.getForObject("http://ratings-data-service/ratingsData/users/"+userId, UserRating.class);
    }

    @HystrixCommand(fallbackMethod = "getFallbackCatalogItem")
    private CatalogItem getCatalogItem(Rating rating) {
        Movie movie = restTemplate.getForObject("http://movie-info-service/movies/"+rating.getMovieId(), Movie.class);
        return new CatalogItem(movie.getName(), movie.getDesc(), rating.getRating());
    }


    private CatalogItem getFallbackCatalogItem(Rating rating){
        return new CatalogItem("Movie name not found", "", rating.getRating());
    }

    private UserRating getFallbackUserRating(@PathVariable("userId") String userId){
        UserRating userRating = new UserRating();
        userRating.setUserId(userId);
        userRating.setRatings(Arrays.asList(
                new Rating("0", 0)
        ));

        return userRating;
    }

    public List<CatalogItem> getFallbackCatalog(@PathVariable("userId") String userId){
        return Arrays.asList(new CatalogItem("No movie", "", 0));
    }

    /*
        Alternate Webclient way
          Movie movie = webClientBuilder.build()
                   .get()
                   .uri("http://localhost:8082/movies/"+rating.getMovieId())
                   .retrieve()
                   .bodyToMono(Movie.class)
                   .block();
     */
}
