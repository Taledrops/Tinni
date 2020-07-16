package com.example.tinni.custom;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableArrayList;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.tinni.R;
import com.example.tinni.adapters.AddAnswersAdapter;
import com.example.tinni.adapters.AddQuestionsAdapter;
import com.example.tinni.databinding.BottomQuestionBinding;
import com.example.tinni.helpers.ItemClickSupport;
import com.example.tinni.helpers.ItemMoveCallback;
import com.example.tinni.models.Answer;
import com.example.tinni.models.Question;
import com.example.tinni.ui.add.AddProgram;
import com.example.tinni.ui.add.AddProgramViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Objects;

/**
 * <h1>Bottom Dialog Question</h1>
 * BottomSheetDialogFragment for the question ui
 *
 * Variables:
 * AddQuestionsAdapter addQuestionsAdapter: The instance of the AddQuestionsAdapter
 * AddProgram addProgram: The instance of the AddProgram Activity
 * AddProgramViewModel viewModel: The corresponding viewModel
 * AddAnswersAdapter addAnswersAdapter: The instance of the AddAnswersAdapter
 * FragmentManager fragmentManager: The instance of the fragmentManager
 * Question question: The current question
 *
 * Source: https://androidwave.com/bottom-sheet-dialog-fragment-in-android/
 *
 * @author Nassim Amar
 * @version 1.0
 * @since   02.07.2020
 */

public class BottomDialogQuestion extends BottomSheetDialogFragment
{
    private AddQuestionsAdapter addQuestionsAdapter;
    private AddProgram addProgram;
    private AddProgramViewModel viewModel;
    private AddAnswersAdapter addAnswersAdapter;
    private FragmentManager fragmentManager;
    private Question question;

    /**
     * <h2>New instance</h2>
     * Creates a new instance of the BottomDialogQuestion class
     *
     * @param _viewModel The corresponding AddProgramViewModel
     * @param _addQuestionsAdapter The instance of the AddQuestionsAdapter
     * @param _addProgram The instance of the AddProgram Activity
     * @param _fragmentManager The instance of the fragmentManager
     * @param _question The current question
     */

    public void newInstance(AddProgramViewModel _viewModel, AddQuestionsAdapter _addQuestionsAdapter, AddProgram _addProgram, FragmentManager _fragmentManager, Question _question)
    {
        viewModel = _viewModel;
        addQuestionsAdapter = _addQuestionsAdapter;
        addProgram = _addProgram;
        fragmentManager = _fragmentManager;
        question = _question;
    }

    /**
     * <h2>On Cancel</h2>
     * Override
     * Called when the dialog gets closed
     * Updates the adapter on the AddProgram Activity
     *
     * @param dialog The DialogInterface instance
     */

    @Override
    public void onCancel(@NonNull DialogInterface dialog)
    {
        super.onCancel(dialog);
        addProgram.updateAdapter(false);
    }

    /**
     * <h2>On Create</h2>
     * Override
     * Called when the dialog gets created
     *
     * @param savedInstanceState The saved Bundle
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
     * Sets up the RecyclerView
     * Handles click events
     *
     * @param inflater The LayoutInflater
     * @param container The ViewGroup
     * @param savedInstanceState The saved Bundle
     */

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        BottomQuestionBinding binding = DataBindingUtil.inflate(inflater, R.layout.bottom_question, container, false);
        binding.setLifecycleOwner(this);
        binding.setVm(viewModel);

        if (question == null)
        {
            viewModel.currentQuestion.set(new Question((int) (System.currentTimeMillis()) / 1000, "", new ObservableArrayList<>(), false));
        }
        else
        {
            ObservableArrayList<Answer> answers = new ObservableArrayList<>();
            for (Answer a : question.answers)
            {
                answers.add(new Answer(a));
            }
            viewModel.currentQuestion.set(new Question(question.getId(), question.text.get(), answers, question.multiple.get()));
            binding.heading.setText(getResources().getString(R.string.edit_question));
        }
        binding.save.setOnClickListener(v -> saveQuestion());

        binding.answers.setItemViewCacheSize(20);
        binding.answers.setDrawingCacheEnabled(true);
        binding.answers.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.answers.setLayoutManager(layoutManager);
        addAnswersAdapter = new AddAnswersAdapter(Objects.requireNonNull(viewModel.currentQuestion.get()).answers);
        binding.answers.setAdapter(addAnswersAdapter);

        ItemTouchHelper.Callback callback = new ItemMoveCallback("answers", addAnswersAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(binding.answers);

        binding.emptyAnswersContainer.emptyAnswers.setOnClickListener(v -> openAnswer());

        binding.addAnswer.setOnClickListener(v -> openAnswer());

        ItemClickSupport.addTo(binding.answers)
                .setOnItemClickListener((recyclerView, position, v) ->
                {
                    Answer a = addAnswersAdapter.getItem(position);
                    if (a != null)
                    {
                        deleteAnswer(a);
                    }
                });

        if (getActivity() != null && question == null)
        {
            binding.title.setOnFocusChangeListener((v, hasFocus) -> binding.title.post(() ->
            {
                InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (inputMethodManager != null)
                {
                    inputMethodManager.showSoftInput(binding.title, InputMethodManager.SHOW_IMPLICIT);
                }
            }));
            binding.title.requestFocus();
        }

        return binding.getRoot();

    }

    /**
     * <h2>Delete Answer</h2>
     * Deleting an answer with a verification prompt
     *
     * @param a The answer to be deleted
     */

    private void deleteAnswer (Answer a)
    {
        if (getContext() != null)
        {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext(), R.style.DialogTheme)
                    .setMessage(getResources().getString(R.string.delete_answer_verification))
                    .setPositiveButton(getResources().getString(R.string.delete), (dialog, which) ->
                    {
                        addAnswersAdapter.removeItem(a);
                        Toast.makeText(getContext(), getResources().getString(R.string.answer_deleted), Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton(getResources().getString(R.string.cancel), null);
            AlertDialog alertDialog = alertDialogBuilder.create();
            Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            alertDialog.show();
            Button nbutton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
            nbutton.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
            Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
            pbutton.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
        }
    }

    /**
     * <h2>Update Adapter</h2>
     * Refreshes the RecyclerView by refreshing its adapter
     */

    public void updateAdapter ()
    {
        if (addAnswersAdapter != null)
        {
            addAnswersAdapter.reloadList();
        }
    }

    /**
     * <h2>Open Answer</h2>
     * Opens the answer creator
     */

    private void openAnswer ()
    {
        if (addAnswersAdapter != null)
        {
            BottomDialogAnswer bottomDialogAnswer = new BottomDialogAnswer();
            bottomDialogAnswer.newInstance(viewModel, addAnswersAdapter, this);
            bottomDialogAnswer.show(fragmentManager, "answer");
        }
    }

    /**
     * <h2>Save Question</h2>
     * Checks ans saves the current question
     */

    private void saveQuestion ()
    {
        if (viewModel.currentQuestion.get() != null)
        {
            boolean ok = true;
            if (Objects.requireNonNull(viewModel.currentQuestion.get()).text.get() == null || Objects.requireNonNull(Objects.requireNonNull(viewModel.currentQuestion.get()).text.get()).length() <= 0)
            {
                ok = false;
                Toast.makeText(getContext(), getResources().getString(R.string.error_title), Toast.LENGTH_SHORT).show();
            }
            else if (Objects.requireNonNull(viewModel.currentQuestion.get()).answers == null || Objects.requireNonNull(viewModel.currentQuestion.get()).answers.size() == 0)
            {
                ok = false;
                Toast.makeText(getContext(), getResources().getString(R.string.error_no_answers), Toast.LENGTH_SHORT).show();
            }

            if (ok)
            {
                if (question == null)
                {
                    addQuestionsAdapter.addItem(new Question(Objects.requireNonNull(viewModel.currentQuestion.get())));
                    Toast.makeText(getContext(), getResources().getString(R.string.question_added), Toast.LENGTH_SHORT).show();
                }
                else
                {
                    question.text.set(Objects.requireNonNull(Objects.requireNonNull(viewModel.currentQuestion.get()).text.get()));
                    question.multiple.set(Objects.requireNonNull(Objects.requireNonNull(viewModel.currentQuestion.get()).multiple.get()));
                    question.answers = new ObservableArrayList<>();
                    question.answers.addAll(Objects.requireNonNull(viewModel.currentQuestion.get()).answers);
                    addQuestionsAdapter.editItem(question);
                    Toast.makeText(getContext(), getResources().getString(R.string.question_edited), Toast.LENGTH_SHORT).show();
                }
                dismiss();
                addProgram.updateAdapter(false);
            }
        }
    }
}
