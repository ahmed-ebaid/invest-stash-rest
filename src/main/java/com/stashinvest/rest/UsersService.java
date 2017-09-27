package com.stashinvest.rest;

import java.sql.SQLException;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;

import com.stashinvest.db.DBHelper;
import com.stashinvest.http.StatusCode;
import com.stashinvest.util.VerificationUtil;

//The following class serves as the UsersService RESTful End Points for adding and retrieving users
@Path("/v1")
public class UsersService {
	private static final Logger log = Logger.getLogger(UsersService.class);

	/**
	 * @param query
	 *            A string query that holds data relevant to email, full_name,
	 *            metadata;
	 * @return 200 status code for users that match the query, or if the query
	 *         is null return all users. This will return a 422 for an invalid
	 *         query and a 500 status codes for server issues.
	 */
	@GET
	@Path("/users")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUsers(@QueryParam("query") String query) {
		log.info("Retrieving Users from DB");
		try {
			DBHelper dbHelper = new DBHelper();
			if (query == null) {
				return Response.ok(dbHelper.getUsersByCreationTime(),
						MediaType.APPLICATION_JSON).build();
			} else {
				return !VerificationUtil.isValidQuery(query) ? Response
						.status(StatusCode.UNPROCESSED_ENTITY.code())
						.entity(StatusCode.UNPROCESSED_ENTITY.reason()).build()
						: Response.ok(dbHelper.getUsersFilteredByQuery(query),
								MediaType.APPLICATION_JSON).build();
			}
		} catch (SQLException e) {
			return Response.serverError()
					.entity("Can't establish a connection to the database")
					.build();
		}
	}

	/**
	 * @param user
	 *            User object
	 * @return 201 for a a created user, 422 for error creating user, 400 for
	 *         invalid params, 500 status codes for a server error
	 */
	@POST
	@Path("/users")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createUser(User user) {
		try {
			log.info("Creating user" + user);
			DBHelper dbHelper = new DBHelper();
			Users users = dbHelper.addUser(user);
			if (users == null) {
				// A user is null is a case for an error in some of the user
				// parameters
				DBErrors errors = new DBErrors();
				errors.setErrors(dbHelper.getErrorMessages());
				return Response.status(StatusCode.UNPROCESSED_ENTITY.code())
						.entity(errors).type(MediaType.APPLICATION_JSON)
						.build();
			} else {
				return Response.status(Status.CREATED).entity(users)
						.type(MediaType.APPLICATION_JSON).build();
			}
		} catch (SQLException e) {
			return Response.serverError()
					.entity("Can't establish a connection to the database")
					.build();
		}
	}
}