package ch.hes.it.higiv.Model;

import android.support.annotation.NonNull;

public class State {

    @NonNull
    private String name;

    public State () {}

    public State(String name) {
        this.name = name;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String toString() {
        return "Name: "  + name ;
    }
}