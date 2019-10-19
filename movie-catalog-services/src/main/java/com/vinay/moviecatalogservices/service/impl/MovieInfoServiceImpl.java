package com.vinay.moviecatalogservices.service.impl;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.vinay.moviecatalogservices.models.CatalogItem;
import com.vinay.moviecatalogservices.models.Movie;
import com.vinay.moviecatalogservices.models.Rating;
import com.vinay.moviecatalogservices.service.MovieInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class MovieInfoServiceImpl implements MovieInfoService {

    @Autowired
    private RestTemplate restTemplate;

    @Override
    @HystrixCommand(fallbackMethod = "getFallbackCatalogItem")
    public CatalogItem getCatalogItem(Rating rating) {
        Movie movie = restTemplate.getForObject("http://movie-info-service/movies/"+rating.getMovieId(), Movie.class);
        return new CatalogItem(movie.getName(), movie.getDesc(), rating.getRating());
    }


    private CatalogItem getFallbackCatalogItem(Rating rating){
        return new CatalogItem("Movie name not found", "", rating.getRating());
    }
}
