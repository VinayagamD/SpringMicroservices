package com.vinay.moviecatalogservices.service;

import com.vinay.moviecatalogservices.models.UserRating;
import org.springframework.web.bind.annotation.PathVariable;

public interface UserRatingService {

    UserRating getUserRating(String userId);
}
