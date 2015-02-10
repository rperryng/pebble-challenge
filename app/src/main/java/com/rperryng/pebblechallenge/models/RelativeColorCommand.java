package com.rperryng.pebblechallenge.models;

import android.graphics.Color;

public class RelativeColorCommand extends ColorCommand {

    private short dr;
    private short dg;
    private short db;

    public RelativeColorCommand(short dr, short dg, short db) {
        super(ColorCommand.Type.RELATIVE);

        this.dr = dr;
        this.dg = dg;
        this.db = db;
    }

    public short getOffsetR() {
        return dr;
    }

    public short getOffsetG() {
        return dg;
    }

    public short getOffsetB() {
        return db;
    }

    public int offsetColor(int color) {
        return Color.rgb(
                Color.red(color) + getOffsetR(),
                Color.green(color) + getOffsetG(),
                Color.blue(color) + getOffsetB()
        );
    }

    public int reverseOffsetColor(int color) {
        return Color.rgb(
                Color.red(color) - getOffsetR(),
                Color.green(color) - getOffsetG(),
                Color.blue(color) - getOffsetB()
        );
    }

    @Override
    public String getCommandString() {
        return String.format(
                "offset R: %d, offset G: %d,offset B: %d",
                getOffsetR(),
                getOffsetG(),
                getOffsetB()
        );
    }
}
