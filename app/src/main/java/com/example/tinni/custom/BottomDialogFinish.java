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
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.tinni.R;
import com.example.tinni.adapters.QuestionAdapter;
import com.example.tinni.databinding.BottomFinishBinding;
import com.example.tinni.databinding.BottomRatingBinding;
import com.example.tinni.models.Answer;
import com.example.tinni.models.Question;
import com.example.tinni.models.Session;
import com.example.tinni.ui.program.ProgramViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * <h1>Bottom Dialog Finish</h1>
 * BottomSheetDialogFragment for the finish ui
 *
 * Variables:
 * BottomDialogQuestions dialog: The instance of the current dialog
 * ProgramViewModel viewModel: The corresponding ProgramViewModel
 * Session session: The current session
 * FragmentManager fragmentManager: The current FragmentManager
 * List<Question> questions: The list of questions
 *
 * Source: https://androidwave.com/bottom-sheet-dialog-fragment-in-android/
 *
 * @author Nassim Amar
 * @version 1.0
 * @since   02.07.2020
 */

public class BottomDialogFinish extends BottomSheetDialogFragment
{
    private BottomDialogFinish dialog;
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
        dialog = new BottomDialogFinish();
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
            bottomSheetBehavior.setPeekHeight((int)(getResources().getDisplayMetrics().heightPixels));
        }
    }

    /**
     * <h2>On Create View</h2>
     * Override
     * Called when the View gets created
     * Connects the ViewModel to the layout
     * Fill the RecylerView with questions
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
        BottomFinishBinding binding = DataBindingUtil.inflate(inflater, R.layout.bottom_finish, container, false);
        binding.setLifecycleOwner(this);
        binding.setVm(viewModel);

        binding.close.setOnClickListener(v -> dismiss());

        return binding.getRoot();

    }
}
