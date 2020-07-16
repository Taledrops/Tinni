package com.example.tinni.helpers;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tinni.R;

import java.util.Objects;

/**
 * <h1>Margin Decorator</h1>
 * Handling margins for items inside a RecyclerView
 *
 * Variables:
 * int space: the space between items
 * String type: custom String to determine on which side the margin should be
 * Functions func: Instance of the helper class "Functions"
 *
 * Source:
 * https://stackoverflow.com/a/27664023/2700965
 *
 * @author Nassim Amar
 * @version 1.0
 * @since   20.06.2020
 */

public class MarginDecorator extends RecyclerView.ItemDecoration
{
    private final int space;
    private final String type;
    private static final Functions func = new Functions();

    public MarginDecorator(String type, int space)
    {
        this.type = type;
        this.space = space;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, RecyclerView parent, @NonNull RecyclerView.State state)
    {
        if (parent.getAdapter() != null)
        {
            switch (type)
            {
                case "LeftLastRight":
                    outRect.left = space;
                    if (parent.getChildAdapterPosition(view) == parent.getAdapter().getItemCount() - 1)
                    {
                        outRect.right = space;
                    }
                    break;
                case "Bottom":
                    outRect.bottom = space;
                    break;
                case "FirstTop":
                    if (parent.getChildAdapterPosition(view) == 0)
                    {
                        outRect.top = space;
                    }
                    break;
                case "FirstAndLast":
                    if (parent.getChildAdapterPosition(view) == 0)
                    {
                        outRect.left = space;
                        outRect.right = func.pxFromDp(view.getContext(), view.getContext().getResources().getInteger(R.integer.small_margin));
                    }
                    else if (parent.getChildAdapterPosition(view) == Objects.requireNonNull(parent.getAdapter()).getItemCount() - 1)
                    {
                        outRect.right = space;
                    }
                    else
                    {
                        outRect.right = func.pxFromDp(view.getContext(), view.getContext().getResources().getInteger(R.integer.small_margin));
                    }
                    break;
                case "StaggeredGrid":
                    outRect.top = space;
                    int halfSpace = space / 2;
                    boolean even = parent.getChildAdapterPosition(view) % 2 == 0;
                    if (parent.getChildAdapterPosition(view) == 0 || even)
                    {
                        outRect.right = halfSpace;
                    }
                    else
                    {
                        outRect.left = halfSpace;
                    }
                    if (parent.getChildAdapterPosition(view) == Objects.requireNonNull(parent.getAdapter()).getItemCount() - 1)
                    {
                        outRect.bottom = space;
                    }
                    break;
            }
        }
    }
}
