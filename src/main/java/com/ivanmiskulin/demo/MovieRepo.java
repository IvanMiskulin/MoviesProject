package com.ivanmiskulin.demo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.ivanmiskulin.demo.entity.Movie;

@RepositoryRestResource(collectionResourceRel = "movie", path = "movies")
public interface MovieRepo extends JpaRepository<Movie, Integer> {

	public List<Movie> findByTitle(String title);
	
}
