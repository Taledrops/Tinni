package com.example.tinni.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.tinni.R;
import com.example.tinni.adapters.SoundHorizontalAdapter;
import com.example.tinni.custom.BottomDialogDailyRating;
import com.example.tinni.databinding.FragmentHomeBinding;
import com.example.tinni.helpers.Constants;
import com.example.tinni.helpers.Functions;
import com.example.tinni.helpers.ItemClickSupport;
import com.example.tinni.helpers.MarginDecorator;
import com.example.tinni.models.Program;
import com.example.tinni.models.Sound;

import java.util.Objects;

/**
 * <h1>Home Fragment</h1>
 * Everything related to the home ui
 *
 * Variables:
 * FragmentHomeBinding binding: The binding to the layout file
 * HomeViewModel viewModel: The ViewModel to this page
 * boolean lastLoaded: Indicator whether recently played items have already been loaded
 * boolean favoritesLoaded: Indicator whether favorite items have already been loaded
 * SoundHorizontalAdapter lastAdapter: Instance of the last adapter
 * SoundHorizontalAdapter favoritesAdapter: Instance of the favorites adapter
 * FragmentManager fragmentManager: Instance of the fragment manager
 * boolean loaded: Indicator whether the items have already been loaded
 * Functions func = new Functions(): Instance of the Functions helper class
 *
 * @author Nassim Amar
 * @version 1.0
 * @since   18.06.2020
 */

public class HomeFragment extends Fragment
{
    private FragmentHomeBinding binding = null;
    private HomeViewModel viewModel;
    private boolean lastLoaded = false;
    private boolean favoritesLoaded = false;
    private SoundHorizontalAdapter lastAdapter;
    private SoundHorizontalAdapter favoritesAdapter;
    private static FragmentManager fragmentManager;
    private boolean loaded = false;
    private static final Functions func = new Functions();

    /**
     * <h2>On Fill Result</h2>
     * Interface for loading content
     */

    public interface OnFillResult
    {
        void result(boolean success);
    }

    /**
     * <h2>On Create View</h2>
     * Connecting the Fragment with the ViewModel and inflating its layout view
     * Setting up the click events
     * Observing the MutableLiveData in the viewModel
     * Handling swipe refreshes
     */

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        binding.setLifecycleOwner(this);
        binding.setVm(viewModel);

        if (getActivity() != null)
        {
            fragmentManager = getActivity().getSupportFragmentManager();
            binding.swipeRefresh.setColorSchemeColors(ContextCompat.getColor(getActivity(), R.color.colorPrimary), ContextCompat.getColor(getActivity(), R.color.colorPrimary), ContextCompat.getColor(getActivity(), R.color.colorPrimary));
        }

        viewModel.getLast().observe(getViewLifecycleOwner(), l ->
        {
            if (l != null && l.size() > 0 && getContext() != null)
            {
                if (!lastLoaded)
                {
                    lastLoaded = true;
                    binding.last.setItemViewCacheSize(20);
                    binding.last.setDrawingCacheEnabled(true);
                    binding.last.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
                    binding.last.addItemDecoration(new MarginDecorator("LeftLastRight", func.pxFromDp(getContext(), getResources().getInteger(R.integer.default_margin))));
                    LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
                    binding.last.setLayoutManager(layoutManager);
                }

                lastAdapter = new SoundHorizontalAdapter(l);
                binding.last.setAdapter(lastAdapter);
            }
        });

        viewModel.getFavorites().observe(getViewLifecycleOwner(), l ->
        {
            if (l != null && l.size() > 0 && getContext() != null)
            {
                if (!favoritesLoaded)
                {
                    favoritesLoaded = true;
                    binding.favorites.setItemViewCacheSize(20);
                    binding.favorites.setDrawingCacheEnabled(true);
                    binding.favorites.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
                    binding.favorites.addItemDecoration(new MarginDecorator("LeftLastRight", func.pxFromDp(getContext(), getResources().getInteger(R.integer.default_margin))));
                    LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
                    binding.favorites.setLayoutManager(layoutManager);
                }

                favoritesAdapter = new SoundHorizontalAdapter(l);
                binding.favorites.setAdapter(favoritesAdapter);
            }
        });

        binding.ratingOne.setOnClickListener(v -> makeRating(1));
        binding.ratingTwo.setOnClickListener(v -> makeRating(2));
        binding.ratingThree.setOnClickListener(v -> makeRating(3));
        binding.ratingFour.setOnClickListener(v -> makeRating(4));
        binding.ratingFive.setOnClickListener(v -> makeRating(5));

        binding.currentProgram.setOnClickListener(v ->
        {
            if (viewModel.currentProgram.get() != null)
            {
                openProgram(Objects.requireNonNull(viewModel.currentProgram.get()));
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

        ItemClickSupport.addTo(binding.last)
                .setOnItemClickListener((recyclerView, position, v) ->
                {
                    if (lastAdapter != null)
                    {
                        Sound s = lastAdapter.getItem(position);
                        if (s != null)
                        {
                            openSound(s, v);
                        }
                    }
                });

        ItemClickSupport.addTo(binding.favorites)
                .setOnItemClickListener((recyclerView, position, v) ->
                {
                    if (favoritesAdapter != null)
                    {
                        Sound s = favoritesAdapter.getItem(position);
                        if (s != null)
                        {
                            openSound(s, v);
                        }
                    }
                });

        return binding.getRoot();
    }

    /**
     * <h2>Open Program</h2>
     * Opens the selected program in the Program Activity
     *
     * @param p the selected program
     *
     */

    private void openProgram (Program p)
    {
        if (getActivity() != null)
        {
            Intent intent = new Intent(getActivity(), com.example.tinni.ui.program.Program.class);
            intent.putExtra("program", p.getId());
            ImageView iv = binding.programImg;

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
    }

    /**
     * <h2>Make Rating</h2>
     * Opens the rating view in a bottom dialog
     *
     * @param rating The selected rating
     *
     */

    private void makeRating (int rating)
    {
        if (fragmentManager != null)
        {
            BottomDialogDailyRating bottomDialogDailyRating = new BottomDialogDailyRating();
            bottomDialogDailyRating.newInstance(viewModel, rating);
            bottomDialogDailyRating.show(fragmentManager, "dailyrating");
        }
    }

    /**
     * <h2>Open Sound</h2>
     * Opens the selected sound in the Sound Activity
     *
     * @param s the selected sound
     * @param v the selected view
     *
     */

    private void openSound (Sound s, View v)
    {
        Intent intent = new Intent(getActivity(), com.example.tinni.ui.sound.Sound.class);
        intent.putExtra("sound", s.getId());
        ImageView iv = v.findViewById(R.id.soundImg);
        if (iv != null)
        {
            iv.setTransitionName("sound" + s.getId());
            if (s.getBitmap() != null && getActivity() != null)
            {
                intent.putExtra("sound_transition_name", iv.getTransitionName());
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

    /**
     * <h2>Scroll To Top</h2>
     * Scrolls the NestedScrollView to the top
     * Triggered by click on the current navigation item
     */

    public void scrollToTop ()
    {
        NestedScrollView nestedScrollView = binding.scroll;
        nestedScrollView.scrollTo(0, 0);
    }

    /**
     * <h2>On Resume</h2>
     * Calls the fill() method in the viewModel if it hasn't already done so
     * Using a handler to give it room to breathe on startup
     */

    @Override
    public void onResume()
    {
        super.onResume();
        if (!loaded)
        {
            loaded = true;
            Handler handler = new Handler();
            handler.postDelayed(() ->
            {
                viewModel.fill(success -> {});
                handler.removeCallbacksAndMessages(null);
            }, getResources().getInteger(R.integer.start_delay));
        }
    }
}
