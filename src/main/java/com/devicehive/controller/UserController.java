package com.devicehive.controller;

import com.devicehive.auth.HiveRoles;
import com.devicehive.dao.UserDAO;
import com.devicehive.json.strategies.JsonPolicyApply;
import com.devicehive.json.strategies.JsonPolicyDef;
import com.devicehive.model.ErrorResponse;
import com.devicehive.model.Network;
import com.devicehive.model.User;
import com.devicehive.model.request.UserRequest;
import com.devicehive.model.response.UserNetworkResponse;
import com.devicehive.model.response.UserResponse;
import com.devicehive.service.UserService;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * TODO JavaDoc
 */
@Path("/user")
public class UserController {

    @Inject
    private UserService userService;
    @Inject
    private UserDAO userDAO;


    /**
     * This method will generate following output
     *
     * <code>
     *  [
     *  {
     *  "id": 2,
     *  "login": "login",
     *  "role": 0,
     *  "status": 0,
     *  "lastLogin": "1970-01-01 03:00:00.0"
     *  },
     *  {
     *  "id": 3,
     *  "login": "login1",
     *  "role": 1,
     *  "status": 2,
     *  "lastLogin": "1970-01-01 03:00:00.0"
     *  }
     *]
     *</code>
     *
     * @param login user login ignored, when loginPattern is specified
     * @param loginPattern login pattern (LIKE %VALUE%) user login will be ignored, if not null
     * @param role User's role ADMIN - 0, CLIENT - 1
     * @param status ACTIVE - 0 (normal state, user can logon) , LOCKED_OUT - 1 (locked for multiple login failures), DISABLED - 2 , DELETED - 3;
     * @param sortField    either of "login", "loginAttempts", "role", "status", "lastLogin"
     * @param sortOrder either ASC or DESC
     * @param take like SQL LIMIT
     * @param skip like SQL OFFSET
     * @return List of User
     */
    @GET
    @RolesAllowed(HiveRoles.ADMIN)
    public Response getUsersList(@QueryParam("login") String login,
                                 @QueryParam("loginPattern") String loginPattern,
                                 @QueryParam("role") Integer role,
                                 @QueryParam("status") Integer status,
                                 @QueryParam("sortField") String sortField,
                                 @QueryParam("sortOrder") String sortOrder,
                                 @QueryParam("take") Integer take,
                                 @QueryParam("skip") Integer skip) {

        boolean sortOrderAsc = true;

        if (sortOrder != null && !sortOrder.equalsIgnoreCase("DESC") && !sortOrder.equalsIgnoreCase("ASC")) {
            return ResponseFactory.response(Response.Status.BAD_REQUEST, new ErrorResponse("Invalid request parameters."));
        }
        if ("DESC".equalsIgnoreCase(sortOrder)) {
            sortOrderAsc = false;
        }
        if (!"ID".equalsIgnoreCase(sortField) && !"Login".equalsIgnoreCase(sortField) && sortField != null) {
            return ResponseFactory.response(Response.Status.BAD_REQUEST, new ErrorResponse("Invalid request parameters."));
        }

        //TODO validation for role and status
        List<User> result = userDAO.getList(login, loginPattern, role, status, sortField, sortOrderAsc, take, skip);

        return ResponseFactory.response(Response.Status.OK, result, JsonPolicyDef.Policy.USERS_LISTED);
    }

    /**
     * Method will generate following output:
     *
     *<code>
     *{
     *     "id": 2,
     *     "login": "login",
     *     "status": 0,
     *     "networks": [
     *     {
     *          "network": {
     *              "id": 5,
     *              "key": "network key",
     *              "name": "name of network",
     *              "description": "short description of network"
     *          }
     *     }
     *     ],
     *     "lastLogin": "1970-01-01 03:00:00.0"
     *}
     *</code>
     *
     * If success, response with status 200, if user is not found 400
     * @param id user id
     * @return
     */
    @GET
    @Path("/{id}")
    @RolesAllowed(HiveRoles.ADMIN)
    public Response getUser(@PathParam("id") long id) {

        User user = userDAO.findUserWithNetworks(id);

        if (user == null) {
            return ResponseFactory.response(Response.Status.NOT_FOUND, new ErrorResponse("User not found."));
        }

        return ResponseFactory.response(Response.Status.OK,
                UserResponse.createFromUser(user),
                JsonPolicyDef.Policy.USER_PUBLISHED);
    }

    /**
     * One needs to provide user resource in request body (all parameters are mandatory):
     *
     * <code>
     * {
     *     "login":"login"
     *     "role":0
     *     "status":0
     *     "password":"qwerty"
     * }
     * </code>
     *
     * In case of success server will provide following response with code 201
     *
     * <code>
     *     {
     *         "id": 1,
     *         "lastLogin": null
     *     }
     * </code>
     *
     * @return Empty body, status 201 if success, 403 if forbidden, 400 otherwise
     */
    @POST
    @RolesAllowed(HiveRoles.ADMIN)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response insertUser(UserRequest user) {

        //neither we want left some params omitted
        if (user.getLogin() == null || user.getPassword() == null || user.getRole() == null
                || user.getStatus() == null) {
            return ResponseFactory.response(Response.Status.BAD_REQUEST, new ErrorResponse("Invalid request parameters."));
        }
        //nor we want these parameters to be null
        if (user.getLogin().getValue() == null || user.getPassword().getValue() == null
                || user.getRole().getValue() == null || user.getStatus().getValue() == null) {
            return ResponseFactory.response(Response.Status.BAD_REQUEST, new ErrorResponse("Invalid request parameters."));
        }

        if (userService.findByLogin(user.getLogin().getValue()) != null) {
            return ResponseFactory.response(Response.Status.FORBIDDEN, new ErrorResponse("User couldn't be created."));
        }

        User created = userService.createUser(user.getLogin().getValue(),
                                              user.getRoleEnum(),
                                              user.getStatusEnum(),
                                              user.getPassword().getValue());

        return ResponseFactory.response(Response.Status.CREATED, created, JsonPolicyDef.Policy.USERS_LISTED);
    }


    /**
     * Updates user. One should specify following json to update user (none of parameters are mandatory, bot neither of them can be null):
     *
     * <code>
     * {
     *   "login": "login",
     *   "role": 0,
     *   "status": 0,
     *   "password": "password"
     * }
     * </code>
     *
     * role:  Administrator - 0, Client - 1
     * status: ACTIVE - 0 (normal state, user can logon) , LOCKED_OUT - 1 (locked for multiple login failures), DISABLED - 2 , DELETED - 3;
     * @param userId - id of user beign edited
     * @return empty response, status 201 if succeeded, 403 if action is forbidden, 400 otherwise
     */
    @PUT
    @Path("/{id}")
    @RolesAllowed(HiveRoles.ADMIN)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateUser(UserRequest user, @PathParam("id") long userId) {

        if (user.getLogin() != null) {
            User u = userService.findByLogin(user.getLogin().getValue());

            if (u != null && u.getId() != userId) {
                return ResponseFactory.response(Response.Status.FORBIDDEN, new ErrorResponse("User couldn't be updated."));
            }
        }

        if (user.getLogin() != null && user.getLogin().getValue() == null) {
            return ResponseFactory.response(Response.Status.BAD_REQUEST, new ErrorResponse("Invalid request parameters."));
        }

        if (user.getPassword() != null && user.getPassword().getValue() == null) {
            return ResponseFactory.response(Response.Status.BAD_REQUEST, new ErrorResponse("Invalid request parameters."));
        }

        if (user.getRole() != null && user.getRole().getValue() == null) {
            return ResponseFactory.response(Response.Status.BAD_REQUEST, new ErrorResponse("Invalid request parameters."));
        }

        if (user.getStatus() != null && user.getStatus().getValue() == null) {
            return ResponseFactory.response(Response.Status.BAD_REQUEST, new ErrorResponse("Invalid request parameters."));
        }

        String loginValue = user.getLogin() == null ? null : user.getLogin().getValue();
        String passwordValue = user.getPassword() == null ? null : user.getPassword().getValue();

        userService.updateUser(userId, loginValue, user.getRoleEnum(), user.getStatusEnum(), passwordValue);

        return ResponseFactory.response(Response.Status.CREATED);
    }


    /**
     * Deletes user by id
     * @param userId id of user to delete
     * @return empty response. state 204 in case of success, 404 if not found
     */
    @DELETE
    @Path("/{id}")
    @RolesAllowed(HiveRoles.ADMIN)
    public Response deleteUser(@PathParam("id") long userId) {

        userService.deleteUser(userId);

        return ResponseFactory.response(Response.Status.NO_CONTENT);
    }

    /**
     * Method returns following body in case of success (status 200):
     *<code>
     *     {
     *       "id": 5,
     *       "key": "network_key",
     *       "name": "network name",
     *       "description": "short description of net"
     *     }
     *</code>
     *in case, there is no such network, or user, or user doesn't have access
     *
     * @param id user id
     * @param networkId network id
     */
    @GET
    @Path("/{id}/network/{networkId}")
    @RolesAllowed(HiveRoles.ADMIN)
    public Response getNetwork(@PathParam("id") long id, @PathParam("networkId") long networkId) {

        User existingUser = userDAO.findUserWithNetworks(id);
        if (existingUser == null) {
            throw new NotFoundException("User not found.");
        }

        for (Network network : existingUser.getNetworks()) {
            if (network.getId() == networkId) {
                return ResponseFactory.response(Response.Status.OK,
                                                UserNetworkResponse.fromNetwork(network),
                                                JsonPolicyDef.Policy.NETWORKS_LISTED);
            }
        }

        throw new NotFoundException("User network not found.");
    }

    /**
     * Request body must be empty. Returns Empty body.
     * @param id user id
     * @param networkId network id
     */
    @PUT
    @Path("/{id}/network/{networkId}")
    @RolesAllowed(HiveRoles.ADMIN)
    public Response assignNetwork(@PathParam("id") long id, @PathParam("networkId") long networkId) {

        try {
            userService.assignNetwork(id, networkId);
        } catch (NotFoundException e) {
            throw new NotFoundException("User network not found.");
        }

        return ResponseFactory.response(Response.Status.CREATED);
    }

    /**
     *   Removes user permissions on network
     * @param id user id
     * @param networkId network id
     * @return Empty body. Status 204 in case of success, 404 otherwise
     */
    @DELETE
    @Path("/{id}/network/{networkId}")
    @RolesAllowed(HiveRoles.ADMIN)
    public Response unassignNetwork(@PathParam("id") long id, @PathParam("networkId") long networkId) {

        userService.unassignNetwork(id, networkId);

        return ResponseFactory.response(Response.Status.NO_CONTENT);
    }


    /**
     * Returns current user with networks:
     *<code>
     *{
     *     "id": 2,
     *     "login": "login",
     *     "status": 0,
     *     "networks": [
     *     {
     *          "network": {
     *              "id": 5,
     *              "key": "network key",
     *              "name": "network name",
     *              "description": "short description of network"
     *          }
     *     }
     *     ],
     *     "lastLogin": "1970-01-01 03:00:00.0"
     *}
     *</code>
     *
     * Or empty body and 403 status in case of user is not logged on
     *
     */
    @GET
    @Path("/current")
    @PermitAll
    public Response getCurrent(@Context ContainerRequestContext requestContext) {

        String login = requestContext.getSecurityContext().getUserPrincipal().getName();

        if (login == null) {
            return ResponseFactory.response(Response.Status.FORBIDDEN, new ErrorResponse("Couldn't get current user."));
        }

        User result = userService.findUserWithNetworksByLogin(login);

        return ResponseFactory.response(Response.Status.OK, result, JsonPolicyDef.Policy.USER_PUBLISHED);
    }

    /**
     * Updates user currently logged on.
     * One should specify following json to update user (none of parameters are mandatory, bot neither of them can be null):
     *
     * <code>
     * {
     *   "login": "login",
     *   "role": 0,
     *   "status": 0,
     *   "password": "password"
     * }
     * </code>
     *
     * role:  Administrator - 0, Client - 1
     * status: ACTIVE - 0 (normal state, user can logon) , LOCKED_OUT - 1 (locked for multiple login failures), DISABLED - 2 , DELETED - 3;
     * @return empty response, status 201 if succeeded, 403 if action is forbidden, 400 otherwise
     */
    @PUT
    @Path("/current")
    @PermitAll
    @Consumes(MediaType.APPLICATION_JSON)
    @JsonPolicyApply(JsonPolicyDef.Policy.USERS_LISTED)
    public Response updateCurrent(UserRequest ui, @Context ContainerRequestContext requestContext) {

        String password = ui.getPassword().getValue();

        if (password == null) {
            return ResponseFactory.response(Response.Status.BAD_REQUEST, new ErrorResponse("Invalid request parameters."));
        }

        String login = requestContext.getSecurityContext().getUserPrincipal().getName();

        if (login == null) {
            return ResponseFactory.response(Response.Status.FORBIDDEN, new ErrorResponse("User couldn't be updated"));
        }

        User u = userService.findUserWithNetworksByLogin(login);

        userService.updatePassword(u.getId(), password);

        return ResponseFactory.response(Response.Status.CREATED);
    }


}
