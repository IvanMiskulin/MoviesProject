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
		List<Movie> movies = new ArrayList<Movie>();
		try {
			String filePath = "src/main/resources/api.key";
			apiKey = new String(Files.readAllBytes(Paths.get(filePath)));
		} catch (IOException e) {
			System.out.println("Error reading api key.");
			e.printStackTrace();
		}
		// Get movies with matching title from in memory database
		movies = repo.findByTitle(title);
		// if there no movies with matching title found in database search themoviedb.org 
		if(movies.isEmpty()) {
			try {
				String urlTitle = title.replace(" ", "%20");
				URL url = new URL("https://api.themoviedb.org/3/search/movie?api_key=" + apiKey + "&query=" + urlTitle);
				HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
				con.setRequestMethod("GET");
				con.setRequestProperty("Content-Type", "application/json");
				int responseCode = con.getResponseCode();
				if(responseCode != 200) {
					throw new RuntimeException("HttpResponseCode: " + responseCode);
				}
				ObjectMapper mapper = new ObjectMapper();				
				JSONObject json = mapper.readValue(url, JSONObject.class);
				
				String sjson = json.toJSONString();
				sjson = sjson.substring(sjson.indexOf("[") + 1, sjson.lastIndexOf("]"));

				String[] movieString = sjson.split("\\{\"adult\":");
				for(String m : movieString) {
					m.trim();	
					if(!m.isEmpty()) {
						m = "{\"adult\":" + m;
						m = m.substring(0, m.lastIndexOf("}") + 1);
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
				System.out.println("Error opening url connection.");
				e.printStackTrace();
			} catch (ClassCastException e) {
				System.out.println("Failed casting. No movies found.");
			} 
		}
		return movies;
	}
}
