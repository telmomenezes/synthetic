package com.telmomenezes.synthetic.motifs;

import com.telmomenezes.synthetic.emd.Feature;

public class FeatureTriadic implements Feature {
    private int triad;
    
    public FeatureTriadic(int triad) {
        this.triad = triad;
    }
    
    public double groundDist(Feature f) {
        int triad1 = triad;
        int triad2 = ((FeatureTriadic)f).triad;
        
        if (triad1 == triad2) {
            return 0;
        }
        
        
        if (triad1 > triad2) {
            int aux = triad2;
            triad2 = triad1;
            triad1 = aux;
        }
        
        switch (triad1) {
        case 1:
            switch (triad2) {
            case 2:
                return 2;
            case 3:
                return 2;
            case 4:
                return 3;
            case 5:
                return 1;
            case 6:
                return 2;
            case 7:
                return 1;
            case 8:
                return 3;
            case 9:
                return 2;
            case 10:
                return 2;
            case 11:
                return 2;
            case 12:
                return 3;
            case 13:
                return 4;
            }
        case 2:
            switch (triad2) {
            case 3:
                return 2;
            case 4:
                return 1;
            case 5:
                return 3;
            case 6:
                return 2;
            case 7:
                return 1;
            case 8:
                return 3;
            case 9:
                return 2;
            case 10:
                return 2;
            case 11:
                return 2;
            case 12:
                return 3;
            case 13:
                return 4;
            }
        case 3:
            switch (triad2) {
            case 4:
                return 1;
            case 5:
                return 1;
            case 6:
                return 2;
            case 7:
                return 1;
            case 8:
                return 1;
            case 9:
                return 2;
            case 10:
                return 2;
            case 11:
                return 2;
            case 12:
                return 3;
            case 13:
                return 4;
            }
        case 4:
            switch (triad2) {
            case 5:
                return 2;
            case 6:
                return 1;
            case 7:
                return 2;
            case 8:
                return 2;
            case 9:
                return 1;
            case 10:
                return 3;
            case 11:
                return 1;
            case 12:
                return 2;
            case 13:
                return 3;
            }
        case 5:
            switch (triad2) {
            case 6:
                return 1;
            case 7:
                return 2;
            case 8:
                return 2;
            case 9:
                return 3;
            case 10:
                return 1;
            case 11:
                return 1;
            case 12:
                return 2;
            case 13:
                return 3;
            }
        case 6:
            switch (triad2) {
            case 7:
                return 3;
            case 8:
                return 3;
            case 9:
                return 2;
            case 10:
                return 2;
            case 11:
                return 2;
            case 12:
                return 1;
            case 13:
                return 2;
            }
        case 7:
            switch (triad2) {
            case 8:
                return 2;
            case 9:
                return 1;
            case 10:
                return 1;
            case 11:
                return 1;
            case 12:
                return 2;
            case 13:
                return 3;
            }
        case 8:
            switch (triad2) {
            case 9:
                return 3;
            case 10:
                return 3;
            case 11:
                return 1;
            case 12:
                return 2;
            case 13:
                return 3;
            }
        case 9:
            switch (triad2) {
            case 10:
                return 4;
            case 11:
                return 2;
            case 12:
                return 1;
            case 13:
                return 2;
            }
        case 10:
            switch (triad2) {
            case 11:
                return 2;
            case 12:
                return 1;
            case 13:
                return 2;
            }
        case 11:
            switch (triad2) {
            case 12:
                return 1;
            case 13:
                return 2;
            }
        case 12:
            switch (triad2) {
            case 13:
                return 1;
            }
        }
        
        // shouldn't happen
        return 0;
    }

}
