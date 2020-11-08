package se325.assignment01.concert.service.services;

import se325.assignment01.concert.common.dto.UserDTO;
import se325.assignment01.concert.service.domain.User;
import se325.assignment01.concert.service.mapper.BookingMapper;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.NoResultException;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.util.UUID;

@Path("/concert-service/login")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LoginResource {


    private static final String COOKIE = "auth"; // used for authentication

    // create sessionId/cookie and save in User table
    private NewCookie createSession(User user, EntityManager em) {
        em.getTransaction().begin();
        user.setSessionId(UUID.randomUUID());
        em.getTransaction().commit();
        NewCookie sessionCookie = new NewCookie(COOKIE, user.getSessionId().toString());
        return sessionCookie;
    }

    @POST
    public Response authenticateUser(UserDTO credential) {
        EntityManager em = PersistenceManager.instance().createEntityManager();
        try {
            User user;
            em.getTransaction().begin();
            try {
                user = em.createQuery("select u from User u where u.username = :username and u.password = :password", User.class)
                        .setParameter("username", credential.getUsername())
                        .setParameter("password", credential.getPassword())
                        .setLockMode(LockModeType.OPTIMISTIC)
                        .getSingleResult();
            } catch (NoResultException e) {
                Response.ResponseBuilder builder = Response.status(Response.Status.UNAUTHORIZED);
                return builder.build();
            } finally {
                em.getTransaction().commit();
            }
            Response.ResponseBuilder builder = Response.ok().cookie(createSession(user,em));
            return builder.build();
        } finally {
            em.close();
        }
    }

}
