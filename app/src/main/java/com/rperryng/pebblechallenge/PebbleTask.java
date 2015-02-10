package com.rperryng.pebblechallenge;

import android.os.AsyncTask;

import com.rperryng.pebblechallenge.models.AbsoluteColorCommand;
import com.rperryng.pebblechallenge.models.ColorCommand;
import com.rperryng.pebblechallenge.models.RelativeColorCommand;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class PebbleTask extends AsyncTask<Void, ColorCommand, Void> {

    // Create our own callback to pass parsed server responses back to the activity
    public interface OnServerMessagedReceivedListener {
        public void onServerMessageReceived(ColorCommand colorCommand);
    }

    // as specified in the instructions
    private static final int DEFAULT_PORT = 1234;

    // The relative color command is 56 bits, or 7 bytes long.  The absolute
    // color command is only 32 bits, or 4 bytes long.  Therefore the input stream
    // will never need to read more than 7 bytes at once.
    private static final int BUFFER_SIZE = 7;

    private String mServerIp;
    private OnServerMessagedReceivedListener mListener;

    public PebbleTask(String serverIp, OnServerMessagedReceivedListener listener) {
        mServerIp = serverIp;
        mListener = listener;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            Socket socket = new Socket(mServerIp, DEFAULT_PORT);
            InputStream inputStream = socket.getInputStream();
            byte[] buffer = new byte[BUFFER_SIZE];

            while (!isCancelled() && (inputStream.read(buffer) != -1)) {
                ColorCommand colorCommand = parseNetworkResponse(buffer);
                publishProgress(colorCommand);
            }

            inputStream.close();
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private ColorCommand parseNetworkResponse(byte[] buffer) {
        ColorCommand colorCommand;
        boolean isRelativeCommand = (buffer[0] == 1);

        if (isRelativeCommand) {
            colorCommand = new RelativeColorCommand(
                    bytesToShort(buffer[1], buffer[2]),
                    bytesToShort(buffer[3], buffer[4]),
                    bytesToShort(buffer[5], buffer[6])
            );
        } else {
            colorCommand = new AbsoluteColorCommand(buffer[1], buffer[2], buffer[3]);
        }
        return colorCommand;
    }

    private short bytesToShort(byte byteLeft, byte byteRight) {
        return (short) ((byteLeft << 8) | (byteRight & 0xFF));
    }

    @Override
    protected void onProgressUpdate(ColorCommand... values) {
        if (mListener != null) {
            ColorCommand colorCommand = values[0];
            mListener.onServerMessageReceived(colorCommand);
        }
    }
}
