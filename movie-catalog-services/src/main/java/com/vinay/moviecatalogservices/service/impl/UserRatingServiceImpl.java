package com.vinay.moviecatalogservices.service.impl;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.vinay.moviecatalogservices.models.Rating;
import com.vinay.moviecatalogservices.models.UserRating;
import com.vinay.moviecatalogservices.service.UserRatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@Service
public class UserRatingServiceImpl implements UserRatingService {

    @Autowired
    private RestTemplate restTemplate;

    @HystrixCommand(fallbackMethod = "getFallbackUserRating")
    public UserRating getUserRating(String userId) {
        return restTemplate.getForObject("http://ratings-data-service/ratingsData/users/"+userId, UserRating.class);
    }



    private UserRating getFallbackUserRating(@PathVariable("userId") String userId){
        UserRating userRating = new UserRating();
        userRating.setUserId(userId);
        userRating.setRatings(Arrays.asList(
                new Rating("0", 0)
        ));

        return userRating;
    }
}
