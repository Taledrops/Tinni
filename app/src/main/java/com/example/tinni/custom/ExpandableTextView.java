package com.example.tinni.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.core.content.ContextCompat;

import com.example.tinni.R;

/**
 * <h1>Expandable TextView</h1>
 * Custom TextView which can expand onClick if content exceeds maxLines
 *
 * Variables:
 * int maxLines: The threshold for showing the "show more" link
 * boolean canExpand: Indicator if TextView can be expanded
 * boolean expanded: Indicator if TextView is currently expanded
 * LinearLayout linearLayout: Instance of the current LinearLayout
 * TextView more: Instance of the "show more" TextView
 * TextView textView: Instance of the current TextView
 * Functions func: Instance of the Functions class
 *
 * @author Nassim Amar
 * @version 1.0
 * @since   25.06.2020
 */

@SuppressLint("AppCompatCustomView")
public class ExpandableTextView extends LinearLayout
{
    private static int maxLines = 6;
    private boolean canExpand = false;
    private boolean expanded = true;
    private final LinearLayout linearLayout = this;
    private TextView more;
    private TextView textView;

    public ExpandableTextView(Context context)
    {
        super(context);
    }

    /**
     * <h2>Constructor</h2>
     * Constructor with onClickListener
     */

    public ExpandableTextView(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);

        setOnClickListener(v ->
        {
            if (canExpand)
            {
                if (expanded)
                {
                    more.setText(getContext().getResources().getString(R.string.showMore));
                    textView.setMaxLines(maxLines);
                }
                else
                {
                    more.setText(getContext().getResources().getString(R.string.showLess));
                    textView.setMaxLines(Integer.MAX_VALUE);
                }
                expanded = !expanded;
            }
        });
    }

    /**
     * <h2>Init</h2>
     * Initialize the ExpandableTextView
     *
     * @param text the text of the TextView
     */

    public void Init (String text)
    {
        if (text != null)
        {
            removeAllViews();

            linearLayout.setOrientation(VERTICAL);
            linearLayout.setClickable(true);
            linearLayout.setFocusable(true);

            linearLayout.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.primary_dark_ripple_no_padding));

            textView = new TextView(new ContextThemeWrapper(getContext(), R.style.SubTextWide));
            maxLines = getResources().getInteger(R.integer.text_lines);

            textView.setText(text.replace("\\n", "\n"));
            textView.setEllipsize(TextUtils.TruncateAt.END);
            textView.setMaxLines(maxLines);
            linearLayout.addView(textView);

            more = new TextView(new ContextThemeWrapper(getContext(), R.style.SmallText));
            more.setText(getContext().getResources().getString(R.string.showMore));

            int h1 = (int) (getResources().getDimension(R.dimen.mediumMargin));

            textView.post(() ->
            {
                int count = textView.getLineCount();
                if (count >= maxLines)
                {
                    LayoutParams mlp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    mlp.topMargin = h1;
                    more.setLayoutParams(mlp);
                    linearLayout.addView(more);
                    canExpand = true;
                    expanded = false;
                }
            });
        }
    }
}
