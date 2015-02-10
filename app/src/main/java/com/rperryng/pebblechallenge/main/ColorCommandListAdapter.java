package com.rperryng.pebblechallenge.main;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rperryng.pebblechallenge.R;
import com.rperryng.pebblechallenge.models.AbsoluteColorCommand;
import com.rperryng.pebblechallenge.models.ColorCommand;
import com.rperryng.pebblechallenge.models.RelativeColorCommand;

import java.util.ArrayList;
import java.util.List;

public class ColorCommandListAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private List<ColorCommand> mColorCommandList;
    private List<Integer> mActivatedRelativeIndices;
    private int mActiveAbsoluteIndex;

    public ColorCommandListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        mColorCommandList = new ArrayList<>();
        mActivatedRelativeIndices = new ArrayList<>();
        mActiveAbsoluteIndex = 0;
    }

    private void deselectAll() {
        for (int activeIndex : mActivatedRelativeIndices) {
            getItem(activeIndex).setActive(false);
        }
        mActivatedRelativeIndices.clear();
        getItem(mActiveAbsoluteIndex).setActive(false);
    }

    public void add(ColorCommand colorCommand) {
        mColorCommandList.add(colorCommand);
        colorCommand.setActive(true);

        int newestIndex = mColorCommandList.size() - 1;
        if (colorCommand.getType() == ColorCommand.Type.ABSOLUTE) {
            deselectAll();
            mActiveAbsoluteIndex = newestIndex;
        } else {
            mActivatedRelativeIndices.add(newestIndex);
        }

        notifyDataSetChanged();
    }

    public void reset() {
        mColorCommandList.clear();
        mActivatedRelativeIndices.clear();
        mActiveAbsoluteIndex = 0;
        notifyDataSetChanged();
    }

    public void toggleRelativeColorCommand(int index) {
        ColorCommand colorCommand = getItem(index);
        boolean isActive = colorCommand.isActive();

        if (isActive) {
            mActivatedRelativeIndices.remove(new Integer(index));
        } else {
            mActivatedRelativeIndices.add(index);
        }
        colorCommand.setActive(!isActive);
    }

    public int setNewAbsolute(int index) {
        // deactivate the currently activate absolute color command
        getItem(mActiveAbsoluteIndex).setActive(false);
        mActiveAbsoluteIndex = index;

        // activate the new absolute color command and extract its color to operate on
        AbsoluteColorCommand newActiveAbsolute = (AbsoluteColorCommand) getItem(index);
        newActiveAbsolute.setActive(true);
        int newColor = newActiveAbsolute.getColor();

        // Reapply all the currently selected relative color commands
        for (int activeIndex : mActivatedRelativeIndices) {
            RelativeColorCommand relativeColorCommand = (RelativeColorCommand) getItem(activeIndex);
            newColor = relativeColorCommand.offsetColor(newColor);
        }

        notifyDataSetChanged();
        return newColor;
    }

    @Override
    public int getCount() {
        return mColorCommandList.size();
    }

    @Override
    public ColorCommand getItem(int position) {
        return mColorCommandList.get(position);
    }

    @Override
    public long getItemId(int position) {
        // No implementation.  Not used by framework.
        return 0;
    }

    private static class ViewHolder {
        public TextView textType;
        public TextView textCommand;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();

            convertView = mInflater.inflate(R.layout.listitem_colorcommand, null, false);
            viewHolder.textType = (TextView) convertView.findViewById(R.id.colorCommandListItem_textType);
            viewHolder.textCommand = (TextView) convertView.findViewById(R.id.colorCommandListItem_textCommand);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ColorCommand currentColorCommand = getItem(position);

        viewHolder.textType.setText(currentColorCommand.getType().toString());
        viewHolder.textCommand.setText(currentColorCommand.getCommandString());

        int backgroundColor = currentColorCommand.isActive()
                ? Color.rgb(155, 242, 138) // a light green I picked out
                : Color.WHITE;

        convertView.setBackgroundColor(backgroundColor);

        return convertView;
    }
}
