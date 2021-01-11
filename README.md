This was created for educational purposes.
The app can be started with command: mvn spring-boot:run
Currently app can add, get, update and delete movies from the database. 
For searching for movies it is first checked in local in memory database.
If there is no movie which has the same title as queried for than it is searched on https://www.themoviedb.org/.
For searching on mentioned website api, it is required to have the api key saved in src/main/resources/api.key
Default port for app is 8080.
Use cases implemented:
1) To get list of all movies: send HTTP GET to http://localhost:8080/movies
2) To get single movie nased on its id send: HTTP GET to http://localhost:8080/movies/{id}
3) Adding a movie to the database send HTTP POST http://localhost:8080/movies with corresponding json in the body
	Example of appropriate movie in json format is in src/main/resources/movie.json
4) Searching for a movie based on a title: HTTP GET http://localhost:8080/movies/search/{movie_title}

Getting all movies and searching for a movie based on a title return list of Movie object.