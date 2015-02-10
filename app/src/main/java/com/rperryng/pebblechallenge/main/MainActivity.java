package com.rperryng.pebblechallenge.main;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.rperryng.pebblechallenge.PebbleTask;
import com.rperryng.pebblechallenge.R;
import com.rperryng.pebblechallenge.models.AbsoluteColorCommand;
import com.rperryng.pebblechallenge.models.ColorCommand;
import com.rperryng.pebblechallenge.models.RelativeColorCommand;


public class MainActivity extends ActionBarActivity implements
        View.OnClickListener,
        AdapterView.OnItemClickListener,
        PebbleTask.OnServerMessagedReceivedListener {

    private static final String NAME_SHARED_PREFERENCES = "mySharedPreferences";
    private static final String KEY_SP_SERVER_IP = "keySpServerIp";
    private static final String KEY_CURRENT_COLOR = "keyCurrentColor";
    private static final int DEFAULT_COLOR = Color.rgb(127, 127, 127);

    private Button mButtonConnect;
    private Button mButtonReset;
    private ColorCommandListAdapter mListAdapter;
    private TextView mTextColor;
    private View mViewColorBlock;

    private PebbleTask mPebbleTask;
    private String mServerIp;
    private int mCurrentColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        mTextColor = (TextView) findViewById(R.id.main_text_color);
        mViewColorBlock = findViewById(R.id.main_view_colorBlock);

        mButtonConnect = (Button) findViewById(R.id.main_button_connect);
        mButtonReset = (Button) findViewById(R.id.main_button_reset);
        mButtonConnect.setOnClickListener(this);
        mButtonReset.setOnClickListener(this);

        ListView listColorCommands = (ListView) findViewById(R.id.main_list_colorCommands);
        mListAdapter = new ColorCommandListAdapter(this);
        listColorCommands.setOnItemClickListener(this);
        listColorCommands.setAdapter(mListAdapter);

        EditText serverIpEditText = (EditText) findViewById(R.id.main_editText_serverIp);
        serverIpEditText.addTextChangedListener(new ServerIpTextWatcher());
        mServerIp = getSharedPreferences(NAME_SHARED_PREFERENCES, Context.MODE_PRIVATE)
                .getString(KEY_SP_SERVER_IP, "");
        serverIpEditText.setText(mServerIp);

        if (savedInstanceState == null) {
            mCurrentColor = DEFAULT_COLOR;
        } else {
            mCurrentColor = savedInstanceState.getInt(KEY_CURRENT_COLOR);
        }

        updateTextAndColor();
    }

    @Override
    protected void onPause() {
        super.onPause();

        getSharedPreferences(NAME_SHARED_PREFERENCES, Context.MODE_PRIVATE)
                .edit()
                .putString(KEY_SP_SERVER_IP, mServerIp)
                .apply();

        if (mPebbleTask != null && (mPebbleTask.getStatus() == AsyncTask.Status.RUNNING)) {
            mPebbleTask.cancel(true);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_CURRENT_COLOR, mCurrentColor);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.main_button_connect:
                if (mPebbleTask != null && (mPebbleTask.getStatus() == AsyncTask.Status.RUNNING)) {
                    Toast.makeText(this, "Server connection already live", Toast.LENGTH_SHORT).show();
                    return;
                }

                mPebbleTask = new PebbleTask(mServerIp, this);
                mPebbleTask.execute();
                break;

            case R.id.main_button_reset:
                mListAdapter.reset();
                mCurrentColor = DEFAULT_COLOR;
                updateTextAndColor();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ColorCommand colorCommand = mListAdapter.getItem(position);

        if (colorCommand.getType() == ColorCommand.Type.ABSOLUTE) {

            // Restrict disabling the currently active absolute color command
            if (colorCommand.isActive()) {
                return;
            }

            mCurrentColor = mListAdapter.setNewAbsolute(position);
        } else {
            mListAdapter.toggleRelativeColorCommand(position);

            RelativeColorCommand relativeColorCommand = (RelativeColorCommand) colorCommand;
            mCurrentColor = relativeColorCommand.isActive()
                    ? relativeColorCommand.offsetColor(mCurrentColor)
                    : relativeColorCommand.reverseOffsetColor(mCurrentColor);
        }

        mListAdapter.notifyDataSetChanged();
        updateTextAndColor();
    }

    @Override
    public void onServerMessageReceived(final ColorCommand colorCommand) {
        if (colorCommand.getType() == ColorCommand.Type.RELATIVE) {
            RelativeColorCommand relativeColorCommand = (RelativeColorCommand) colorCommand;
            mCurrentColor = relativeColorCommand.offsetColor(mCurrentColor);

        } else {
            AbsoluteColorCommand absoluteColorCommand = (AbsoluteColorCommand) colorCommand;
            mCurrentColor = absoluteColorCommand.getColor();
        }

        mListAdapter.add(colorCommand);
        mListAdapter.notifyDataSetChanged();
        updateTextAndColor();
    }

    private class ServerIpTextWatcher implements TextWatcher {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            mServerIp = s.toString();
            mButtonConnect.setEnabled(!mServerIp.isEmpty());
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // No Implementation
        }

        @Override
        public void afterTextChanged(Editable s) {
            // No implementation
        }
    }

    private void updateTextAndColor() {
        mTextColor.setText(String.format(
                "R: %d, G: %d, B: %d",
                Color.red(mCurrentColor),
                Color.green(mCurrentColor),
                Color.blue(mCurrentColor)
        ));
        mViewColorBlock.setBackgroundColor(mCurrentColor);
    }
}
