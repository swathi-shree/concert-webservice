package se325.assignment01.concert.service.services;


import se325.assignment01.concert.common.dto.PerformerDTO;
import se325.assignment01.concert.service.domain.Performer;
import se325.assignment01.concert.service.mapper.PerformerMapper;

import javax.persistence.EntityManager;
import javax.ws.rs.*;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Path("/concert-service/performers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PerformerResource {


    //get all performers stored in database
    @GET
    public Response getAllPerformers() {
        EntityManager em = PersistenceManager.instance().createEntityManager();
        try {
            List<Performer> performers = em.createQuery("select p from Performer p", Performer.class).getResultList();

            if (performers.isEmpty()) {
                Response.ResponseBuilder builder = Response.noContent();
                return builder.build();
                // return Response.noContent().build();
            }

            // convert each performer to dtoPerformers
            Set<PerformerDTO> dtoPerformers = new HashSet<>();
            performers.forEach(performer -> dtoPerformers.add(PerformerMapper.toPerformerDTO(performer)));

            GenericEntity<Set<PerformerDTO>> out = new GenericEntity<>(dtoPerformers) {
            };
            Response.ResponseBuilder builder = Response.ok(out);
            return builder.build();
          //  return Response.ok(out).build();
        } finally {
            em.close();
        }
    }

    //find a performer and return 404 if performer not found
    @GET
    @Path("/{id}")
    public Response getPerformer(@PathParam("id") long id) {
        EntityManager em = PersistenceManager.instance().createEntityManager();
        try {
            em.getTransaction().begin();
            Performer performer = em.find(Performer.class, id);
            if (performer == null) {
                throw new WebApplicationException(Response.Status.NOT_FOUND);
            }
            Response.ResponseBuilder builder = Response.ok(PerformerMapper.toPerformerDTO(performer));
            return builder.build();
            //return Response.ok(PerformerMapper.toPerformerDTO(performer)).build();
        } finally {
            em.close();
        }
    }



}
