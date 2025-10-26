package com.example.movieAPI.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.Set;

@Entity
@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer movieId;

    @Column(nullable = false)
    @NotBlank(message = "Please provide movie's title.")
    private String title;

    @Column(nullable = false)
    @NotBlank(message = "Please provide movie's director.")
    private String director;

    @Column(nullable = false)
    @NotBlank(message = "Please provide movie's studioName.")
    private String studioName;

    @ElementCollection
    @CollectionTable(name = "movie_cast")
    private Set<String> cast;

    @Column(nullable = false)
    private Integer releaseYear;

    @Column(nullable = false)
    @NotBlank(message = "Please provide movie's poster.")
    private String poster;
}
