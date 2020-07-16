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
import com.example.tinni.models.Question;
import com.example.tinni.ui.program.ProgramViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Objects;

/**
 * <h1>Bottom Dialog Questions</h1>
 * BottomSheetDialogFragment for the questions ui
 *
 * Variables:
 * ProgramViewModel viewModel: The corresponding ProgramViewModel
 * boolean start: Indicator if this is the starting or ending questionnaire
 * FragmentManager fragmentManager: The current FragmentManager
 *
 * Source: https://androidwave.com/bottom-sheet-dialog-fragment-in-android/
 *
 * @author Nassim Amar
 * @version 1.0
 * @since   30.06.2020
 */

public class BottomDialogQuestions extends BottomSheetDialogFragment
{
    private ProgramViewModel viewModel;
    private boolean start;
    private FragmentManager fragmentManager;

    /**
     * <h2>New instance</h2>
     * Creates a new instance of the BottomDialogQuestions class
     *
     * @param _viewModel The corresponding ProgramViewModel
     * @param _start Indicator if this is the starting questionnaire
     * @param _fragmentManager: The current FragmentManager
     *
     */

    public void newInstance(ProgramViewModel _viewModel, boolean _start, FragmentManager _fragmentManager)
    {
        viewModel = _viewModel;
        start = _start;
        fragmentManager = _fragmentManager;
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
        com.example.tinni.databinding.BottomQuestionsBinding binding = DataBindingUtil.inflate(inflater, R.layout.bottom_questions, container, false);
        binding.setLifecycleOwner(this);
        binding.setVm(viewModel);

        if (start)
        {
            binding.text.setText(getResources().getString(R.string.questionnaire_sub));
        }
        else
        {
            binding.text.setText(getResources().getString(R.string.questionnaire_end_sub));
        }

        if (viewModel.current.getValue() != null && Objects.requireNonNull(viewModel.current.getValue()).getQuestions().size() > 0)
        {
            binding.save.setOnClickListener(v -> save());

            binding.questions.setItemViewCacheSize(20);
            binding.questions.setDrawingCacheEnabled(true);
            binding.questions.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
            binding.questions.setLayoutManager(layoutManager);

            QuestionAdapter questionAdapter = new QuestionAdapter(viewModel.current.getValue().getQuestions());
            binding.questions.setAdapter(questionAdapter);
        }

        return binding.getRoot();

    }

    /**
     * <h2>Save</h2>
     * Saves the current questionnaire
     *
     */

    private void save()
    {
        if (getDialog() != null && viewModel.current.getValue() != null && Objects.requireNonNull(viewModel.current.getValue()).getQuestions() != null)
        {
            int ok = 0;
            for (Question q: Objects.requireNonNull(viewModel.current.getValue()).getQuestions())
            {
                if (q.answers.stream().anyMatch(o -> o.active.get()))
                {
                    ok ++;
                }
            }

            if (ok >= Objects.requireNonNull(viewModel.current.getValue()).getQuestions().size())
            {
                viewModel.activate(start);
                getDialog().dismiss();
                if (!start)
                {
                    BottomDialogFinish bottomDialogFinish = new BottomDialogFinish();
                    bottomDialogFinish.newInstance(viewModel);
                    bottomDialogFinish.show(fragmentManager, "finish");
                }
            }
            else
            {
                Toast.makeText(getContext(), getResources().getString(R.string.error_questions), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
