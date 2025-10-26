package com.example.movieAPI.controllers;

import com.example.movieAPI.dto.MovieDto;
import com.example.movieAPI.dto.MoviePageResponse;
import com.example.movieAPI.service.MovieService;
import com.example.movieAPI.utils.AppConstants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/movie")
public class MovieController {
    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @PostMapping("/add-movie")
    public ResponseEntity<MovieDto> addMovieHandler(@RequestPart MultipartFile file, @RequestPart String movieDto) throws IOException {

      MovieDto dto = convertToMovieDto(movieDto);
     return new ResponseEntity<>(movieService.addMovie(dto,file), HttpStatus.CREATED);
    }

    @GetMapping("/{movieId}")
    public ResponseEntity<MovieDto> getMovieHandler(@PathVariable Integer movieId){
        return  ResponseEntity.ok(movieService.getMovie(movieId));
    }

    @GetMapping("/all")
    public ResponseEntity<List<MovieDto>> getAllMoviesHandler(){
        return ResponseEntity.ok(movieService.getAllMovies());
    }

    @PutMapping("/update/{movieId}")
    public ResponseEntity<MovieDto> updateMovieHandler(@PathVariable Integer movieId,MultipartFile file,@RequestPart String movietodto) throws IOException {
        if(file.isEmpty())
            file = null;
        MovieDto movieDto = convertToMovieDto(movietodto);
        return ResponseEntity.ok(movieService.updateMovie(movieId,movieDto,file));

    }

    @DeleteMapping("/delete/{movieId}")
    public ResponseEntity<String>  deleteMovieHandler(@PathVariable Integer movieId) throws IOException{
        return ResponseEntity.ok(movieService.deleteMovie(movieId));
    }

    @GetMapping("/allMoviesPage")
    public ResponseEntity<MoviePageResponse> getMoviesWithPagination(
            @RequestParam(defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(defaultValue = AppConstants.PAGE_SIZE,required = false) Integer pageSize
    ){
        return ResponseEntity.ok(movieService.getAllMoviesWithPagination(pageNumber,pageSize));
    }


    @GetMapping("/allMoviesPageSort")
    public ResponseEntity<MoviePageResponse> getMoviesWithPaginationAndSorting(
            @RequestParam(defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(defaultValue = AppConstants.PAGE_SIZE,required = false) Integer pageSize,
            @RequestParam(defaultValue = AppConstants.SORT_BY,required = false) String sortBy,
            @RequestParam(defaultValue = AppConstants.SORT_DIR,required = false) String sortDir
    ){
        return ResponseEntity.ok(movieService.getAllMoviesWithPaginationAndSorting(pageNumber,pageSize,sortBy,sortDir));
    }

    //this function is written since we need to convert string object to json format in order to read it.
    // we cannot directly pass json in th input, we have to pass it as a string and then convert it to json and then convert it to moviedto desired object.
    private MovieDto convertToMovieDto(String movieDtoToObj) throws JsonProcessingException {

        //object mapper is provided by jackson
        ObjectMapper objectMapper = new ObjectMapper();
        MovieDto movieDto = objectMapper.readValue(movieDtoToObj,MovieDto.class);
        //in the above read value functino it takes 2 arguments the string to convert and to the class of the object it should be converted to.

        return movieDto;

    }
}
