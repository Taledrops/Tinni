package com.example.tinni.custom;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.example.tinni.R;
import com.example.tinni.databinding.BottomDailyRatingBinding;
import com.example.tinni.helpers.Constants;
import com.example.tinni.models.Rating;
import com.example.tinni.ui.home.HomeViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

/**
 * <h1>Bottom Dialog Daily Rating</h1>
 * BottomSheetDialogFragment for the daily rating ui
 *
 * Variables:
 * BottomDialogDailyRating dialog: The instance of the current dialog
 * HomeViewModel viewModel: The corresponding HomeViewModel
 * int rating: The current rating (1-5)
 *
 * Source: https://androidwave.com/bottom-sheet-dialog-fragment-in-android/
 *
 * @author Nassim Amar
 * @version 1.0
 * @since   08.07.2020
 */

public class BottomDialogDailyRating extends BottomSheetDialogFragment
{
    private HomeViewModel viewModel;
    private int rating;

    /**
     * <h2>New instance</h2>
     * Creates a new instance of the BottomDialogQuestions class
     *
     * @param _viewModel The corresponding HomeViewModel
     *
     */

    public void newInstance(HomeViewModel _viewModel, int _rating)
    {
        viewModel = _viewModel;
        rating = _rating;
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
        setStyle(STYLE_NORMAL, R.style.BottomDialog);
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
     * Handles the click on the save button
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
        BottomDailyRatingBinding binding = DataBindingUtil.inflate(inflater, R.layout.bottom_daily_rating, container, false);
        binding.setLifecycleOwner(this);
        binding.setVm(viewModel);

        Rating lastRating = Constants.getInstance().wasLastRatingToday();
        if (lastRating != null)
        {
            binding.text.setText(lastRating.getText());
        }

        binding.save.setOnClickListener(v -> save(binding.text.getText().toString()));

        return binding.getRoot();

    }

    /**
     * <h2>Save</h2>
     *
     * Save the text and update the ViewModel and SharedPreferences
     *
     */

    private void save (String text)
    {
        viewModel.setRating(rating, text);
        Toast.makeText(getContext(), getResources().getString(R.string.rating_saved), Toast.LENGTH_SHORT).show();
        dismiss();
    }
}
