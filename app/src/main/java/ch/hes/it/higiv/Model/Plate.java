
package ch.hes.it.higiv.Model;

import android.support.annotation.NonNull;

public class Plate {

    @NonNull
    private String number;


    private int noGoodEvaluation;
    private int noBadEvaluation;


    public Plate () {}

    public Plate(String number) {
        this.number = number;
    }


    public int getNoGoodEvaluation() {
        return noGoodEvaluation;
    }

    public void setNoGoodEvaluation(int noGoodEvaluation) {
        this.noGoodEvaluation = noGoodEvaluation;
    }

    public int getNoBadEvaluation() {
        return noBadEvaluation;
    }

    public void setNoBadEvaluation(int noBadEvaluation) {
        this.noBadEvaluation = noBadEvaluation;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }



    public String toString() {
        return "Number: "  + number;
    }
}

