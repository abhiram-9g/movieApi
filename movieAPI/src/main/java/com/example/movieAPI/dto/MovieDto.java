package com.example.movieAPI.dto;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.Set;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class MovieDto {

    private Integer movieId;


    @NotBlank(message = "Please provide movie's title.")
    private String title;


    @NotBlank(message = "Please provide movie's director.")
    private String director;


    @NotBlank(message = "Please provide movie's studioName.")
    private String studioName;

    private Set<String> cast;

    private Integer releaseYear;

    @NotBlank(message = "Please provide movie's poster.")
    private String poster;

    @NotBlank(message = "Please provide poster's url")
    private String posterUrl;
}
