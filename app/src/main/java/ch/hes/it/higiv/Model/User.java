package ch.hes.it.higiv.Model;

public class User {

    
    private String firstname;
    private String lastname;
    private String gender;
    private String emergencyPhone;

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmergencyPhone(){return emergencyPhone;}

    public void setEmergencyPhone(String emergencyPhone){this.emergencyPhone = emergencyPhone;}

}
