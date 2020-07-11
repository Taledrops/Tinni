package com.example.tinni.ui.ratings;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.transition.Transition;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.tinni.R;
import com.example.tinni.adapters.RatingAdapter;
import com.example.tinni.adapters.RatingsAdapter;
import com.example.tinni.adapters.SessionAdapter;
import com.example.tinni.custom.BottomDialogQuestions;
import com.example.tinni.custom.BottomDialogRating;
import com.example.tinni.databinding.ActivityProgramBinding;
import com.example.tinni.databinding.ActivityRatingsBinding;
import com.example.tinni.helpers.Constants;
import com.example.tinni.helpers.Functions;
import com.example.tinni.helpers.ItemClickSupport;
import com.example.tinni.helpers.MarginDecorator;
import com.example.tinni.models.Session;
import com.example.tinni.ui.program.ProgramViewModel;

import java.util.Objects;

/**
 * <h1>Program Activity</h1>
 * Everything related to the program ui
 *
 * Variables:
 *
 * Session currentSession: The currently opened session
 * FragmentManager fragmentManager: The FragmentManager instance to open BottomDialogQuestions
 * Functions func: A static instance of the helper class "Functions"
 *
 * @author Nassim Amar
 * @version 1.0
 * @since   28.06.2020
 */

public class Ratings extends AppCompatActivity
{
    private RatingsViewModel viewModel;
    private ActivityRatingsBinding binding;
    private boolean ratingsLoaded = false;
    private RatingsAdapter ratingsAdapter;
    private static final Functions func = new Functions();

    /**
     * <h2>On Create</h2>
     * Connecting the Activity with the ViewModel
     */

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(RatingsViewModel.class);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_ratings);
        binding.setLifecycleOwner(this);
        binding.setVm(viewModel);

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        viewModel.getRatings().observe(this, l ->
        {
            if (l != null && l.size() > 0)
            {
                if (!ratingsLoaded)
                {
                    ratingsLoaded = true;
                    binding.ratings.setItemViewCacheSize(20);
                    binding.ratings.setDrawingCacheEnabled(true);
                    binding.ratings.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
                    binding.ratings.addItemDecoration(new MarginDecorator("FirstTop", func.pxFromDp(this, getResources().getInteger(R.integer.default_margin))));
                    LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
                    binding.ratings.setLayoutManager(layoutManager);
                }

                ratingsAdapter = new RatingsAdapter(l);
                binding.ratings.setAdapter(ratingsAdapter);
            }
        });
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
