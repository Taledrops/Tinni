package com.example.tinni.ui.completed;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.tinni.R;
import com.example.tinni.adapters.ProgramAdapter;
import com.example.tinni.adapters.ProgramListAdapter;
import com.example.tinni.adapters.ProgramSmallAdapter;
import com.example.tinni.adapters.RatingsAdapter;
import com.example.tinni.databinding.ActivityCompletedBinding;
import com.example.tinni.databinding.ActivityRatingsBinding;
import com.example.tinni.helpers.Constants;
import com.example.tinni.helpers.Functions;
import com.example.tinni.helpers.ItemClickSupport;
import com.example.tinni.helpers.MarginDecorator;
import com.example.tinni.models.Program;
import com.example.tinni.models.SelectedProgram;
import com.example.tinni.ui.ratings.RatingsViewModel;

/**
 * <h1>Completed Activity</h1>
 * Everything related to the completed ui
 *
 * Variables:
 *
 * Session currentSession: The currently opened session
 * FragmentManager fragmentManager: The FragmentManager instance to open BottomDialogQuestions
 * Functions func: A static instance of the helper class "Functions"
 *
 * @author Nassim Amar
 * @version 1.0
 * @since   09.07.2020
 */

public class Completed extends AppCompatActivity
{
    private CompletedViewModel viewModel;
    private ActivityCompletedBinding binding;
    private boolean programsLoaded = false;
    private ProgramListAdapter programAdapter;
    private static final Functions func = new Functions();

    /**
     * <h2>On Create</h2>
     * Connecting the Activity with the ViewModel
     */

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(CompletedViewModel.class);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_completed);
        binding.setLifecycleOwner(this);
        binding.setVm(viewModel);

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        viewModel.getPrograms().observe(this, l ->
        {
            if (l != null && l.size() > 0)
            {
                if (!programsLoaded)
                {
                    programsLoaded = true;
                    binding.programs.setItemViewCacheSize(20);
                    binding.programs.setDrawingCacheEnabled(true);
                    binding.programs.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
                    binding.programs.addItemDecoration(new MarginDecorator("FirstTop", func.pxFromDp(this, getResources().getInteger(R.integer.default_margin))));
                    LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
                    binding.programs.setLayoutManager(layoutManager);
                }

                programAdapter = new ProgramListAdapter(l);
                binding.programs.setAdapter(programAdapter);
            }
        });

        ItemClickSupport.addTo(binding.programs)
                .setOnItemClickListener((recyclerView, position, v) ->
                {
                    SelectedProgram sp = programAdapter.getItem(position);
                    if (sp != null)
                    {
                        openProgram(sp, v);
                    }
                });
    }

    /**
     * <h2>Open Program</h2>
     * Opens the selected program in the Program Activity
     *
     * @param sp the selected program
     *
     */

    private void openProgram (SelectedProgram sp, View v)
    {
        Program p = sp.getProgram();
        if (p != null)
        {
            Intent intent = new Intent(this, com.example.tinni.ui.pastprogram.PastProgram.class);
            intent.putExtra("selectedprogram", sp.getId());
            ImageView iv = v.findViewById(R.id.programImg);
            if (iv != null)
            {
                iv.setTransitionName("program" + p.getId());
                if (p.getBitmap() != null)
                {
                    Constants.getInstance().programs.stream().filter(x -> x.getId() == p.getId()).findFirst().ifPresent(existingProgram -> existingProgram.setBitmap(p.getBitmap()));
                    intent.putExtra("program_transition_name", iv.getTransitionName());
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, iv, iv.getTransitionName());
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
     * <h2>On Resume</h2>
     * Called when Activity becomes visible
     * Calls the prepare function in the viewModel
     *
     */

    @Override
    public void onResume()
    {
        super.onResume();
        Handler handler = new Handler();
        handler.postDelayed(() ->
        {
            viewModel.prepare();
            handler.removeCallbacksAndMessages(null);
        }, getResources().getInteger(R.integer.start_delay));
    }

    /**
     * <h2>On Support Navigate Up</h2>
     * Override
     * Handle the click on the back arrow and call onBackPressed
     */

    @Override
    public boolean onSupportNavigateUp()
    {
        onBackPressed();
        return true;
    }
}
