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
import com.example.tinni.databinding.BottomNoteBinding;
import com.example.tinni.ui.sound.SoundViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Objects;

/**
 * <h1>Bottom Dialog Note</h1>
 * BottomSheetDialogFragment for the add note ui
 *
 * Variables:
 * SoundViewModel viewModel: The corresponding SoundViewModel
 * String oldText: Stores the old text of the note
 *
 * Source: https://androidwave.com/bottom-sheet-dialog-fragment-in-android/
 *
 * @author Nassim Amar
 * @version 1.0
 * @since   12.07.2020
 */

public class BottomDialogNote extends BottomSheetDialogFragment
{
    private SoundViewModel viewModel;
    private String oldText;

    /**
     * <h2>New instance</h2>
     * Creates a new instance of the BottomDialogQuestions class
     *
     * @param _viewModel The corresponding SoundViewModel
     *
     */

    public void newInstance(SoundViewModel _viewModel)
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
        if (oldText != null && oldText.length() > 0)
        {
            viewModel.note.set(oldText);
        }
        else
        {
            viewModel.note.set("");
        }
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
     * Handles the click on the button
     * Stores the oldText
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
        BottomNoteBinding binding = DataBindingUtil.inflate(inflater, R.layout.bottom_note, container, false);
        binding.setLifecycleOwner(this);
        binding.setVm(viewModel);

        binding.save.setOnClickListener(v -> save());

        if (viewModel.note.get() != null && Objects.requireNonNull(viewModel.note.get()).length() > 0)
        {
            oldText = viewModel.note.get();
        }

        return binding.getRoot();

    }

    /**
     * <h2>Save</h2>
     *
     * Saving the note
     *
     */

    private void save ()
    {
        if (viewModel.note.get() != null && Objects.requireNonNull(viewModel.note.get()).length() > 0)
        {
            viewModel.saveNote();
            Toast.makeText(getContext(), getResources().getString(R.string.note_saved), Toast.LENGTH_SHORT).show();
            dismiss();
        }
    }
}
