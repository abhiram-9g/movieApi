package com.example.movieAPI.service;

import com.example.movieAPI.dto.MovieDto;
import com.example.movieAPI.dto.MoviePageResponse;
import com.example.movieAPI.entities.Movie;
import com.example.movieAPI.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class MovieServiceImpl implements MovieService{

    private final MovieRepository movieRepository;

    private final FileService fileService;

    @Value("${project.poster}")
    private String path;

    @Value("${base.url}")
    private String baseUrl;

    public MovieServiceImpl(MovieRepository movieRepository, FileService fileService) {
        this.movieRepository = movieRepository;
        this.fileService = fileService;
    }

    @Override
    public MovieDto addMovie(MovieDto movieDto, MultipartFile file) throws IOException {

        //1. Uploading the file
        String uploadedFileName = fileService.uploadFile(path,file);

        //2. set the value of the poster as filename
        movieDto.setPoster(uploadedFileName);

        //3.map dto to movie object
        // we are converting the map dto to movie object because the movie repository for saving the movie details uses "movie" object.

        Movie movie = new Movie(
                null,
                movieDto.getTitle(),
                movieDto.getDirector(),
                movieDto.getStudioName(),
                movieDto.getCast(),
                movieDto.getReleaseYear(),
                movieDto.getPoster()
        );

        //4. save the movie object - > saved movie object is returned.
        Movie savedMovie = movieRepository .save(movie);

        //5. generate the posterurl
        String posterUrl = baseUrl+ "/file/" + uploadedFileName;

        //6. map Movie object to movieDTO object  in order to return it
        MovieDto response = new MovieDto(
                savedMovie.getMovieId(),
                savedMovie.getTitle(),
                savedMovie.getDirector(),
                savedMovie.getStudioName(),
                savedMovie.getCast(),
                savedMovie.getReleaseYear(),
                savedMovie.getPoster(),
                posterUrl
        );
        return response;
    }

    @Override
    public MovieDto getMovie(Integer movieId) {
        //1. check the data in db and if exists, fetch the data of given ID
        Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new RuntimeException("movie not found"));

        //2. generate posterurl
        String posterUrl = baseUrl+"/file/"+movie.getPoster();

        //3. map to movieDto object and return it
        MovieDto response = new MovieDto(
                movie.getMovieId(),
                movie.getTitle(),
                movie.getDirector(),
                movie.getStudioName(),
                movie.getCast(),
                movie.getReleaseYear(),
                movie.getPoster(),
                posterUrl
        );

        return response;

    }

    @Override
    public List<MovieDto> getAllMovies() {
        //1.fetch all data from db
        List<MovieDto> list = new ArrayList<>();
        List<Movie> movies = movieRepository.findAll();

        //2. iterate through the list, generate posterurl for each movie obj
        for (int i = 0; i < movies.size(); i++) {
            Movie t = movies.get(i);
            String poster = t.getPoster();
            String posterUrl = baseUrl +"/file/"+poster;
            MovieDto data = new MovieDto(
                    t.getMovieId(),
                    t.getTitle(),
                    t.getDirector(),
                    t.getStudioName(),
                    t.getCast(),
                    t.getReleaseYear(),
                    t.getPoster(),
                    posterUrl
            );
            list.add(data);
        }

        return list;
    }

    @Override
    public MovieDto updateMovie(Integer movieId, MovieDto movieDto, MultipartFile file) throws IOException {
        //1. check if the movie exists or not with given id
        Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new RuntimeException("Movie does not exist."));

        //2. if exists -> if file is null
        // if file is not null, then delete existing file associated with the record
        String filename = movie.getPoster();
        if(file != null){
            Files.deleteIfExists(Paths.get(path+File.separator+filename));
            filename = fileService.uploadFile(path,file);
        }


        //3. set moviedto poster value
        movieDto.setPoster(filename);

        //4. map to movie object.
        Movie m = new Movie(
                movie.getMovieId(),
                movieDto.getTitle(),
                movieDto.getDirector(),
                movieDto.getStudioName(),
                movieDto.getCast(),
                movieDto.getReleaseYear(),
                movieDto.getPoster()
        );
        //5. save the movie object
        movieRepository.save(m);

        //6. set the poster url
        String posterUrl = baseUrl + "/file/" + filename;

        //map to movieDto and return it
        MovieDto dt = new MovieDto(
                m.getMovieId(),
                m.getTitle(),
                m.getDirector(),
                m.getStudioName(),
                m.getCast(),
                m.getReleaseYear(),
                m.getPoster(),
                posterUrl
        );
        return dt;
    }


    @Override
    public String deleteMovie(Integer movieId) throws IOException {
        //check if movie exists
        Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new RuntimeException("Movie does not exist."));
        Integer id = movie.getMovieId();
        //delete the file associated with the object
        Files.deleteIfExists(Paths.get(path+File.separator+movie.getPoster()));

        //delete the movie object
        movieRepository.delete(movie);

        return "Movie deleted with id : " + id;
    }

    @Override
    public MoviePageResponse getAllMoviesWithPagination(Integer pageNumber, Integer pageSize) {
        //by default pagination feature is offered by spring boot
        // which can be accessed by pageable interface.
        Pageable pageable = PageRequest.of(pageNumber,pageSize);

        Page<Movie> moviePage = movieRepository.findAll(pageable);
        List<Movie> movies = moviePage.getContent();


        List<MovieDto> list = new ArrayList<>();
        //2. iterate through the list, generate posterurl for each movie obj
        for (int i = 0; i < movies.size(); i++) {
            Movie t = movies.get(i);
            String poster = t.getPoster();
            String posterUrl = baseUrl +"/file/"+poster;
            MovieDto data = new MovieDto(
                    t.getMovieId(),
                    t.getTitle(),
                    t.getDirector(),
                    t.getStudioName(),
                    t.getCast(),
                    t.getReleaseYear(),
                    t.getPoster(),
                    posterUrl
            );
            list.add(data);
        }

        return  new MoviePageResponse(list,pageNumber,pageSize,moviePage.getTotalElements(),moviePage.getTotalPages(),moviePage.isLast());

    }

    @Override
    public MoviePageResponse getAllMoviesWithPaginationAndSorting(Integer pageNumber, Integer pageSize, String sortBy, String dir) {
        Sort sort = dir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNumber,pageSize,sort);

        Page<Movie> moviePage = movieRepository.findAll(pageable);
        List<Movie> movies = moviePage.getContent();


        List<MovieDto> list = new ArrayList<>();
        //2. iterate through the list, generate posterurl for each movie obj
        for (int i = 0; i < movies.size(); i++) {
            Movie t = movies.get(i);
            String poster = t.getPoster();
            String posterUrl = baseUrl +"/file/"+poster;
            MovieDto data = new MovieDto(
                    t.getMovieId(),
                    t.getTitle(),
                    t.getDirector(),
                    t.getStudioName(),
                    t.getCast(),
                    t.getReleaseYear(),
                    t.getPoster(),
                    posterUrl
            );
            list.add(data);
        }

        return  new MoviePageResponse(list,pageNumber,pageSize,moviePage.getTotalElements(),moviePage.getTotalPages(),moviePage.isLast());

    }


}
