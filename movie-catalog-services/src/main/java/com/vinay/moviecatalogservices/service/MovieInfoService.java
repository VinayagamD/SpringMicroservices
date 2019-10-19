package com.vinay.moviecatalogservices.service;


import com.vinay.moviecatalogservices.models.CatalogItem;
import com.vinay.moviecatalogservices.models.Rating;

public interface MovieInfoService {

    CatalogItem getCatalogItem(Rating rating);

}
