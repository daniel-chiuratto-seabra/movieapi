package nl.backbase;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

/**
 * This is the Main class where the application is actually started, and the {@link UserDetailsServiceAutoConfiguration}
 * is being excluded from the Spring context, so it does not create default username and password to access the API,
 * something that needs to be handled by the {@code JWT} authentication method in a stateless way
 *
 * @author Daniel Chiuratto Seabra
 * @since 02/08/2022
 */
@SpringBootApplication(exclude = { UserDetailsServiceAutoConfiguration.class })
public class MovieApplication {

	/**
	 * This is the actual main method where the application is started
	 *
	 * ATTENTION: BEFORE RUNNING, PLEASE TAKE A LOOK AT THE "how_to_run.md" DOCUMENTATION FILE, SINCE THE OMDB API TOKEN
	 * NEEDS TO BE SET FIRST, OTHERWISE AN ERROR WILL BE THROWN DURING STARTUP
	 *
	 * @param args {@link String[]} instance containing args that can be passed when starting the application
	 *
	 * @author Daniel Chiuratto Seabra
	 * @since 02/08/2022
	 */
	public static void main(String[] args) {
		SpringApplication.run(MovieApplication.class, args);
	}
}
