package se325.assignment01.concert.service.services;

import se325.assignment01.concert.common.dto.BookingDTO;
import se325.assignment01.concert.common.dto.BookingRequestDTO;
import se325.assignment01.concert.service.domain.Booking;
import se325.assignment01.concert.service.domain.Concert;
import se325.assignment01.concert.service.domain.Seat;
import se325.assignment01.concert.service.domain.User;
import se325.assignment01.concert.service.mapper.BookingMapper;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.NoResultException;
import javax.ws.rs.*;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;


@Path("/concert-service/bookings")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BookingResource {


    private static final String COOKIE = "auth"; // used for authentication

    // get authenticated user
    private User getAuthUser(Cookie authCookie, EntityManager em) {
        if (authCookie == null) {
            return null;
        }
        User user = null;
        em.getTransaction().begin();
        try {
            user = em.createQuery("select u from User u where u.sessionId = :uuid", User.class)
                    .setParameter("uuid", UUID.fromString(authCookie.getValue()))
                    .setLockMode(LockModeType.OPTIMISTIC)
                    .getSingleResult();
        } catch (NoResultException e) {}
        em.getTransaction().commit();
        return user;
    }


    // get booking for authenticated user
    @POST
    public Response makeBooking(BookingRequestDTO request, @CookieParam(COOKIE) Cookie authCookie) {
        EntityManager em = PersistenceManager.instance().createEntityManager();
        try {
            User user = getAuthUser(authCookie, em);
            if (user == null) {

                Response.ResponseBuilder builder = Response.status(Response.Status.UNAUTHORIZED);
                return builder.build();
            }
            em.getTransaction().begin();
            Concert concert = em.find(Concert.class, request.getConcertId() , LockModeType.PESSIMISTIC_READ);
            if (concert == null || !concert.getDates().contains(request.getDate())) {
                Response.ResponseBuilder builder = Response.status(Response.Status.BAD_REQUEST);
                return builder.build();
            }
            em.getTransaction().commit();

            em.getTransaction().begin();
            List<Seat> unreserved = em.createQuery("select s from Seat s where s.label in :label and s.date = :date and s.isBooked = false", Seat.class)
                    .setParameter("label", request.getSeatLabels())
                    .setParameter("date", request.getDate())
                    .getResultList();

            if (unreserved.size() != request.getSeatLabels().size()) {

                Response.ResponseBuilder builder = Response.status(Response.Status.FORBIDDEN);
                return builder.build();
            }

            Booking booking = new Booking(user, request.getConcertId(), request.getDate());
            booking.getSeats().addAll(unreserved);
            unreserved.forEach(seat -> seat.setBooked(true));
            em.persist(booking);
            Response.ResponseBuilder builder = Response.created(URI.create("/concert-service/bookings/" + booking.getId()));
            return builder.build();
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().commit();
            }
        }
    }

    // get user booking
    @GET
    public Response getUserBookings(@CookieParam(COOKIE) Cookie authCookie) {
        EntityManager em = PersistenceManager.instance().createEntityManager();
        try {
            User user = getAuthUser(authCookie, em);
            if (user == null) {

                Response.ResponseBuilder builder = Response.status(Response.Status.UNAUTHORIZED);
                return builder.build();
            }
            // Get bookings and convert to DTOs
            em.getTransaction().begin();
            em.lock(user, LockModeType.OPTIMISTIC);
            Set<BookingDTO> dtoBookings = new HashSet<>();
            user.getBookings().forEach(booking -> dtoBookings.add(BookingMapper.toBookingDTO(booking)));
            em.getTransaction().commit();
            GenericEntity<Set<BookingDTO>> out = new GenericEntity<>(dtoBookings) {
            };
            Response.ResponseBuilder builder = Response.ok(out);
            return builder.build();
        } finally {
            em.close();
        }
    }

    // get booking for particular id
    @GET
    @Path("/{id}")
    public Response getBooking(@PathParam("id") long id, @CookieParam(COOKIE) Cookie authCookie) {
        EntityManager em = PersistenceManager.instance().createEntityManager();
        try {
            User user = getAuthUser(authCookie, em);
            if (user == null) {

                Response.ResponseBuilder builder = Response.status(Response.Status.UNAUTHORIZED);
                return builder.build();
            }
            em.getTransaction().begin();
            //find a booking
            Booking booking = em.find(Booking.class, id);

            if (!booking.getUser().equals(user)) {
                Response.ResponseBuilder builder = Response.status(Response.Status.FORBIDDEN);
                return builder.build();
            }
            Response.ResponseBuilder builder = Response.ok(BookingMapper.toBookingDTO(booking));
            return builder.build();
        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().commit();
            }
            em.close();
        }
    }

}
