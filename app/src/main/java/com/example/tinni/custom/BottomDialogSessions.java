package com.example.tinni.custom;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableArrayList;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.tinni.R;
import com.example.tinni.adapters.SelectSessionAdapter;
import com.example.tinni.adapters.SelectedSessionAdapter;
import com.example.tinni.adapters.SoundAdapter;
import com.example.tinni.databinding.BottomFinishBinding;
import com.example.tinni.databinding.BottomSessionsBinding;
import com.example.tinni.helpers.Functions;
import com.example.tinni.helpers.ItemClickSupport;
import com.example.tinni.helpers.ItemMoveCallback;
import com.example.tinni.helpers.MarginDecorator;
import com.example.tinni.models.Session;
import com.example.tinni.models.Sound;
import com.example.tinni.ui.add.AddProgram;
import com.example.tinni.ui.add.AddProgramViewModel;
import com.example.tinni.ui.program.ProgramViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <h1>Bottom Dialog Sessions</h1>
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

public class BottomDialogSessions extends BottomSheetDialogFragment
{
    private BottomDialogSessions dialog;
    private AddProgramViewModel viewModel;
    private SelectedSessionAdapter selectedSessionAdapter;
    private BottomSessionsBinding binding;
    private AddProgram addProgram;
    private boolean setup = false;
    private static final Functions func = new Functions();

    /**
     * <h2>New instance</h2>
     * Creates a new instance of the BottomDialogQuestions class
     *
     * @param _viewModel The corresponding AddProgramViewModel
     *
     */

    public void newInstance(AddProgramViewModel _viewModel, SelectedSessionAdapter _selectedSessionAdapter, AddProgram _addProgram)
    {
        dialog = new BottomDialogSessions();
        viewModel = _viewModel;
        selectedSessionAdapter = _selectedSessionAdapter;
        addProgram = _addProgram;
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
        addProgram.updateAdapter(true);
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
        binding = DataBindingUtil.inflate(inflater, R.layout.bottom_sessions, container, false);
        binding.setLifecycleOwner(this);
        binding.setVm(viewModel);

        binding.placeholder.blinking(true);

        Handler handler = new Handler();
        handler.postDelayed(() ->
        {
            binding.sounds.setItemViewCacheSize(getResources().getInteger(R.integer.item_view_cache_size));
            binding.sounds.setDrawingCacheEnabled(true);
            binding.sounds.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
            binding.sounds.addItemDecoration(new MarginDecorator("StaggeredGrid", func.pxFromDp(binding.sounds.getContext(), getResources().getInteger(R.integer.default_margin))));
            binding.sounds.setHasFixedSize(true);
            StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            binding.sounds.setLayoutManager(staggeredGridLayoutManager);

            SelectSessionAdapter selectSessionAdapter = new SelectSessionAdapter(viewModel.sessions);
            binding.sounds.setAdapter(selectSessionAdapter);

            binding.placeholder.blinking(false);
            binding.sounds.setVisibility(View.VISIBLE);

            handler.removeCallbacksAndMessages(null);

            ItemClickSupport.addTo(binding.sounds)
                    .setOnItemClickListener((recyclerView, position, v) ->
                    {
                        Session s = selectSessionAdapter.getItem(position);
                        if (s != null)
                        {
                            sessionClick(s);
                        }
                    });
        }, getResources().getInteger(R.integer.bottom_dialog_delay));

        binding.save.setOnClickListener(v ->
        {
            dismiss();
            addProgram.updateAdapter(true);
        });

        setupSessions();

        ItemClickSupport.addTo(binding.selectedSounds)
                .setOnItemClickListener((recyclerView, position, v) ->
                {
                    Session s = selectedSessionAdapter.getItem(position);
                    if (s != null)
                    {
                        deleteSession(s);
                    }
                });

        return binding.getRoot();

    }

    /**
     * <h2>Delete Session</h2>
     *
     * Deleting a session with a verification prompt
     *
     * @param s The session to be deleted
     *
     */

    private void deleteSession (Session s)
    {
        if (getContext() != null)
        {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext(), R.style.DialogTheme)
                    .setMessage(getResources().getString(R.string.delete_session_verification))
                    .setPositiveButton(getResources().getString(R.string.delete), (dialog, which) ->
                    {
                        selectedSessionAdapter.removeItem(s);
                        Toast.makeText(getContext(), getResources().getString(R.string.session_deleted), Toast.LENGTH_SHORT).show();
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
     * <h2>Setup Sessions</h2>
     *
     * Setting up the RecyclerView for the selected sessions
     *
     */

    private void setupSessions ()
    {
        if (!setup)
        {
            setup = true;
            binding.selectedSounds.setItemViewCacheSize(20);
            binding.selectedSounds.setDrawingCacheEnabled(true);
            binding.selectedSounds.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
            binding.selectedSounds.addItemDecoration(new MarginDecorator("LeftLastRight", func.pxFromDp(binding.selectedSounds.getContext(), getResources().getInteger(R.integer.default_margin))));
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
            binding.selectedSounds.setLayoutManager(layoutManager);
            selectedSessionAdapter = new SelectedSessionAdapter(viewModel.selectedSessions);
            binding.selectedSounds.setAdapter(selectedSessionAdapter);

            ItemTouchHelper.Callback callback = new ItemMoveCallback("sessions", selectedSessionAdapter);
            ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
            touchHelper.attachToRecyclerView(binding.selectedSounds);
        }
    }

    /**
     * <h2>Session Click</h2>
     *
     * Handles the click on a session
     *
     * Source 1: https://stackoverflow.com/a/35383103/2700965
     * Source 2: https://stackoverflow.com/a/13056259/2700965
     *
     * @param s  The clicked Session object
     */

    private void sessionClick (Session s)
    {
        if (getContext() != null)
        {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_length, (ViewGroup) getView(), false);
            final EditText editText = view.findViewById(R.id.minutes);

            if (editText != null)
            {
                editText.setText(String.valueOf(s.getTime() / 60));
                editText.setSelectAllOnFocus(true);
            }
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext(), R.style.DialogTheme)
                    .setMessage(getResources().getString(R.string.session_length))
                    .setView(view)
                    .setPositiveButton(getResources().getString(R.string.save_selection), (dialog, which) ->
                    {
                        if (editText != null && editText.getText() != null && editText.getText().length() > 0)
                        {
                            int time = Integer.parseInt(editText.getText().toString());
                            time = time * 60;
                            if (time > 0)
                            {
                                s.setTime(time);
                            }
                            else
                            {
                                s.setTime(60);
                            }
                        }
                        else
                        {
                            s.setTime(60);
                        }

                        selectedSessionAdapter.addItem(s);
                    })
                    .setNegativeButton(getResources().getString(R.string.cancel), null);
            AlertDialog alertDialog = alertDialogBuilder.create();
            Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            alertDialog.show();
            if (getActivity() != null && getContext() != null && editText != null)
            {
                Button nbutton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                nbutton.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
                Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                pbutton.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
                editText.setOnFocusChangeListener((v, hasFocus) -> editText.post(() ->
                {
                    InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (inputMethodManager != null)
                    {
                        inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
                    }
                }));
                editText.requestFocus();
            }
        }
    }
}
