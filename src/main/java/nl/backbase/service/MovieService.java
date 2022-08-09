package nl.backbase.service;

import lombok.extern.slf4j.Slf4j;
import nl.backbase.controller.exception.MovieNotFoundException;
import nl.backbase.dto.BestPictureMovieDTO;
import nl.backbase.dto.MovieTop10DTO;
import nl.backbase.dto.RatingRequestDTO;
import nl.backbase.dto.source.MovieSourceDTO;
import nl.backbase.helper.ValueParserHelper;
import nl.backbase.helper.csv.CSVData;
import nl.backbase.mapper.MovieMappers;
import nl.backbase.mapper.RatingMappers;
import nl.backbase.model.MovieEntity;
import nl.backbase.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Executors;

/**
 * This {@link Service} is related in dealing with Movie operations. It is through it that the user can request the
 * available operations such as listing the Top 10 most rated movies, see if a given Movie won as Best Picture in Oscar
 * and also to rate a given Movie Title
 *
 * @author Daniel Chiuratto Seabra
 * @since 02/08/2022
 */
@Slf4j
@Service
@Transactional
public class MovieService {

    private final MovieRepository movieRepository;
    private final MovieSourceService movieSourceService;
    private final MovieMappers movieMappers;
    private final RatingMappers ratingMappers;
    private final String apiKey;

    public MovieService(final MovieRepository movieRepository,
                        final MovieSourceService movieSourceService,
                        final MovieMappers movieMappers,
                        final RatingMappers ratingMappers,
                        @Value("${omdbapi.api.key}") final String apiKey) {
        this.movieRepository = movieRepository;
        this.movieSourceService = movieSourceService;
        this.movieMappers = movieMappers;
        this.ratingMappers = ratingMappers;
        this.apiKey = apiKey;
    }

    /**
     * This method returns a {@link Collection<MovieTop10DTO>}> containing the 10 most rated Movies, sorted by Box Office
     * in descend order
     *
     * @return {@link Collection<MovieTop10DTO>} instance containing the 10 Movies and its ratings sorted by rating average
     * and Box Office in a descend order
     *
     * @author Daniel Chiuratto Seabra
     * @since 02/08/2022
     */
    public Collection<MovieTop10DTO> getMovieTop10DTOCollection() {
        final var top10Collection = this.movieRepository.findTop10OrderedByBoxOffice(Pageable.ofSize(10));
        return this.movieMappers.movieTop10EntityToMovieTop10DTO(top10Collection);
    }

    /**
     * This method saves the {@link RatingRequestDTO} instance, relating it in the given Movie. If the movie is not stored
     * in the database, then the Movie data is retrieved from the external Movie Source API and then stored into the database
     * with the respective rating
     *
     * @param ratingRequestDTO {@link RatingRequestDTO} instance containing the Movie Title to be rated as well the rate value
     * @return {@link RatingRequestDTO} instance with the Movie name updated since when the movie does not exist in the database
     * it is retrieve from the external Movie Source API that does a "like" query in its data source
     *
     * @author Daniel Chiuratto Seabra
     * @since 02/08/2022
     */
    public RatingRequestDTO saveRatingRequestDTO(final RatingRequestDTO ratingRequestDTO) {
        // Fist the Movie Title is searched in the database
        var movieEntity = this.movieRepository.findByTitleIgnoreCase(ratingRequestDTO.getMovieTitle());
        if (movieEntity == null) {
            // In case the Movie does not exist in the database, then it is requested in the external Movie Source API
            final var movieSourceDTO = this.movieSourceService.getMovieSourceDTO(this.apiKey, ratingRequestDTO.getMovieTitle());
            if (movieSourceDTO != null) {
                movieEntity = this.movieMappers.movieSourceDTOToMovieEntity(movieSourceDTO);
            } else {
                throw new MovieNotFoundException(String.format("The searched '%s' movie cannot be found", ratingRequestDTO.getMovieTitle()));
            }
        }

        // The logged username is retrieve, so it is used as a Rating Source
        final var authentication = SecurityContextHolder.getContext().getAuthentication();

        // The RatingRequestDTO then is mapped into RatingEntity to be able to be stored into the database
        final var ratingEntity = this.ratingMappers.ratingRequestDTORatingEntity(ratingRequestDTO, authentication, movieEntity);
        // The RatingEntity is added into the MovieEntity
        movieEntity.getRatings().add(ratingEntity);
        // Then finally the MovieEntity is saved relating it with the corresponding Rating
        this.movieRepository.save(movieEntity);

        return ratingRequestDTO;
    }

    /**
     * This method returns a {@link BestPictureMovieDTO} instance related with the requested Movie Title
     * @param movieTitle {@link String} representing the Movie Title to be searched in the database
     *
     * @return {@link BestPictureMovieDTO} instance representing the Oscar Best Picture winner movie
     *
     * @throws MovieNotFoundException thrown in case a Movie that did not earn as Best Picture in Oscar is requested
     *
     * @author Daniel Chiuratto Seabra
     * @since 02/08/2022
     */
    public BestPictureMovieDTO getBestPictureMovieDTO(final String movieTitle) throws MovieNotFoundException {
        var movieEntity = this.movieRepository.findByTitleIgnoreCase(movieTitle);
        if (movieEntity == null) {
            throw new MovieNotFoundException(movieTitle);
        }
        return this.movieMappers.movieEntityToBestPictureMovieDTO(movieEntity);
    }

    /**
     * The class below is just to attend the purpose of this assessment and exists only to fill the database with the
     * content available inside the CSV file sent together with the assessment description PDF. It is filtered by profile
     * since that during the Integration Tests they should not be loaded. It makes the test easier since who is going
     * to analyse the application won't have to horry about loading the database with the data inside of it
     *
     * @author Daniel Chiuratto Seabra
     * @since 06/08/2022
     */
    @Configuration
    @Profile("!test")
    private static class CSVFileLoader {
        private final MovieRepository movieRepository;
        private final MovieSourceService movieSourceService;
        private final MovieMappers movieMappers;
        private final String apiKey;

        @SuppressWarnings("unused") // It is used by Spring only, not manually instantiated
		public CSVFileLoader(final MovieRepository movieRepository,
                             final MovieSourceService movieSourceService,
                             final MovieMappers movieMappers,
                             @Value("${omdbapi.api.key}") final String apiKey) {

            this.movieRepository = movieRepository;
            this.movieSourceService = movieSourceService;
            this.movieMappers = movieMappers;
            this.apiKey = apiKey;
        }

        /**
         * This method should be run after this class be constructed automatically, where it is responsible in loading
         * the "academy_awards.csv" file content, parsing its content using the OpenCSV library into a {@link Collection<CSVData>},
         * and then the {@link Collection<CSVData>} needs to be saved in the database. The database loading is made in a
         * separate {@link Thread} so Spring startup does not get stuck while they get stored
         *
         * @author Daniel Chiuratto Seabra
         * @since 04/08/2022
         */
        @PostConstruct
        private void loadMoviesCSVContent() {
            // Here the CSV File content is loaded into the memory from the resource folder
            final var csvFileContentInputStream = this.getClass().getClassLoader().getResourceAsStream("academy_awards.csv");
            // The returned InputStream is loaded and converted into a Collection of CSVData
            final var csvCollection = ValueParserHelper.getCSVDataCollectionFromInputStream(csvFileContentInputStream);
            // Then the CSV Data collection database loading process is triggered but in a different Thread, so the main Thread that
            // is starting the Spring Boot does not get stuck waiting the loading to finish
            Executors.newSingleThreadExecutor().execute(() -> loadCSVDataIntoDatabase(csvCollection));
        }

        /**
         * This method receives a {@link Collection<CSVData>}, parses all the {@link CSVData} into {@link MovieEntity}
         * by requesting additional data of each movie from the Movie Source API, and then parsing the {@link MovieSourceDTO}
         * into {@link MovieEntity} to be ready for storing. All those movies are set as Best Picture Oscar Winner since they
         * came from the CSV File, already filtered by Best Picture, and by "Won?" column as "YES"
         *
         * @param csvCollection {@link Collection<CSVData>} instance containing all the parsed data loaded from the CSV file
         *
         * @author Daniel Chiuratto Seabra
         * @since 04/08/2022
         */
        private void loadCSVDataIntoDatabase(final Collection<CSVData> csvCollection) {
            try {
                log.info("LOADING the CSV File Content into the Database...");
                // First the Collection that will contain all the MovieEntities is instantiated
                final var movieEntityCollection = new ArrayList<MovieEntity>();
                // Now the Collection that contains the CSVData elements will be iterated
                csvCollection.forEach(csvMovie -> {
                    // First the corresponding movie is retrieved from the Movie Repository
                    var movieEntity = this.movieRepository.findByTitleIgnoreCase(csvMovie.getNominee());
                    // If the movie does not exist in the database then the external Movie Source API is used
                    if (movieEntity == null) {
                        // A request is made through the Movie Source Service, passing the API key and the Movie Title
                        final var movieSourceDTO = this.movieSourceService.getMovieSourceDTOFromCSVFile(this.apiKey, csvMovie.getNominee(), csvMovie.getAdditionalInfo());
                        // Then the representation of the external API that is MovieSourceDTO is parsed into MovieEntity to be able to be saved
                        // into the database
                        if (movieSourceDTO != null) {
                            movieEntity = this.movieMappers.movieSourceDTOToMovieEntity(movieSourceDTO);
                            // The entity has the oscarWinner property set to true, since that all movies that came from the CSV file
                            // were already filtered as Best Picture Oscar winners
                            movieEntity.setOscarWinner(true);
                            // Then the generated entity is added into the Movie Entity collection
                            movieEntityCollection.add(movieEntity);
                        } else {
                            // If the movie is not found in the Movie Source, then it is skipped
                            log.info("The movie {} cannot be found in the Movie Source API", csvMovie.getNominee());
                        }
                    }
                });

                // If the collection is not empty it means that it contains entities to be stored into the database
                if (!movieEntityCollection.isEmpty()) {
                    // All the collection is passed into the Movie Repository to be stored
                    log.info("STORING CSV Content into the Database...");
                    this.movieRepository.saveAll(movieEntityCollection);
                } else {
                    // Here is just to log in case the loaded Movies already exists in the database
                    log.info("NO CSV Content to be stored into the Database...");
                }

                log.info("FINISHED loading the CSV File Content into the Database...");
            } catch (final Exception e) {
                log.error("An error occurred while loading the file into the database, the process will be skipped", e);
            }
        }
    }
}
