package org.icatproject.authn_apikey;

import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.RestAssured;
import jakarta.ws.rs.core.Response;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

@QuarkusIntegrationTest
public class AuthenticateIT {

	@Test
	public void testValidLoginUser() {
		String jsonString = "{\"user\":\"app2\"}";

		RestAssured.given()
				.header("Authorization", "Bearer 1159bfc57922c1708b63e31c04589f4b33155c5b24327bcb5b7b25859c84e399")
				.header("Content-Type", "application/json")
				.body(jsonString)  // Send the username in the request body
				.when()
				.post("/authn.apikey/authenticate")
				.then()
				.statusCode(Response.Status.OK.getStatusCode())
				.body(Matchers.equalTo("app2 logged in successfully"));
	}

	@Test
	public void testInvalidUsernameValidKey() {
		String jsonString = "{\"user\":\"invaliduser\"}";

		RestAssured.given()
				.header("Authorization", "Bearer 1159bfc57922c1708b63e31c04589f4b33155c5b24327bcb5b7b25859c84e399")
				.header("Content-Type", "application/json")
				.body(jsonString)
				.when()
				.post("/authn.apikey/authenticate")
				.then()
				.statusCode(Response.Status.FORBIDDEN.getStatusCode())
				.body("message", Matchers.equalTo("The username and key do not match, or the key has expired"));
	}

	@Test
	public void testInvalidApiKey() {
		String jsonString = "{\"user\":\"app2\"}";

		RestAssured.given()
				.header("Authorization", "Bearer 666")
				.header("Content-Type", "application/json")
				.body(jsonString)
				.when()
				.post("/authn.apikey/authenticate")
				.then()
				.statusCode(Response.Status.FORBIDDEN.getStatusCode())
				.body("message", Matchers.equalTo("The username and key do not match, or the key has expired"));
	}

	@Test
	public void testExpiredApiKey() {
		String jsonString = "{\"user\":\"app2\"}";

		RestAssured.given()
				.header("Authorization", "Bearer 0059bfc57922c1708b63e31c04589f4b33155c5b24327bcb5b7b25859c84e399")
				.header("Content-Type", "application/json")
				.body(jsonString)
				.when()
				.post("/authn.apikey/authenticate")
				.then()
				.statusCode(Response.Status.FORBIDDEN.getStatusCode())
				.body("message", Matchers.equalTo("The username and key do not match, or the key has expired"));
	}

	@Test
	public void testNoKey() {
		String jsonString = "{\"user\":\"app2\"}";

		RestAssured.given()
				.header("Content-Type", "application/json")
				.body(jsonString)
				.when()
				.post("/authn.apikey/authenticate")
				.then()
				.statusCode(Response.Status.BAD_REQUEST.getStatusCode())
				.body("message", Matchers.equalTo("Missing or invalid Authorization header"));
	}

	@Test
	public void testIncorrectUserTag() {
		String jsonString = "{\"wrong\":\"wrong\"}";

		RestAssured.given()
				.header("Authorization", "Bearer 1159bfc57922c1708b63e31c04589f4b33155c5b24327bcb5b7b25859c84e399")
				.header("Content-Type", "application/json")
				.body(jsonString)
				.when()
				.post("/authn.apikey/authenticate")
				.then()
				.statusCode(Response.Status.BAD_REQUEST.getStatusCode())
				.body("message", Matchers.equalTo("Missing 'user' field in request"));
	}
}
