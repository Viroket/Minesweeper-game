package com.example.tal.minesweeper;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TileView extends LinearLayout {
    public TextView text;

    public TileView(Context context) {
        super(context);

        this.setOrientation(VERTICAL);

        text = new TextView(context);
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        text.setLayoutParams(layoutParams);

        text.setTextAlignment(TEXT_ALIGNMENT_CENTER);
        text.setGravity(Gravity.CENTER_VERTICAL);
        text.setTextSize(20);
        text.setTextColor(Color.BLACK);
        //setBackgroundColor(Color.GRAY);
        setBackgroundResource(R.drawable.emptybutton2);

        this.addView(text);

    }
}
