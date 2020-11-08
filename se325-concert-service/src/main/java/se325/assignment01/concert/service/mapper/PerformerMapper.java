package se325.assignment01.concert.service.mapper;

import se325.assignment01.concert.common.dto.PerformerDTO;
import se325.assignment01.concert.service.domain.Performer;

//helper class to convert between domain-model and DTO objects representing performers
public class PerformerMapper {

    private PerformerMapper(){}

    public static PerformerDTO toPerformerDTO(Performer p) {
        return new PerformerDTO(p.getId(),p.getName(),p.getImageName(),p.getGenre(),p.getBlurb());
    }
}

