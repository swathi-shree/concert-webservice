package se325.assignment01.concert.service.mapper;


import net.bytebuddy.build.Plugin;
import se325.assignment01.concert.common.dto.ConcertDTO;
import se325.assignment01.concert.common.dto.ConcertSummaryDTO;
import se325.assignment01.concert.service.domain.Concert;

// mapper class that maps between Concert domain and Concert DTO
public class ConcertMapper {

    private ConcertMapper(){}
    public static ConcertDTO toConcertDTO(Concert c){
        ConcertDTO dto = new ConcertDTO(c.getId(),c.getTitle(),c.getImageName(),c.getBlurb());
        c.getPerformers().forEach(performer -> dto.getPerformers().add(PerformerMapper.toPerformerDTO(performer)));
        dto.getDates().addAll(c.getDates());
        return dto;

    }

    public static ConcertSummaryDTO toConcertSummaryDTO(Concert c){
        return new ConcertSummaryDTO(c.getId(),c.getTitle(),c.getImageName());
    }
}


