package com.stashinvest.rest;

import java.io.IOException;
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

import com.stashinvest.jdbc.DBHelper;

@Path("/v1")
public class UsersService {

	@GET
	@Path("/users")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUsers(@QueryParam("query") String query) {
		Logger.getLogger(getClass()).info("Getting Users");
		try {
			DBHelper dbHelper = new DBHelper();
			if (query == null) {
				return Response.ok(dbHelper.getUsersByCreationTime(),
						MediaType.APPLICATION_JSON).build();
			} else {
				if (!dbHelper.isValidQuery(query)) {
					return Response
							.status(StatusCode.UNPROCESSED_ENTITY.code())
							.entity(StatusCode.UNPROCESSED_ENTITY.reason() + "")
							.build();
				} else {
					return Response.ok(dbHelper.getUsersFilteredByQuery(query),
							MediaType.APPLICATION_JSON).build();
				}
			}
		} catch (SQLException e) {
			return Response.serverError()
					.entity("Can't establish a database connection").build();
		}
	}

	@POST
	@Path("/users")
	// @consumes defines methods that resource class can accept
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createUser(User user) throws IOException {
		try {
			DBHelper dbHelper = new DBHelper();
			Users users = dbHelper.addUser(user);
			if (users != null) {
				return Response.status(Status.CREATED).entity(users).type(MediaType.APPLICATION_JSON).build();
			} else {
				return null;
			}
		} catch (SQLException e) {
			return Response.serverError()
					.entity("Can't establish a database connection").build();
		}
	}
}