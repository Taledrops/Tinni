package com.example.tinni.custom;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.example.tinni.R;
import com.example.tinni.databinding.BottomNextBinding;
import com.example.tinni.helpers.CircleTransform;
import com.example.tinni.helpers.Constants;
import com.example.tinni.ui.program.ProgramViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

/**
 * <h1>Bottom Dialog Next</h1>
 * BottomSheetDialogFragment for the next ui
 *
 * Variables:
 * ProgramViewModel viewModel: The corresponding ProgramViewModel
 *
 * Source: https://androidwave.com/bottom-sheet-dialog-fragment-in-android/
 *
 * @author Nassim Amar
 * @version 1.0
 * @since   02.07.2020
 */

public class BottomDialogNext extends BottomSheetDialogFragment
{
    private ProgramViewModel viewModel;

    /**
     * <h2>New instance</h2>
     * Creates a new instance of the BottomDialogQuestions class
     *
     * @param _viewModel The corresponding ProgramViewModel
     *
     */

    public void newInstance(ProgramViewModel _viewModel)
    {
        viewModel = _viewModel;
    }

    /**
     * <h2>On Cancel</h2>
     * Override
     * Called when the dialog gets closed
     *
     * @param dialog The DialogInterface instance
     *
     */

    @Override
    public void onCancel(@NonNull DialogInterface dialog)
    {
        super.onCancel(dialog);
    }

    /**
     * <h2>On Create</h2>
     * Override
     * Called when the dialog gets created
     *
     * @param savedInstanceState The saved Bundle
     *
     */

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    /**
     * <h2>On Activity Created</h2>
     * Override
     * Called when the activity gets created
     * Sets the height of dialog to full screen height
     *
     * @param savedInstanceState The saved Bundle
     *
     */

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        if (getView() != null)
        {
            View parent = (View) getView().getParent();
            BottomSheetBehavior<?> bottomSheetBehavior = BottomSheetBehavior.from(parent);
            bottomSheetBehavior.setPeekHeight(getResources().getDisplayMetrics().heightPixels);
        }
    }

    /**
     * <h2>On Create View</h2>
     * Override
     * Called when the View gets created
     * Connects the ViewModel to the layout
     * Sets up the next session
     * Creates a click handler to create a new calendar entry
     *
     * Source: https://stackoverflow.com/a/14695308/2700965
     *
     * @param inflater The LayoutInflater
     * @param container The ViewGroup
     * @param savedInstanceState The saved Bundle
     *
     */

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        BottomNextBinding binding = DataBindingUtil.inflate(inflater, R.layout.bottom_next, container, false);
        binding.setLifecycleOwner(this);
        binding.setVm(viewModel);

        if (viewModel.nextSession != null && viewModel.nextSession.getSound() != null)
        {
            if (Constants.getInstance().picasso != null && viewModel.nextSession.getSound().getImg() > 0)
            {
                Constants.getInstance().picasso.load(viewModel.nextSession.getSound().getImg()).fit().transform(new CircleTransform()).centerCrop().into(binding.sessionImg);
            }
            binding.sessionTitle.setText(viewModel.nextSession.getSound().getTitle());

            if (viewModel.current.getValue() != null)
            {
                int interval = viewModel.current.getValue().getInterval();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(calendar.getTime());
                calendar.add(Calendar.DAY_OF_YEAR, interval);
                SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                binding.sessionDate.setText(format.format(calendar.getTime()));

                binding.calendar.setOnClickListener(v ->
                {
                    Intent intent = new Intent(Intent.ACTION_EDIT);
                    intent.setType("vnd.android.cursor.sound_item_horizontal/event");
                    String title = viewModel.nextSession.getSound().getTitle();
                    if (viewModel.active.get() != null)
                    {
                        title = String.format(getResources().getString(R.string.session_x_of_y), Objects.requireNonNull(viewModel.active.get()).sessions.size(), viewModel.current.getValue().getTitle());
                    }
                    intent.putExtra(CalendarContract.Events.TITLE, title);
                    intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                            calendar.getTimeInMillis());
                    intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
                            calendar.getTimeInMillis() + (viewModel.nextSession.getTime() * 1000));
                    intent.putExtra(CalendarContract.Events.ALL_DAY, false);
                    startActivity(intent);
                });
            }
            else
            {
                binding.sessionDate.setVisibility(View.GONE);
            }
        }
        else
        {
            binding.sessionContainer.setVisibility(View.GONE);
        }

        binding.close.setOnClickListener(v -> dismiss());

        return binding.getRoot();

    }
}
