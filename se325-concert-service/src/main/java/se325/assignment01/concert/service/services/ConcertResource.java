package se325.assignment01.concert.service.services;


import se325.assignment01.concert.common.dto.ConcertDTO;
import se325.assignment01.concert.common.dto.ConcertSummaryDTO;
import se325.assignment01.concert.service.domain.Concert;
import se325.assignment01.concert.service.mapper.ConcertMapper;
import javax.persistence.EntityManager;
import javax.ws.rs.*;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Path("/concert-service")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ConcertResource {
    // get all concerts information
    @GET
    @Path("/concerts")
    public Response getAllConcerts() {
        EntityManager em = PersistenceManager.instance().createEntityManager();
        try {
            List<Concert> concerts = em.createQuery("select c from Concert c", Concert.class).getResultList();
            if (concerts.isEmpty()) {
                Response.ResponseBuilder builder = Response.noContent();
                return builder.build();
            }
            // for each concert,convert to DTO
            Set<ConcertDTO> dtoConcerts = new HashSet<>();
            concerts.forEach(concert -> dtoConcerts.add(ConcertMapper.toConcertDTO(concert)));
            GenericEntity<Set<ConcertDTO>> out = new GenericEntity<>(dtoConcerts) {
            };
            Response.ResponseBuilder builder = Response.ok(out);
            return builder.build();
        } finally {
            em.close();
        }
    }

    // get a concert using id
    @GET
    @Path("/concerts/{id}")
    public Response getConcert(@PathParam("id") long id) {
        EntityManager em = PersistenceManager.instance().createEntityManager();
        try {
            em.getTransaction().begin();
            // find a concert
            Concert concert = em.find(Concert.class, id);
            em.getTransaction().commit();
            if (concert == null) {
                throw new WebApplicationException(Response.Status.NOT_FOUND);
            }
            Response.ResponseBuilder builder = Response.ok(ConcertMapper.toConcertDTO(concert));
            return builder.build();
        } finally {
            em.close();
        }
    }

    // get all concert summaries
    @GET
    @Path("/concerts/summaries")
    public Response getSummaries() {
        EntityManager em = PersistenceManager.instance().createEntityManager();
        try {
            em.getTransaction().begin();
            List<Concert> concerts = em.createQuery("select c from Concert c", Concert.class).getResultList();

            if (concerts.isEmpty()) {
                Response.ResponseBuilder builder = Response.noContent();
                return builder.build();
            }

            // convert to dtoConcertSummaries
            Set<ConcertSummaryDTO> dtoConcertSummaries = new HashSet<>();
            concerts.forEach(concert -> dtoConcertSummaries.add(ConcertMapper.toConcertSummaryDTO(concert)));
            em.getTransaction().commit();

            GenericEntity<Set<ConcertSummaryDTO>> out = new GenericEntity<>(dtoConcertSummaries) {
            };
            Response.ResponseBuilder builder = Response.ok(out);
            return builder.build();
        } finally {
            em.close();
        }
    }
}