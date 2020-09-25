package io.github.haopooby.entity

import org.springframework.data.repository.PagingAndSortingRepository

interface AdsRepository : PagingAndSortingRepository<Ads, String> {

}