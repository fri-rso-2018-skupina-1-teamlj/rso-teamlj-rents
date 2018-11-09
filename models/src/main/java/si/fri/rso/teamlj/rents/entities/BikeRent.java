package si.fri.rso.teamlj.rents.entities;

import si.fri.rso.teamlj.rents.dtos.Bike;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;

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

    private String latitudeOfRent;

    private String longitudeOfRent;

    private String latitudeOfReturn;

    private String longitudeOfReturn;

    private Instant dateOfRent;

    private Instant dateOfReturn;

    @Column(name = "bike_id")
    private Integer bikeId;

    @Column(name = "user_id")
    private Integer userId;

    @Transient
    private List<Bike> bikes;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLatitudeOfRent() {
        return latitudeOfRent;
    }

    public void setLatitudeOfRent(String latitudeOfRent) {
        this.latitudeOfRent = latitudeOfRent;
    }

    public String getLongitudeOfRent() {
        return longitudeOfRent;
    }

    public void setLongitudeOfRent(String longitudeOfRent) {
        this.longitudeOfRent = longitudeOfRent;
    }

    public String getLatitudeOfReturn() {
        return latitudeOfReturn;
    }

    public void setLatitudeOfReturn(String latitudeOfReturn) {
        this.latitudeOfReturn = latitudeOfReturn;
    }

    public String getLongitudeOfReturn() {
        return longitudeOfReturn;
    }

    public void setLongitudeOfReturn(String longitudeOfReturn) {
        this.longitudeOfReturn = longitudeOfReturn;
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

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getBikeId() {
        return bikeId;
    }

    public void setBikeId(Integer bikeId) {
        this.bikeId = bikeId;
    }

    public List<Bike> getBikes() {
        return bikes;
    }

    public void setBikes(List<Bike> bikes) {
        this.bikes = bikes;
    }
}
