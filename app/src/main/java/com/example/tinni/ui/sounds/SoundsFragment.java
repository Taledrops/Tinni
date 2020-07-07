package com.example.tinni.ui.sounds;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.tinni.R;
import com.example.tinni.adapters.CategoryAdapter;
import com.example.tinni.adapters.SoundAdapter;
import com.example.tinni.databinding.FragmentSoundsBinding;
import com.example.tinni.helpers.Constants;
import com.example.tinni.helpers.Functions;
import com.example.tinni.helpers.ItemClickSupport;
import com.example.tinni.helpers.MarginDecorator;
import com.example.tinni.models.Category;
import com.example.tinni.models.Sound;
import com.example.tinni.ui.add.Add;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <h1>Sounds Fragment</h1>
 * Everything related to the sounds ui
 *
 * Variables:
 * FragmentSoundsBinding binding: The binding to the layout file
 * SoundsViewModel viewModel: The ViewModel to this page
 * boolean loaded: Indicator if categories and sounds were already loaded
 * CategoryAdapter categoryAdapter: The adapter for the category items
 * SoundAdapter soundAdapter: The adapter for the sound items
 * Functions func: A static instance of the helper class "Functions"
 *
 * @author Nassim Amar
 * @version 1.0
 * @since   18.06.2020
 */

public class SoundsFragment extends Fragment
{
    private FragmentSoundsBinding binding = null;
    private SoundsViewModel viewModel;
    private static boolean loaded = false;

    private CategoryAdapter categoryAdapter;
    private SoundAdapter soundAdapter;

    private static final Functions func = new Functions();

    /**
     * <h2>On Create View</h2>
     * Connecting the Fragment with the ViewModel and inflating its layout view
     * Observing category and sound items on SoundsViewModel and update ui
     */

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        viewModel = new ViewModelProvider(this).get(SoundsViewModel.class);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sounds, container, false);
        binding.setLifecycleOwner(this);
        binding.setVm(viewModel);

        viewModel.getCategories().observe(getViewLifecycleOwner(), result ->
        {
            if (getContext() != null && result != null)
            {
                binding.categories.setItemViewCacheSize(getResources().getInteger(R.integer.item_view_cache_size));
                binding.categories.setDrawingCacheEnabled(true);
                binding.categories.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
                binding.categories.addItemDecoration(new MarginDecorator("LeftLastRight", func.pxFromDp(getContext(), getResources().getInteger(R.integer.default_margin))));
                LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
                binding.categories.setLayoutManager(layoutManager);

                categoryAdapter = new CategoryAdapter(result);
                binding.categories.setAdapter(categoryAdapter);
            }
        });

        viewModel.getSounds().observe(getViewLifecycleOwner(), result ->
        {
            if (getContext() != null && result != null)
            {
                if (soundAdapter == null)
                {
                    binding.sounds.setItemViewCacheSize(getResources().getInteger(R.integer.item_view_cache_size));
                    binding.sounds.setDrawingCacheEnabled(true);
                    binding.sounds.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
                    binding.sounds.addItemDecoration(new MarginDecorator("StaggeredGrid", func.pxFromDp(getContext(), getResources().getInteger(R.integer.default_margin))));
                    binding.sounds.setHasFixedSize(true);
                    StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                    binding.sounds.setLayoutManager(staggeredGridLayoutManager);

                    soundAdapter = new SoundAdapter(result);
                    binding.sounds.setAdapter(soundAdapter);
                }
                else
                {
                    soundAdapter.reloadList(result);
                }
            }
        });

        binding.fab.setOnClickListener(v -> openAdd());

        ItemClickSupport.addTo(binding.categories)
                .setOnItemClickListener((recyclerView, position, v) ->
                {
                    if (categoryAdapter != null && !viewModel.loading.get())
                    {
                        Category c = categoryAdapter.getItem(position);
                        if (c != null)
                        {
                            categoryClick(c);
                        }
                    }
                });

        ItemClickSupport.addTo(binding.sounds)
                .setOnItemClickListener((recyclerView, position, v) ->
                {
                    Sound s = soundAdapter.getItem(position);
                    if (s != null)
                    {
                        openSound(s, v);
                    }
                });

        return binding.getRoot();
    }

    /**
     * <h2>Open Add</h2>
     * Opens the Add Activity
     *
     */

    private void openAdd ()
    {
        startActivity(new Intent(getActivity(), Add.class));
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
     * <h2>Category click</h2>
     * Handles the click on a category
     * Removes category from Shared Preferences if it is inside the SelectedCategories list (in Constants)
     * Adds category to Shared Preferences if it is not inside the SelectedCategories List (in Constants)
     * Triggers the filterSoundsAsyncTask on the viewmodel to filter the results
     *
     * Arguments:
     * Category c: The clicked category
     */

    private void categoryClick (Category c)
    {
        viewModel.loading.set(true);
        if (!c.isReset() && !c.isFav() && !c.isMine())
        {
            c.active.set(Constants.getInstance().selectedCategories.contains(c.getId()));
            if (!Constants.getInstance().selectedCategories.contains(c.getId()))
            {
                c.active.set(true);
                Constants.getInstance().selectedCategories.add(c.getId());

                List<Category> toReset = categoryAdapter.categoryList.stream().filter(x -> x.isReset() || x.isMine() || x.isFav()).collect(Collectors.toList());
                for (Category rc : toReset)
                {
                    rc.active.set(false);
                    Constants.getInstance().selectedCategories.remove(Integer.valueOf(rc.getId()));
                    categoryAdapter.categoryList.stream().filter(x -> x.getId() == rc.getId()).findFirst().ifPresent(resetCategory -> resetCategory.active.set(false));
                }
            }
            else
            {
                c.active.set(false);
                Constants.getInstance().selectedCategories.remove(Integer.valueOf(c.getId()));

                if (Constants.getInstance().selectedCategories.size() == 0)
                {
                    categoryAdapter.categoryList.stream().filter(Category::isReset).findFirst().ifPresent(resetCategory -> resetCategory.active.set(true));
                }
            }
        }
        else
        {
            categoryAdapter.categoryList.forEach(x -> x.active.set(false));
            if (!c.isReset())
            {
                if (!Constants.getInstance().selectedCategories.contains(c.getId()))
                {
                    c.active.set(true);
                    Constants.getInstance().selectedCategories.clear();
                    Constants.getInstance().selectedCategories.add(c.getId());
                }
                else
                {
                    c.active.set(false);
                    Constants.getInstance().selectedCategories.clear();
                    categoryAdapter.categoryList.stream().filter(Category::isReset).findFirst().ifPresent(resetCategory -> resetCategory.active.set(true));
                }
            }
            else
            {
                c.active.set(true);
                Constants.getInstance().selectedCategories.clear();
            }
        }

        viewModel.filterList();

        Constants.getInstance().handleSelectedCategories();
    }

    /**
     * <h2>Scroll To Top</h2>
     * Scrolls the RecyclerView to the top
     * Triggered by click on the current navigation sound_item_horizontal
     */

    public void scrollToTop ()
    {
        binding.sounds.scrollToPosition(0);
        binding.appBar.setExpanded(true, false);
    }

    /**
     * <h2>On Resume</h2>
     * Called when fragment becomes visible
     * Loaded indicator assures that content is only loaded once
     * Calling viewModel.Fill() inside onResume() has the purpose to only load the data once the page is fully loaded
     * Checking for viewModel.getSounds().getValue() == null is a fallback for when the user closes and reopens the app
     * Add recently uploaded sound to the RecyclerView
     */

    @Override
    public void onResume()
    {
        super.onResume();
        if (!loaded || viewModel.getSounds().getValue() == null)
        {
            loaded = true;
            viewModel.fill(new WeakReference<>(getContext()));
        }

        if (Constants.getInstance().soundToAdd != null && soundAdapter != null)
        {
            soundAdapter.addItem(Constants.getInstance().soundToAdd);
            viewModel.manualAdd(Constants.getInstance().soundToAdd);
            Constants.getInstance().soundToAdd = null;
            binding.sounds.scrollToPosition(0);
            Toast.makeText(getContext(), getString(R.string.add_success), Toast.LENGTH_SHORT).show();
        }
        else if (Constants.getInstance().soundToRemove != null && soundAdapter != null)
        {
            Sound s = soundAdapter.soundList.stream().filter(x -> x.getId() == Constants.getInstance().soundToRemove.getId()).findFirst().orElse(null);
            if (s != null)
            {
                soundAdapter.removeItem(s);
                viewModel.manualRemove(s);
                Constants.getInstance().soundToRemove = null;
                Toast.makeText(getContext(), getResources().getString(R.string.delete_success), Toast.LENGTH_SHORT).show();
            }
        }
    }
}