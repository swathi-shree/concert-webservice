package se325.assignment01.concert.service.domain;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// booking has id,concert id, date of concert,seats for the concert and user
@Entity
@Table(name="BOOKINGS")
public class Booking {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;//primary key
    private long concertId;
    private LocalDateTime date;
    @OneToMany(cascade = CascadeType.PERSIST,fetch = FetchType.LAZY) // using LAZY if One to Many or Many to One collection
    @Fetch(FetchMode.SUBSELECT)
    private List<Seat> seats = new ArrayList<>();
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    public Booking(){}

    public Booking(User user,long concertId,LocalDateTime date){
        this.user = user;
        this.concertId = concertId;
        this.date=date;

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getConcertId() {
        return concertId;
    }

    public void setConcertId(long concertId) {
        this.concertId = concertId;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public List<Seat> getSeats() {
        return seats;
    }

    public void setSeats(List<Seat> seats) {
        this.seats = seats;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Booking))
            return false;
        if (obj == this)
            return true;

        Booking rhs = (Booking) obj;
        return new EqualsBuilder().
                append(concertId, rhs.concertId).append(date,rhs.date).append(user,rhs.user).
                isEquals();
    }


    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31).
                append(concertId).append(date).append(user).hashCode();
    }


}
