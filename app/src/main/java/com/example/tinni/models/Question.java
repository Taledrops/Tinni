package com.example.tinni.models;

import android.view.View;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tinni.adapters.AnswerAdapter;
import com.example.tinni.adapters.QuestionAdapter;
import com.example.tinni.helpers.CircleTransform;
import com.example.tinni.helpers.Constants;
import com.example.tinni.helpers.ItemClickSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * <h1>Question Model</h1></h1>
 * Model for a Question object
 *
 * Fields:
 * int id: Unique identifier for question
 * String text: Text of the question
 * List<Answer> answers: The answer list to the question
 * boolean multiple: Whether multiple selection is supported
 * boolean numbered: Whether to show the current id before the text
 *
 * @author Nassim Amar
 * @version 1.0
 * @since   30.06.2020
 */

public class Question
{
    private int id;
    public ObservableField<String> text = new ObservableField<>();
    public ObservableArrayList<Answer> answers = new ObservableArrayList<>();
    public ObservableBoolean multiple = new ObservableBoolean();
    private boolean numbered = true;

    /**
     * <h2>Constructor</h2>
     * Constructor for object
     */

    public Question(int id, String text, ObservableArrayList<Answer> answers, boolean multiple)
    {
        this.id = id;
        this.text.set(text);
        this.answers = answers;
        this.multiple.set(multiple);
    }

    public Question(Question q)
    {
        this.id = q.getId();
        this.text = q.text;
        for (Answer a : q.answers)
        {
            System.out.println("### ADDE ANSWER -> " + a.text.get());
            this.answers.add(new Answer(a));
        }
        this.multiple = q.multiple;
        this.numbered = q.isNumbered();
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public boolean isNumbered()
    {
        return numbered;
    }

    public void setNumbered(boolean numbered)
    {
        this.numbered = numbered;
    }

    /**
     * <h3>Answers</h3>
     * Filling the RecyclerView of the answers
     * Handling clicks on the Answer objects
     *
     * @param question The Question object
     */

    @BindingAdapter("android:answers")
    public static void setAnswers(RecyclerView recyclerView, Question question)
    {
        if (question != null && question.answers != null && question.answers.size() > 0)
        {
            question.answers.forEach(x -> x.active.set(false));
            recyclerView.setItemViewCacheSize(20);
            recyclerView.setDrawingCacheEnabled(true);
            recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
            LinearLayoutManager layoutManager = new LinearLayoutManager(recyclerView.getContext(), LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(layoutManager);

            AnswerAdapter answerAdapter = new AnswerAdapter(question.answers);
            recyclerView.setAdapter(answerAdapter);

            ItemClickSupport.addTo(recyclerView)
                    .setOnItemClickListener((rv, position, v) ->
                    {
                        Answer a = answerAdapter.getItem(position);
                        if (a != null)
                        {
                            if (!question.multiple.get())
                            {
                                question.answers.stream().filter(x -> x.getId() != a.getId()).forEach(x -> x.active.set(false));
                            }
                            a.active.set(!a.active.get());
                        }
                    });
        }
    }
}
