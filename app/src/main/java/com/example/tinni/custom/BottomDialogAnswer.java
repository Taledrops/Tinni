package com.example.tinni.custom;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.example.tinni.R;
import com.example.tinni.adapters.AddAnswersAdapter;
import com.example.tinni.adapters.AddQuestionsAdapter;
import com.example.tinni.databinding.BottomAnswerBinding;
import com.example.tinni.databinding.BottomQuestionBinding;
import com.example.tinni.models.Answer;
import com.example.tinni.models.Question;
import com.example.tinni.ui.add.AddProgram;
import com.example.tinni.ui.add.AddProgramViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.Objects;

/**
 * <h1>Bottom Dialog Answer</h1>
 * BottomSheetDialogFragment for the answer ui
 *
 * Variables:
 * BottomDialogQuestion dialog: The instance of the current dialog
 * ProgramViewModel viewModel: The corresponding ProgramViewModel
 * Session session: The current session
 * FragmentManager fragmentManager: The current FragmentManager
 * List<Question> questions: The list of questions
 *
 * Source 1: https://androidwave.com/bottom-sheet-dialog-fragment-in-android/
 * Source 2: https://stackoverflow.com/a/9598729/2700965
 *
 * @author Nassim Amar
 * @version 1.0
 * @since   06.07.2020
 */

public class BottomDialogAnswer extends BottomSheetDialogFragment
{
    private BottomDialogAnswer dialog;
    private AddAnswersAdapter addAnswersAdapter;
    private BottomDialogQuestion bottomDialogQuestion;
    private AddProgramViewModel viewModel;

    /**
     * <h2>New instance</h2>
     * Creates a new instance of the BottomDialogQuestion class
     *
     * @param _viewModel The corresponding ProgramViewModel
     *
     */

    public void newInstance(AddProgramViewModel _viewModel, AddAnswersAdapter _addAnswersAdapter, BottomDialogQuestion _bottomDialogQuestion)
    {
        dialog = new BottomDialogAnswer();
        viewModel = _viewModel;
        addAnswersAdapter = _addAnswersAdapter;
        bottomDialogQuestion = _bottomDialogQuestion;
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
        bottomDialogQuestion.updateAdapter();
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
        BottomAnswerBinding binding = DataBindingUtil.inflate(inflater, R.layout.bottom_answer, container, false);
        binding.setLifecycleOwner(this);
        binding.setVm(viewModel);

        viewModel.currentAnswer.set(new Answer((int)(System.currentTimeMillis()) / 1000, "", false));
        binding.save.setOnClickListener(v -> saveAnswer());

        if (getActivity() != null)
        {
            binding.text.setOnFocusChangeListener((v, hasFocus) -> binding.text.post(() ->
            {
                InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (inputMethodManager != null)
                {
                    inputMethodManager.showSoftInput(binding.text, InputMethodManager.SHOW_IMPLICIT);
                }
            }));
            binding.text.requestFocus();

            binding.text.setOnEditorActionListener((v, actionId, event) ->
            {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE))
                {
                    saveAnswer();
                }
                return false;
            });
        }

        return binding.getRoot();

    }

    /**
     * <h2>Save Answer</h2>
     *
     * Checks and saves the current answer
     *
     */

    private void saveAnswer ()
    {
        if (viewModel.currentAnswer.get() != null)
        {
            boolean ok = true;
            if (Objects.requireNonNull(viewModel.currentAnswer.get()).text.get() == null || Objects.requireNonNull(Objects.requireNonNull(viewModel.currentAnswer.get()).text.get()).length() <= 0)
            {
                ok = false;
                Toast.makeText(getContext(), getResources().getString(R.string.error_text), Toast.LENGTH_SHORT).show();
            }

            if (ok)
            {
                addAnswersAdapter.addItem(new Answer(Objects.requireNonNull(viewModel.currentAnswer.get())));
                dismiss();
                bottomDialogQuestion.updateAdapter();
                Toast.makeText(getContext(), getResources().getString(R.string.answer_added), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
