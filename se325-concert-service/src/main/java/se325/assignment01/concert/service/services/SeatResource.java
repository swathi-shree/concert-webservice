package se325.assignment01.concert.service.services;

import se325.assignment01.concert.common.dto.SeatDTO;
import se325.assignment01.concert.common.types.BookingStatus;
import se325.assignment01.concert.service.domain.Seat;
import se325.assignment01.concert.service.jaxrs.LocalDateTimeParam;
import se325.assignment01.concert.service.mapper.SeatMapper;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.*;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Path("/concert-service/seats")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SeatResource {
    // get all available seats for a particular date
    @GET
    @Path("/{date}")
    public Response getSeat(@PathParam("date") LocalDateTimeParam dateTime, @QueryParam("status") BookingStatus status) {
        LocalDateTime date = dateTime.getLocalDateTime();
        EntityManager em = PersistenceManager.instance().createEntityManager();
        try {
            em.getTransaction().begin();
            boolean seatAvailable = em.createQuery("select count(s) from Seat s where s.date = :date", Long.class)
                    .setParameter("date", dateTime.getLocalDateTime())
                    .getSingleResult()
                    .intValue() > 0;
            if(!seatAvailable) {
                Response.ResponseBuilder builder = Response.status(Response.Status.BAD_REQUEST);
                return builder.build();


               // return Response.status(Response.Status.BAD_REQUEST).build();
            }
            em.getTransaction().commit();

            em.getTransaction().begin();
            TypedQuery<Seat> selectQuery;
            if (status == BookingStatus.Any) {
                selectQuery = em.createQuery("select s from Seat s where s.date = :date", Seat.class)
                        .setParameter("date", date);
            } else {
                selectQuery = em.createQuery("select s from Seat s where s.date = :date and s.isBooked = :status", Seat.class)
                        .setParameter("date", date)
                        .setParameter("status", status == BookingStatus.Booked);
            }
            List<Seat> seats = selectQuery.getResultList();

            // Get seats and convert to DTOs
            Set<SeatDTO> dtoSeats = new HashSet<>();
            seats.forEach(seat -> dtoSeats.add(SeatMapper.toSeatDTO(seat)));

            em.getTransaction().commit();

            GenericEntity<Set<SeatDTO>> out = new GenericEntity<>(dtoSeats) {
            };
            Response.ResponseBuilder builder = Response.ok(out);
            return builder.build();
            //return Response.ok(out).build();

        } finally {
            if (em.getTransaction().isActive()) {
                em.getTransaction().commit();
            }
            em.close();
        }
    }


}
