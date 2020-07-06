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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.example.tinni.R;
import com.example.tinni.adapters.ProgramAdapter;
import com.example.tinni.databinding.BottomImportBinding;
import com.example.tinni.helpers.Constants;
import com.example.tinni.models.Program;
import com.example.tinni.models.Sound;
import com.example.tinni.ui.programs.ProgramsFragment;
import com.example.tinni.ui.programs.ProgramsViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * <h1>Bottom Dialog Import</h1>
 * BottomSheetDialogFragment for the import ui
 *
 * Variables:
 * BottomDialogQuestions dialog: The instance of the current dialog
 * ProgramsViewModel viewModel: The corresponding ProgramsViewModel
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

public class BottomDialogImport extends BottomSheetDialogFragment
{
    private BottomDialogImport dialog;
    private ProgramsViewModel viewModel;
    private ProgramAdapter programAdapter;
    private ProgramsFragment programsFragment;

    /**
     * <h2>New instance</h2>
     * Creates a new instance of the BottomDialogQuestions class
     *
     * @param _viewModel The corresponding ProgramsViewModel
     *
     */

    public void newInstance(ProgramsViewModel _viewModel, ProgramAdapter _programAdapter, ProgramsFragment _programsFragment)
    {
        dialog = new BottomDialogImport();
        viewModel = _viewModel;
        programAdapter = _programAdapter;
        programsFragment = _programsFragment;
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
        BottomImportBinding binding = DataBindingUtil.inflate(inflater, R.layout.bottom_import, container, false);
        binding.setLifecycleOwner(this);
        binding.setVm(viewModel);

        if (binding.json.getText() != null)
        {
            binding.importProgram.setOnClickListener(v -> importProgram(binding.json.getText().toString()));
        }

        return binding.getRoot();

    }

    /**
     * <h2>Import Program</h2>
     *
     * Checks the JSON and adds a new program if successful
     *
     */

    private void importProgram (String json)
    {
        if (json != null && json.length() > 0)
        {
            Type type = new TypeToken<Program>()
            {
            }.getType();

            try
            {
                Program program = Constants.getInstance().gson.fromJson(json, type);

                if (program != null && programAdapter != null)
                {
                    Toast.makeText(getContext(), "new ID: " + program.getId(), Toast.LENGTH_SHORT).show();

                    for (Program p : Constants.getInstance().programs)
                    {
                        Toast.makeText(getContext(), "ANDERE: " + p.getId(), Toast.LENGTH_SHORT).show();
                    }
                    Program p = Constants.getInstance().programs.stream().filter(x -> x.getId() == program.getId()).findFirst().orElse(null);
                    if (p != null)
                    {
                        program.setId((int)(System.currentTimeMillis()) / 1000);
                        Toast.makeText(getContext(), "GIBTS -> " + p.getId() , Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(getContext(), "GIBTS NICHT", Toast.LENGTH_SHORT).show();
                    }
                    programAdapter.addItem(program);
                    Toast.makeText(getContext(), getString(R.string.add_program_success), Toast.LENGTH_SHORT).show();
                    Constants.getInstance().addCustomProgram(program, false);
                    dismiss();
                    programsFragment.scrollToTop();
                    programsFragment.updateAdapter();
                }
                else
                {
                    Toast.makeText(getContext(), getResources().getString(R.string.error_import), Toast.LENGTH_SHORT).show();
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
                Toast.makeText(getContext(), getResources().getString(R.string.error_import), Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            Toast.makeText(getContext(), getResources().getString(R.string.error_code), Toast.LENGTH_SHORT).show();
        }
    }
}
