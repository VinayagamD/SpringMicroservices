package com.vinay.moviecatalogservices.resources;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.vinay.moviecatalogservices.models.CatalogItem;
import com.vinay.moviecatalogservices.models.Movie;
import com.vinay.moviecatalogservices.models.Rating;
import com.vinay.moviecatalogservices.models.UserRating;
import com.vinay.moviecatalogservices.service.MovieInfoService;
import com.vinay.moviecatalogservices.service.UserRatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/catalog")
public class MovieCatalogResource {

   /* private final RestTemplate restTemplate;
    private final WebClient.Builder webClientBuilder;*/


    @Autowired
    private MovieInfoService movieInfoService;

    @Autowired
    private UserRatingService userRatingService;



  /*  @Autowired
    public MovieCatalogResource(RestTemplate restTemplate, @Qualifier("getWebClientBuilder") WebClient.Builder webClientBuilder) {
        this.restTemplate = restTemplate;
        this.webClientBuilder = webClientBuilder;
    }*/

    @GetMapping("/{userId}")
    public List<CatalogItem> getCatalog(@PathVariable("userId") String userId){

        // Get all rated movies id
        UserRating userRating = userRatingService.getUserRating(userId);

        return userRating.getRatings().stream().map(rating -> movieInfoService.getCatalogItem(rating)).collect(Collectors.toList());

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
