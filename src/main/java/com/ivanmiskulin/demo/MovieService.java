package com.ivanmiskulin.demo;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ivanmiskulin.demo.entity.Movie;

import net.minidev.json.JSONObject;

@RestController
public class MovieService {

	@Autowired
	private MovieRepo repo;
	
	private String apiKey;
	
	@GetMapping(value = "movies/search/{title}")
	@ResponseBody
	public List<Movie> getMoviesByTitle(@PathVariable("title") String title) {
		// List of movies which will be returned as a result of the search request
		List<Movie> movies = new ArrayList<Movie>();
		// Get all movies in local database
		// and for each movie check if it contains searched title
		repo.findAll().forEach((movie) -> {
			if(movie.getTitle().contains(title)) {
				movies.add(movie);
			}
		});
		
		// If there are no movies found in local database with matching, search themoviedb.org 
		if(movies.isEmpty()) {
			try {
				// Get the api key for connecting to the themoviedb api
				String filePath = "src/main/resources/api.key";
				apiKey = new String(Files.readAllBytes(Paths.get(filePath)));
				String urlTitle = title.replace(" ", "%20");
				// Create search query for themoviedb api
				URL url = new URL("https://api.themoviedb.org/3/search/movie?api_key=" + apiKey + "&query=" + urlTitle);
				HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
				con.setRequestMethod("GET");
				con.setRequestProperty("Content-Type", "application/json");
				int responseCode = con.getResponseCode();
				if(responseCode != 200) {
					throw new RuntimeException("HttpResponseCode: " + responseCode);
				}
				ObjectMapper mapper = new ObjectMapper();
				// Read content as a JSON object
				JSONObject json = mapper.readValue(url, JSONObject.class);
				// Convert to string 
				String sjson = json.toJSONString();
				// Get the results part of string which contains list of movies as array of json objects
				sjson = sjson.substring(sjson.indexOf("[") + 1, sjson.lastIndexOf("]"));
				// To save each json object string in array
				// Split by first field in each json object
				String[] movieString = sjson.split("\\{\"adult\":");
				for(String m : movieString) {
					// Trim each line in array of Strings so the line with white space only are avoided
					m.trim();	
					if(!m.isEmpty()) {
						// Add back start of each string with which it was split previously
						m = "{\"adult\":" + m;
						m = m.substring(0, m.lastIndexOf("}") + 1);
						// Convert the whole string to the movie object
						Movie movie = new ObjectMapper().readValue(m, Movie.class);
						// Saving movie to the repository returns movie instance with id that local database generated when saving the object
						// Then add object with new generated id to the list which will be returned to the client
						movies.add(repo.save(movie));
					}
				}
				
			} catch (MalformedURLException e) {
				System.out.println("Malformed URL.");
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println("Error opening url connection. Check api key.");
				e.printStackTrace();
			} catch (ClassCastException e) {
				System.out.println("Failed casting. No movies found.");
			} 
		}
		return movies;
	}
}
