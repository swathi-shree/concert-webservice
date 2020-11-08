package se325.assignment01.concert.service.domain;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="CONCERTS")
public class Concert {

    @Id // primary key if not error in concert class
    @GeneratedValue(strategy = GenerationType.IDENTITY) //specifies how the primary key should be generated.
    private Long id;
    private String title;
    @Column(name = "IMAGE_NAME")
    private String imageName;
    @NotNull
    @Column(name ="BLURB",length=1000 )// use length to allow more than 255 characters to be displayed
    private String blurb;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "CONCERT_DATES",joinColumns = @JoinColumn(name = "CONCERT_ID"))
    @Column(name ="DATE")
    private Set<LocalDateTime> date = new HashSet<>(); // used over ArrayList to ensure no duplicates


    @ManyToMany(cascade =CascadeType.PERSIST,fetch = FetchType.EAGER)
    @JoinTable(name = "CONCERT_PERFORMER",joinColumns = @JoinColumn(name= "CONCERT_ID"),inverseJoinColumns = @JoinColumn(name = "PERFORMER_ID"))
    @Fetch(FetchMode.SUBSELECT)
    @Column(name = "PERFORMER_ID")
    private Set<Performer> performers = new HashSet<>();


    public Concert(){};

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageName() { return imageName; }

    public void setImageName(String imageName) { this.imageName = imageName; }

    public String getBlurb() { return blurb; }

    public void setBlurb(String blurb) { this.blurb = blurb; }

    public Set<LocalDateTime> getDates() {
        return date;
    }

    public void setDate(Set<LocalDateTime> date) {
        this.date = date;
    }

    public Set<Performer> getPerformers() { return performers; }

    public void setPerformer(Set<Performer>performers){this.performers = performers;}

    @Override
    public String toString() {
        return "Concert{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", imageName='" + imageName + '\'' +
                ", blurb='" + blurb + '\'' +
                ", date=" + date +
                ", performers=" + performers +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Concert))
            return false;
        if (obj == this)
            return true;

        Concert rhs = (Concert) obj;
        return new EqualsBuilder().
                append(title, rhs.title).
                isEquals();
    }
    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31).
                append(title).hashCode();
    }

}

