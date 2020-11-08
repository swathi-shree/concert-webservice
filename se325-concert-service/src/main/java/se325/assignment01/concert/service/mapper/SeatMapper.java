package se325.assignment01.concert.service.mapper;

import se325.assignment01.concert.common.dto.SeatDTO;
import se325.assignment01.concert.service.domain.Seat;

//helper class to convert between domain-model and DTO objects representing seats.
public class SeatMapper {

    private SeatMapper(){}
    public static SeatDTO toSeatDTO(Seat s){
        return new SeatDTO(s.getLabel(),s.getPrice());
    }

}
