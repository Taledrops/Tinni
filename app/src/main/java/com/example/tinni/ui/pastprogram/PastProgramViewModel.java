package com.example.tinni.ui.pastprogram;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tinni.R;
import com.example.tinni.helpers.Constants;
import com.example.tinni.models.Answer;
import com.example.tinni.models.Category;
import com.example.tinni.models.Program;
import com.example.tinni.models.Question;
import com.example.tinni.models.Questionnaire;
import com.example.tinni.models.SelectedProgram;
import com.example.tinni.models.Session;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <h1>Past program ViewModel</h1>
 * ViewModel for the past program ui
 *
 * Variables:
 * MutableLiveData<Sound> current: The current Program object
 * ObservableField<SelectedProgram> active: Set when program is the currently selected one
 * Session nextSession: The next session which has not been completed yet
 *
 * @author Nassim Amar
 * @version 1.0
 * @since   28.06.2020
 */

public class PastProgramViewModel extends ViewModel
{
    public ObservableBoolean loading = new ObservableBoolean(true);
    public ObservableField<SelectedProgram> selected = new ObservableField<>();
    public MutableLiveData<List<Questionnaire>> questionnaire = new MutableLiveData<>();
    public MutableLiveData<List<Session>> sessions = new MutableLiveData<>();

    public MutableLiveData<List<Questionnaire>> getQuestionnaire()
    {
        return questionnaire;
    }

    public MutableLiveData<List<Session>> getSessions()
    {
        return sessions;
    }

    /**
     * <h2>Prepare</h2>
     * Prepare the current program
     *
     * @param selectedProgram The selectedProgram object
     */

    public void prepare (Context context, SelectedProgram selectedProgram)
    {
        if (questionnaire.getValue() == null || questionnaire.getValue().size() == 0 || selected.get() == null)
        {
            Toast.makeText(context, "na 1 -> " + selectedProgram.getStartQuestions().size() + " --- " + selectedProgram.getEndQuestions().size(), Toast.LENGTH_SHORT).show();
            selected.set(selectedProgram);

            List<Questionnaire> newQuestionnaire = new ArrayList<>();
            int i = 0;
            for (Question q : selectedProgram.getStartQuestions())
            {
                if (!q.multiple.get() || q.answers.size() < 2)
                {
                    Answer a = q.answers.stream().filter(x -> x.active.get()).findFirst().orElse(null);
                    String answer = context.getString(R.string.no_answer);
                    String endAnswer = context.getString(R.string.no_answer);
                    if (a != null)
                    {
                        answer = a.text.get();
                    }

                    if (selectedProgram.getEndQuestions().size() >= i)
                    {
                        a = selectedProgram.getEndQuestions().get(i).answers.stream().filter(x -> x.active.get()).findFirst().orElse(null);
                        if (a != null)
                        {
                            endAnswer = a.text.get();
                        }
                    }
                    else
                    {
                        endAnswer = context.getString(R.string.no_answer);
                    }
                    newQuestionnaire.add(new Questionnaire(i + 1, q.text.get(), answer, endAnswer));
                }
                else
                {
                    List<Answer> answers = q.answers.stream().filter(p -> p.active.get()).collect(Collectors.toList());
                    StringBuilder answer = new StringBuilder();
                    StringBuilder endAnswer = new StringBuilder();
                    int j = 1;
                    for (Answer a : answers)
                    {
                        answer.append(a.text.get());
                        if (j < answers.size())
                        {
                            answer.append(System.lineSeparator());
                        }
                        j++;
                    }
                    if (selectedProgram.getEndQuestions().size() >= i)
                    {
                        List<Answer> answersEnd = selectedProgram.getEndQuestions().get(i).answers.stream().filter(p -> p.active.get()).collect(Collectors.toList());
                        j = 1;
                        for (Answer a : answersEnd)
                        {
                            endAnswer.append(a.text.get());
                            if (j < answersEnd.size())
                            {
                                endAnswer.append(System.lineSeparator());
                            }
                            j++;
                        }
                    }
                    newQuestionnaire.add(new Questionnaire(i + 1, q.text.get(), answer.toString(), endAnswer.toString()));
                }

                i++;
            }
            Toast.makeText(context, "na 2 -> " + newQuestionnaire.size(), Toast.LENGTH_SHORT).show();

            if (newQuestionnaire.size() > 0)
            {
                questionnaire.setValue(newQuestionnaire);
            }
            else
            {
                questionnaire.setValue(null);
            }
            loading.set(false);
        }
        if (sessions.getValue() == null || sessions.getValue().size() == 0)
        {
            if (selectedProgram.getProgram() != null && selectedProgram.getProgram().getSessions() != null && selectedProgram.getProgram().getSessions().size() > 0)
            {
                sessions.setValue(selectedProgram.getProgram().getSessions());
            }
            else
            {
                sessions.setValue(null);
            }
        }
    }
}
