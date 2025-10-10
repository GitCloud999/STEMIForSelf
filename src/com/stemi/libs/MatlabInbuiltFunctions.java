package com.stemi.libs;

public class MatlabInbuiltFunctions {

    // Finds the absolute of a complex no.
    public double absolute(double real, double imag){
        return Math.sqrt(real*real + (imag*imag));
    }

    public double absolute(int real, int imag){
        return Math.sqrt(real*real + (imag*imag));
    }


}
