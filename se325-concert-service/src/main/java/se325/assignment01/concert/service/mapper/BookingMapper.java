package se325.assignment01.concert.service.mapper;

import se325.assignment01.concert.common.dto.BookingDTO;
import se325.assignment01.concert.common.dto.SeatDTO;
import se325.assignment01.concert.service.domain.Booking;

import java.util.ArrayList;
import java.util.List;


//helper class to convert between domain-model and DTO objects representing bookings.
public class BookingMapper {
    private BookingMapper(){}
    public static BookingDTO toBookingDTO(Booking b){
        List<SeatDTO> seats = new ArrayList<>();
        b.getSeats().forEach(seat->seats.add(SeatMapper.toSeatDTO(seat)));
        return new BookingDTO(b.getId(),b.getDate(),seats);
    }


}



