package ch.hes.it.higiv.Model;

import android.support.annotation.NonNull;

public class Travel {

    @NonNull
    private String destination;
    @NonNull
    private String state;
    @NonNull
    private String idPlate;
    @NonNull
    private int numberOfPerson;
    @NonNull
    private String idUser;

    // geoloc start
    // geoloc end travel
    // date heure begin travel
    // date heure travel

    public Travel (){}

    public Travel(String destination, String state, String idPlate, int numberOfPerson, String idUser) {
        this.destination = destination;
        this.state = state;
        this.idPlate = idPlate;
        this.numberOfPerson = numberOfPerson;
        this.idUser = idUser;
    }


    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getIdPlate() {
        return idPlate;
    }

    public void setIdPlate(String idPlate) {
        this.idPlate = idPlate;
    }

    public int getNumberOfPerson() {
        return numberOfPerson;
    }

    public void setNumberOfPerson(int numberOfPerson) {
        this.numberOfPerson = numberOfPerson;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }


    public String toString() {
        return "Description: "  + destination +
                "State: "  + state +
                "Number plate: "  + idPlate +
                "Number of person: "  + numberOfPerson +
                "\n"+ "IdUser: " + idUser;
    }
}