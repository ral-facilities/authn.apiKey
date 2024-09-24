package org.icatproject.authn_apikey;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.persistence.EntityManager;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.icatproject.authentication.AuthnException;
import org.jboss.logging.Logger;

import java.net.HttpURLConnection;
import java.time.Instant;
import java.util.List;

@Path("/authn.apikey")
@ApplicationScoped
public class ApiKey_Authenticator {

	private static final Logger logger = Logger.getLogger(ApiKey_Authenticator.class);

	@Inject
	EntityManager manager;

	@Inject
	@ConfigProperty(name = "quarkus.application.version")
	String projectVersion;

	@GET
	@Path("version")
	@Produces(MediaType.APPLICATION_JSON)
	public String getVersion() {
		JsonObject versionJson = Json.createObjectBuilder()
				.add("version", projectVersion)
				.build();
		return versionJson.toString();
	}

	@POST
	@Path("authenticate")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String authenticate(@Context HttpHeaders httpHeaders, JsonObject jsonRequest) throws AuthnException {
		// Extract the Authorisation header
		String authorisationHeader = httpHeaders.getHeaderString(HttpHeaders.AUTHORIZATION);

		// Get the API key by stripping "Bearer" from the header
		if (authorisationHeader == null || !authorisationHeader.startsWith("Bearer ")) {
			throw new AuthnException(HttpURLConnection.HTTP_BAD_REQUEST, "Missing or invalid Authorization header");
		}
		String requestApiKey = authorisationHeader.substring("Bearer".length()).trim();

		// Extract the username from the JSON request body
		if (!jsonRequest.containsKey("user")) {
			throw new AuthnException(HttpURLConnection.HTTP_BAD_REQUEST, "Missing 'user' field in request");
		}
		String requestUsername = jsonRequest.getString("user");

		// Get the current Unix timestamp (seconds since epoch)
		long currentEpochTime = Instant.now().getEpochSecond();

		// This query does all the validation for us, it will only return a value if it finds
		// the requested user, with the request api key, with an expiry greater than today's unix date.
		// It may return more than one, which is fine.
		List<ApiKey> result = manager.createQuery("SELECT a FROM ApiKey a WHERE " +
						"a.userName = :username AND " +
						"a.key = :apiKey AND " +
						"a.expiry >= :currentEpochTime", ApiKey.class)
				.setParameter("username", requestUsername)
				.setParameter("apiKey", requestApiKey)
				.setParameter("currentEpochTime", currentEpochTime)
				.getResultList();

		if (result.isEmpty()) {
			logger.error("Unsuccessful request from " +requestUsername + " with key " + requestApiKey);
			throw new AuthnException(HttpURLConnection.HTTP_FORBIDDEN, "The username and key do not match, or the key has expired");
		}

		logger.info(requestUsername + " logged in successfully");

		return requestUsername + " logged in successfully";
	}
}
