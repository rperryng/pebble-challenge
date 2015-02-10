package com.rperryng.pebblechallenge.models;

import android.graphics.Color;

public class AbsoluteColorCommand extends ColorCommand {

    // Save the values as bytes to save memory
    // Note that there are no unsigned values in java, so in order to get
    // the unsigned value we convert the number to an Integer (any arithmetic with
    // lower level type automatically turns the resultant into integer).
    private byte r;
    private byte g;
    private byte b;

    public AbsoluteColorCommand(byte r, byte g, byte b) {
        super(ColorCommand.Type.ABSOLUTE);

        this.r = r;
        this.g = g;
        this.b = b;
    }

    public int getR() {
        return byteToInteger(r);
    }

    public int getG() {
        return byteToInteger(g);
    }

    public int getB() {
        return byteToInteger(b);
    }

    private static int byteToInteger(byte b) {
        return b & 0xFF;
    }

    public int getColor() {
        return Color.rgb(getR(), getG(), getB());
    }

    @Override
    public String getCommandString() {
        return String.format(
                "set R: %d, set G: %d, set B: %d",
                getR(),
                getG(),
                getB()
        );
    }
}
