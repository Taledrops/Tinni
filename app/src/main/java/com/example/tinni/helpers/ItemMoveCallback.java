package com.example.tinni.helpers;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tinni.adapters.AddAnswersAdapter;
import com.example.tinni.adapters.AddQuestionsAdapter;
import com.example.tinni.adapters.SelectedSessionAdapter;

/**
 * <h1>Item Move Callback</h1>
 * Helper class to enable drag & drop feature inside RecyclerView
 *
 * Source: https://www.journaldev.com/23208/android-recyclerview-drag-and-drop
 *
 * @author Nassim Amar
 * @version 1.0
 * @since   05.07.2020
 */

public class ItemMoveCallback extends ItemTouchHelper.Callback
{
    private final ItemTouchHelperContract mAdapter;
    private final String mType;

    public ItemMoveCallback(String type, ItemTouchHelperContract adapter)
    {
        mAdapter = adapter;
        mType = type;
    }

    @Override
    public boolean isLongPressDragEnabled()
    {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled()
    {
        return false;
    }



    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i)
    {

    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder)
    {
        int dragFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        if (!mType.equals("sessions"))
        {
            dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        }
        return makeMovementFlags(dragFlags, 0);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                          RecyclerView.ViewHolder target)
    {
        switch (mType)
        {
            case "sessions":
                mAdapter.onRowMovedSession(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                break;
            case "questions":
                mAdapter.onRowMovedQuestions(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                break;
            case "answers":
                mAdapter.onRowMovedAnswers(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                break;
        }
        return true;
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder,
                                  int actionState)
    {
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE)
        {
            switch (mType)
            {
                case "sessions":
                    if (viewHolder instanceof SelectedSessionAdapter.SessionViewHolder)
                    {
                        SelectedSessionAdapter.SessionViewHolder myViewHolder=
                                (SelectedSessionAdapter.SessionViewHolder) viewHolder;
                        mAdapter.onRowSelectedSession(myViewHolder);
                    }
                    break;
                case "questions":
                    if (viewHolder instanceof AddQuestionsAdapter.QuestionViewHolder)
                    {
                        AddQuestionsAdapter.QuestionViewHolder myViewHolder=
                                (AddQuestionsAdapter.QuestionViewHolder) viewHolder;
                        mAdapter.onRowSelectedQuestions(myViewHolder);
                    }
                    break;
                case "answers":
                    if (viewHolder instanceof AddAnswersAdapter.AnswerViewHolder)
                    {
                        AddAnswersAdapter.AnswerViewHolder myViewHolder=
                                (AddAnswersAdapter.AnswerViewHolder) viewHolder;
                        mAdapter.onRowSelectedAnswers(myViewHolder);
                    }
                    break;
            }
        }

        super.onSelectedChanged(viewHolder, actionState);
    }
    @Override
    public void clearView(@NonNull RecyclerView recyclerView,
                          @NonNull RecyclerView.ViewHolder viewHolder)
    {
        super.clearView(recyclerView, viewHolder);

        switch (mType)
        {
            case "sessions":
                if (viewHolder instanceof SelectedSessionAdapter.SessionViewHolder)
                {
                    SelectedSessionAdapter.SessionViewHolder myViewHolder=
                            (SelectedSessionAdapter.SessionViewHolder) viewHolder;
                    mAdapter.onRowClearSession(myViewHolder);
                }
                break;
            case "questions":
                if (viewHolder instanceof AddQuestionsAdapter.QuestionViewHolder)
                {
                    AddQuestionsAdapter.QuestionViewHolder myViewHolder=
                            (AddQuestionsAdapter.QuestionViewHolder) viewHolder;
                    mAdapter.onRowClearQuestions(myViewHolder);
                }
                break;
            case "answers":
                if (viewHolder instanceof AddAnswersAdapter.AnswerViewHolder)
                {
                    AddAnswersAdapter.AnswerViewHolder myViewHolder=
                            (AddAnswersAdapter.AnswerViewHolder) viewHolder;
                    mAdapter.onRowClearAnswers(myViewHolder);
                }
                break;
        }
    }

    public interface ItemTouchHelperContract
    {
        void onBindViewHolderSession(SelectedSessionAdapter.SessionViewHolder holder, int position);
        void onRowMovedSession(int fromPosition, int toPosition);
        void onRowSelectedSession(SelectedSessionAdapter.SessionViewHolder myViewHolder);
        void onRowClearSession(SelectedSessionAdapter.SessionViewHolder myViewHolder);

        void onBindViewHolderQuestions(AddQuestionsAdapter.QuestionViewHolder holder, int position);
        void onRowMovedQuestions(int fromPosition, int toPosition);
        void onRowSelectedQuestions(AddQuestionsAdapter.QuestionViewHolder myViewHolder);
        void onRowClearQuestions(AddQuestionsAdapter.QuestionViewHolder myViewHolder);

        void onBindViewHolderAnswers(AddAnswersAdapter.AnswerViewHolder holder, int position);
        void onRowMovedAnswers(int fromPosition, int toPosition);
        void onRowSelectedAnswers(AddAnswersAdapter.AnswerViewHolder myViewHolder);
        void onRowClearAnswers(AddAnswersAdapter.AnswerViewHolder myViewHolder);
    }
}
