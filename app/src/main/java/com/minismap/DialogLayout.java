package com.minismap;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by nbp184 on 2016/03/18.
 */
public class DialogLayout extends LinearLayout {

    private TextView title;

    public DialogLayout(Context context) {
        super(context);
        init(null, 0);
    }

    public DialogLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public DialogLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    public void init(AttributeSet attrs, int defStyleAttr) {
        if(getOrientation() == HORIZONTAL) {
            setOrientation(VERTICAL);
        }

        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.DialogLayout, defStyleAttr, 0);
        String name = a.getString(R.styleable.DialogLayout_dialogTitle);
        if(name == null) {
            name = "Dialog";
        }
        a.recycle();

        title = new TextView(getContext());
        int colour;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            title.setTextAppearance(android.R.style.TextAppearance_Large);
            colour = getResources().getColor(R.color.colorAccent, null);
        } else {
            title.setTextAppearance(getContext(), android.R.style.TextAppearance_Large);
            colour = getResources().getColor(R.color.colorAccent);
        }
        title.setTextColor(colour);
        title.setText(name);
        int padding = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics());
        title.setPadding(padding, padding, padding, padding);
        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        title.setLayoutParams(lp);
        addView(title);

        View v = new View(getContext());
        lp = new LayoutParams(LayoutParams.MATCH_PARENT, (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()));
        v.setLayoutParams(lp);
        v.setBackgroundColor(colour);
        addView(v);
    }

    public void setTitle(String title) {
        this.title.setText(title);
    }

    public void setTitle(int resid) {
        title.setText(resid);
    }

}
