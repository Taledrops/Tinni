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
import androidx.databinding.ObservableArrayList;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.tinni.R;
import com.example.tinni.adapters.QuestionAdapter;
import com.example.tinni.databinding.BottomQuestionsBinding;
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
 * <h1>Bottom Dialog Rating</h1>
 * BottomSheetDialogFragment for the ratings ui
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

public class BottomDialogRating extends BottomSheetDialogFragment
{
    private BottomDialogRating dialog;
    private ProgramViewModel viewModel;
    private Session session;
    private FragmentManager fragmentManager;
    private List<Question> questions = new ArrayList<>();

    /**
     * <h2>New instance</h2>
     * Creates a new instance of the BottomDialogQuestions class
     *
     * @param _viewModel The corresponding ProgramViewModel
     *
     */

    public void newInstance(ProgramViewModel _viewModel, Session _session, FragmentManager _fragmentManager)
    {
        dialog = new BottomDialogRating();
        viewModel = _viewModel;
        session = _session;
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
        Toast.makeText(getContext(), "CANCEL", Toast.LENGTH_SHORT).show();
        if (viewModel.updateProgram())
        {
            BottomDialogQuestions bottomDialogQuestions = new BottomDialogQuestions();
            bottomDialogQuestions.setCancelable(false);
            bottomDialogQuestions.newInstance(viewModel, false, fragmentManager);
            bottomDialogQuestions.show(fragmentManager, "program");
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
        BottomRatingBinding binding = DataBindingUtil.inflate(inflater, R.layout.bottom_rating, container, false);
        binding.setLifecycleOwner(this);
        binding.setVm(viewModel);

        ObservableArrayList<Answer> answers = new ObservableArrayList<>();
        answers.add(new Answer(1, getResources().getString(R.string.very_good), true));
        answers.add(new Answer(2, getResources().getString(R.string.good), true));
        answers.add(new Answer(3, getResources().getString(R.string.neutral), true));
        answers.add(new Answer(4, getResources().getString(R.string.less_good), true));
        answers.add(new Answer(5, getResources().getString(R.string.bad), true));
        Question q = new Question(0, getResources().getString(R.string.session_rating), answers, false);
        q.setNumbered(false);
        questions.add(q);

        binding.questions.setItemViewCacheSize(20);
        binding.questions.setDrawingCacheEnabled(true);
        binding.questions.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.questions.setLayoutManager(layoutManager);

        QuestionAdapter questionAdapter = new QuestionAdapter(questions);
        binding.questions.setAdapter(questionAdapter);

        binding.save.setOnClickListener(v -> save());

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
            int answerId = 0;
            for (Question q: Objects.requireNonNull(questions))
            {
                Answer a = q.answers.stream().filter(x -> x.active.get()).findFirst().orElse(null);
                if (a != null)
                {
                    answerId = a.getId();
                }
            }

            if (answerId > 0)
            {
                getDialog().dismiss();
                session.setRating(answerId);
                session.setDate(System.currentTimeMillis());
                if (viewModel.updateProgram())
                {
                    BottomDialogQuestions bottomDialogQuestions = new BottomDialogQuestions();
                    bottomDialogQuestions.setCancelable(false);
                    bottomDialogQuestions.newInstance(viewModel, false, fragmentManager);
                    bottomDialogQuestions.show(fragmentManager, "program");
                }
                else
                {
                    BottomDialogNext bottomDialogNext = new BottomDialogNext();
                    bottomDialogNext.newInstance(viewModel);
                    bottomDialogNext.show(fragmentManager, "next");
                }
            }
            else
            {
                Toast.makeText(getContext(), getResources().getString(R.string.error_rating), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
