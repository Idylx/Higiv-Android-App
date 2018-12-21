package ch.hes.it.higiv.Model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class Travel {

    @NonNull
    private String destination;
    @NonNull
    private String idPlate;
    @NonNull
    private int numberOfPerson;
    @Nullable
    private String filePath;
    @NonNull
    private String idUser;

    // geoloc start
    // geoloc end travel

    @NonNull
    private String timeBegin;
    private String timeEnd;
    private String badComment;

    public Travel (){}

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath( String filePath) {
        this.filePath = filePath;
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

    public String getTimeBegin() {
        return timeBegin;
    }

    public void setTimeBegin( String timeBegin) {
        this.timeBegin = timeBegin;
    }

    public String getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(String timeEnd) {
        this.timeEnd = timeEnd;
    }

    public String getBadComment() {
        return badComment;
    }

    public void setBadComment(String badComment) {
        this.badComment = badComment;
    }

    public String toString() {
        return "Description: "  + destination +
                "Number plate: "  + idPlate +
                "Number of person: "  + numberOfPerson +
                "\n"+ "IdUser: " + idUser+ "BadComment: " + badComment;
    }
}