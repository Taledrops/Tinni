package com.example.tinni.ui.stats;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.tinni.MainActivity;
import com.example.tinni.R;
import com.example.tinni.adapters.ProgramSmallAdapter;
import com.example.tinni.adapters.RatingAdapter;
import com.example.tinni.adapters.SoundHorizontalAdapter;
import com.example.tinni.adapters.StatAdapter;
import com.example.tinni.databinding.FragmentStatsBinding;
import com.example.tinni.helpers.Constants;
import com.example.tinni.helpers.Functions;
import com.example.tinni.helpers.ItemClickSupport;
import com.example.tinni.helpers.MarginDecorator;
import com.example.tinni.models.Program;
import com.example.tinni.models.Rating;
import com.example.tinni.models.SelectedProgram;
import com.example.tinni.models.Session;

import java.util.Objects;

/**
 * <h1>Stats Fragment</h1>
 * Everything related to the stats ui
 *
 * @author Nassim Amar
 * @version 1.0
 * @since   18.06.2020
 */

public class StatsFragment extends Fragment
{
    private FragmentStatsBinding binding = null;
    private StatsViewModel viewModel;
    private boolean ratingsLoaded = false;
    private boolean programsLoaded = false;
    private boolean statsLoaded = false;
    private RatingAdapter ratingAdapter;
    private ProgramSmallAdapter programAdapter;
    private StatAdapter statAdapter;
    private static final Functions func = new Functions();

    /**
     * <h2>On Create View</h2>
     * Connecting the Fragment with the ViewModel and inflating its layout view
     */

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        viewModel = new ViewModelProvider(this).get(StatsViewModel.class);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_stats, container, false);
        binding.setLifecycleOwner(this);
        binding.setVm(viewModel);

        viewModel.getRatings().observe(getViewLifecycleOwner(), l ->
        {
            if (l != null && l.size() > 0 && getContext() != null)
            {
                if (!ratingsLoaded)
                {
                    ratingsLoaded = true;
                    binding.ratings.setItemViewCacheSize(20);
                    binding.ratings.setDrawingCacheEnabled(true);
                    binding.ratings.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
                    binding.ratings.addItemDecoration(new MarginDecorator("FirstAndLast", func.pxFromDp(getContext(), getResources().getInteger(R.integer.default_margin))));
                    LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
                    binding.ratings.setLayoutManager(layoutManager);
                }

                ratingAdapter = new RatingAdapter(l);
                binding.ratings.setAdapter(ratingAdapter);
            }
        });

        viewModel.getPrograms().observe(getViewLifecycleOwner(), l ->
        {
            if (l != null && l.size() > 0 && getContext() != null)
            {
                if (!programsLoaded)
                {
                    programsLoaded = true;
                    binding.programs.setItemViewCacheSize(20);
                    binding.programs.setDrawingCacheEnabled(true);
                    binding.programs.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
                    binding.programs.addItemDecoration(new MarginDecorator("LeftLastRight", func.pxFromDp(getContext(), getResources().getInteger(R.integer.default_margin))));
                    LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
                    binding.programs.setLayoutManager(layoutManager);
                }

                programAdapter = new ProgramSmallAdapter(l);
                binding.programs.setAdapter(programAdapter);
            }
        });

        viewModel.getStats().observe(getViewLifecycleOwner(), l ->
        {
            if (l != null && l.size() > 0 && getContext() != null)
            {
                if (!statsLoaded)
                {
                    statsLoaded = true;
                    binding.stats.setItemViewCacheSize(20);
                    binding.stats.setDrawingCacheEnabled(true);
                    binding.stats.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
                    binding.stats.addItemDecoration(new MarginDecorator("LeftLastRight", func.pxFromDp(getContext(), getResources().getInteger(R.integer.default_margin))));
                    LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
                    binding.stats.setLayoutManager(layoutManager);
                }

                statAdapter = new StatAdapter(l);
                binding.stats.setAdapter(statAdapter);
            }
        });

        binding.completedHeader.setOnClickListener(v -> openRatings());
        binding.programsHeader.setOnClickListener(v -> openCompletedPrograms());
        binding.delete.setOnClickListener(v -> deleteData());

        ItemClickSupport.addTo(binding.ratings)
                .setOnItemClickListener((recyclerView, position, v) -> openRatings());

        ItemClickSupport.addTo(binding.programs)
                .setOnItemClickListener((recyclerView, position, v) ->
                {
                    SelectedProgram sp = programAdapter.getItem(position);
                    if (sp != null)
                    {
                        openProgram(sp, v);
                    }
                });

        return binding.getRoot();
    }

    /**
     * <h2>Delete Data</h2>
     * Deletes all the SharedPreferences and restarts the app
     *
     * Source: https://stackoverflow.com/a/46848226/2700965
     *
     */

    private void deleteData ()
    {
        if (getActivity() != null)
        {
            @SuppressLint("ApplySharedPref") AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity(), R.style.DialogTheme)
                    .setMessage(getResources().getString(R.string.delete_data_verfication))
                    .setPositiveButton(getResources().getString(R.string.delete), (dialog, which) ->
                    {
                        Constants.getInstance().preferences.edit().clear().commit();
                        PackageManager packageManager = getActivity().getPackageManager();
                        Intent intent = packageManager.getLaunchIntentForPackage(getActivity().getPackageName());
                        if (intent != null)
                        {
                            ComponentName componentName = intent.getComponent();
                            Intent mainIntent = Intent.makeRestartActivityTask(componentName);
                            getActivity().startActivity(mainIntent);
                            Runtime.getRuntime().exit(0);
                        }
                    })
                    .setNegativeButton(getResources().getString(R.string.cancel), null);
            AlertDialog alertDialog = alertDialogBuilder.create();
            Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            alertDialog.show();
            Button nbutton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
            nbutton.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorAccent));
            Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
            pbutton.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorAccent));
        }
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
        if (getActivity() != null)
        {
            Program p = sp.getProgram();
            if (p != null)
            {
                Intent intent = new Intent(getActivity(), com.example.tinni.ui.pastprogram.PastProgram.class);
                intent.putExtra("selectedprogram", sp.getId());
                ImageView iv = v.findViewById(R.id.programImg);
                if (iv != null)
                {
                    iv.setTransitionName("program" + p.getId());
                    if (p.getBitmap() != null)
                    {
                        Constants.getInstance().programs.stream().filter(x -> x.getId() == p.getId()).findFirst().ifPresent(existingProgram -> existingProgram.setBitmap(p.getBitmap()));
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
    }

    /**
     * <h2>Open Ratings</h2>
     * Opens the Ratings Activity
     *
     */

    private void openRatings ()
    {
        if (Constants.getInstance().ratings.size() > 0)
        {
            Intent intent = new Intent(getActivity(), com.example.tinni.ui.ratings.Ratings.class);
            startActivity(intent);
        }
        else
        {
            Toast.makeText(getContext(), getResources().getString(R.string.error_no_ratings), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * <h2>Open Completed Programs</h2>
     * Opens the Completed Activity
     *
     */

    private void openCompletedPrograms ()
    {
        if (Constants.getInstance().pastPrograms.size() > 0)
        {
            Intent intent = new Intent(getActivity(), com.example.tinni.ui.completed.Completed.class);
            startActivity(intent);
        }
        else
        {
            Toast.makeText(getContext(), getResources().getString(R.string.error_no_completed_programs), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * <h2>On Resume</h2>
     * Called when activity becomes visible
     * Calls the prepare function on the viewModel
     *
     */

    @Override
    public void onResume()
    {
        super.onResume();
        Handler handler = new Handler();
        handler.postDelayed(() ->
        {
            viewModel.prepare(getContext());
            handler.removeCallbacksAndMessages(null);
        }, getResources().getInteger(R.integer.start_delay));
    }
}
