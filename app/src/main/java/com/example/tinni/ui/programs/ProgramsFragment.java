package com.example.tinni.ui.programs;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.tinni.R;
import com.example.tinni.adapters.ProgramAdapter;
import com.example.tinni.custom.BottomDialogImport;
import com.example.tinni.databinding.FragmentProgramsBinding;
import com.example.tinni.helpers.Constants;
import com.example.tinni.helpers.Functions;
import com.example.tinni.helpers.ItemClickSupport;
import com.example.tinni.helpers.MarginDecorator;
import com.example.tinni.models.Program;
import com.example.tinni.ui.add.AddProgram;

/**
 * <h1>Programs Fragment</h1>
 * Everything related to the programs ui
 *
 * Variables:
 * FragmentProgramsBinding binding: The binding to the layout file
 * ProgramsViewModel viewModel: The ViewModel to this page
 * boolean loaded: Indicator if programs were already loaded
 * FragmentManager fragmentManager: Instance of the fragment manager
 * ProgramAdapter programAdapter: The adapter for the program items
 * Functions func: A static instance of the helper class "Functions"
 *
 * @author Nassim Amar
 * @version 1.0
 * @since   27.06.2020
 */

public class ProgramsFragment extends Fragment
{
    private FragmentProgramsBinding binding = null;
    private ProgramsViewModel viewModel;
    private static boolean loaded = false;
    private static FragmentManager fragmentManager;
    private ProgramAdapter programAdapter;
    private static final Functions func = new Functions();

    /**
     * <h2>On Create View</h2>
     * Connecting the Fragment with the ViewModel and inflating its layout view
     * Observing program items and updating the ui
     * Handling click events
     * Handling swipe refreshes
     */

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        viewModel = new ViewModelProvider(this).get(ProgramsViewModel.class);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_programs, container, false);
        binding.setLifecycleOwner(this);
        binding.setVm(viewModel);

        if (getActivity() != null)
        {
            fragmentManager = getActivity().getSupportFragmentManager();
            binding.swipeRefresh.setColorSchemeColors(ContextCompat.getColor(getActivity(), R.color.colorPrimary), ContextCompat.getColor(getActivity(), R.color.colorPrimary), ContextCompat.getColor(getActivity(), R.color.colorPrimary));
        }

        viewModel.getPrograms().observe(getViewLifecycleOwner(), result ->
        {
            if (getContext() != null && result != null && result.size() > 0)
            {
                if (programAdapter == null)
                {
                    binding.programs.setItemViewCacheSize(20);
                    binding.programs.setDrawingCacheEnabled(true);
                    binding.programs.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
                    binding.programs.addItemDecoration(new MarginDecorator("FirstTop", func.pxFromDp(getContext(), getResources().getInteger(R.integer.default_margin))));
                    LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                    binding.programs.setLayoutManager(layoutManager);

                    programAdapter = new ProgramAdapter(result);
                    binding.programs.setAdapter(programAdapter);
                }
                else
                {
                    programAdapter.reloadList(result);
                }
            }
        });

        binding.swipeRefresh.setOnRefreshListener(() ->
        {
            if (!viewModel.loading.get())
            {
                viewModel.fill(success -> binding.swipeRefresh.setRefreshing(false));
            }
            else
            {
                binding.swipeRefresh.setRefreshing(false);
            }
        });

        binding.fab.setOnClickListener(v -> openAdd());

        binding.importProgram.setOnClickListener(v -> importProgram());

        ItemClickSupport.addTo(binding.programs)
                .setOnItemClickListener((recyclerView, position, v) ->
                {
                    Program p = programAdapter.getItem(position);
                    if (p != null)
                    {
                        openProgram(p, v);
                    }
                });

        return binding.getRoot();
    }

    /**
     * <h2>Import Program</h2>
     * Opens a dialog to import a program via JSON
     *
     */

    private void importProgram ()
    {
        if (fragmentManager != null)
        {
            BottomDialogImport bottomDialogImport = new BottomDialogImport();
            bottomDialogImport.newInstance(viewModel, programAdapter, this);
            bottomDialogImport.show(fragmentManager, "import");
        }
    }

    /**
     * <h2>Update Adapter</h2>
     * Refreshes the RecyclerView by refreshing its adapter
     */

    public void updateAdapter ()
    {
        if (programAdapter != null)
        {
            programAdapter.updateList();
        }
    }

    /**
     * <h2>Open Add</h2>
     * Opens the Add Program Activity
     */

    private void openAdd ()
    {
        startActivity(new Intent(getActivity(), AddProgram.class));
    }

    /**
     * <h2>Open Program</h2>
     * Opens the selected program in the Program Activity
     *
     * @param p the selected program
     * @param v the selected view
     *
     */

    private void openProgram (Program p, View v)
    {
        if (getActivity() != null)
        {
            Intent intent = new Intent(getActivity(), com.example.tinni.ui.program.Program.class);
            intent.putExtra("program", p.getId());
            ImageView iv = v.findViewById(R.id.programImg);
            if (iv != null)
            {
                iv.setTransitionName("program" + p.getId());
                if (p.getBitmap() != null)
                {
                    intent.putExtra("program_transition_name", iv.getTransitionName());
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), iv, iv.getTransitionName());
                    startActivity(intent, options.toBundle());
                }
                else
                {
                    startActivity(intent);
                }
            }
            else
            {
                startActivity(intent);
            }
        }
    }

    /**
     * <h2>Scroll To Top</h2>
     * Scrolls the RecyclerView to the top
     * Triggered by click on the current navigation sound_item_horizontal
     */

    public void scrollToTop ()
    {
        binding.programs.scrollToPosition(0);
        binding.appBar.setExpanded(true, false);
    }

    /**
     * <h2>On Resume</h2>
     * Loaded indicator assures that content is only loaded once
     * Calling viewModel.fill() inside onResume() has the purpose to only load the data once the page is fully loaded
     * Checking for viewModel.getPrograms().getValue() == null is a fallback for when the user closes and reopens the app
     */

    @Override
    public void onResume()
    {
        super.onResume();
        if (!loaded || viewModel.getPrograms().getValue() == null)
        {
            loaded = true;
            Handler handler = new Handler();
            handler.postDelayed(() ->
            {
                viewModel.fill(success -> {});
                handler.removeCallbacksAndMessages(null);
            }, getResources().getInteger(R.integer.start_delay));
        }

        if (Constants.getInstance().changedProgram != 0 && programAdapter != null)
        {
            programAdapter.programList.stream().filter(x -> x.getId() == Constants.getInstance().changedProgram).findFirst().ifPresent(p -> p.active.set(false));
            Constants.getInstance().changedProgram = 0;
        }
        else if (Constants.getInstance().programToAdd != null && programAdapter != null)
        {
            programAdapter.addItem(Constants.getInstance().programToAdd);
            Constants.getInstance().programToAdd = null;
            binding.programs.scrollToPosition(0);
            Toast.makeText(getContext(), getString(R.string.add_program_success), Toast.LENGTH_SHORT).show();
        }
        else if (Constants.getInstance().programToRemove != null && programAdapter != null)
        {
            Program p = programAdapter.programList.stream().filter(x -> x.getId() == Constants.getInstance().programToRemove.getId()).findFirst().orElse(null);
            if (p != null)
            {
                programAdapter.removeItem(p);
                Constants.getInstance().programToRemove = null;
                Toast.makeText(getContext(), getResources().getString(R.string.delete_program_success), Toast.LENGTH_SHORT).show();
            }
        }
    }
}