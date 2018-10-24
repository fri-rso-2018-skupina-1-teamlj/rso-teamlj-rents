package si.fri.rso.teamlj.rents.entities;

import javax.persistence.*;
import java.time.Instant;

@Entity(name = "rents")
@NamedQueries(value =
        {
                @NamedQuery(name = "BikeRent.getAll", query = "SELECT r FROM rents r"),
                @NamedQuery(name = "BikeRent.findByUser", query = "SELECT r FROM rents r WHERE r.userId = " +
                        ":userId")
        })
public class BikeRent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String locationOfRent;

    private String locationOfReturn;

    private Instant dateOfRent;

    private Instant dateOfReturn;

    @Column(name = "bike_id")
    private String bikeId;

    @Column(name = "user_id")
    private String userId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLocationOfRent() {
        return locationOfRent;
    }

    public void setLocationOfRent(String locationOfRent) {
        this.locationOfRent = locationOfRent;
    }

    public String getLocationOfReturn() {
        return locationOfReturn;
    }

    public void setLocationOfReturn(String locationOfReturn) {
        this.locationOfReturn = locationOfReturn;
    }

    public Instant getDateOfRent() {
        return dateOfRent;
    }

    public void setDateOfRent(Instant dateOfRent) {
        this.dateOfRent = dateOfRent;
    }

    public Instant getDateOfReturn() {
        return dateOfReturn;
    }

    public void setDateOfReturn(Instant dateOfReturn) {
        this.dateOfReturn = dateOfReturn;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getBikeId() {
        return bikeId;
    }

    public void setBikeId(String bikeId) {
        this.bikeId = bikeId;
    }
}
