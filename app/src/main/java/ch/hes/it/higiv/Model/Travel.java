package ch.hes.it.higiv.Model;

import android.support.annotation.NonNull;

public class Travel {

    @NonNull
    private String destination;
    @NonNull
    private String state;
    @NonNull
    private int numberPlate;
    @NonNull
    private int numberOfPerson;
    @NonNull
    private String idUser;

    public Travel (){}

    public Travel(String destination, String state, int numberPlate, int numberOfPerson, String idUser) {
        this.destination = destination;
        this.state = state;
        this.numberPlate = numberPlate;
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

    public int getNumberPlate() {
        return numberPlate;
    }

    public void setNumberPlate(int numberPlate) {
        this.numberPlate = numberPlate;
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
                "Number plate: "  + numberPlate +
                "Number of person: "  + numberOfPerson +
                "\n"+ "IdUser: " + idUser;
    }
}