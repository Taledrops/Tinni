package com.example.tinni.ui.stats;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.tinni.R;
import com.example.tinni.databinding.FragmentStatsBinding;

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

        System.out.println("##### BUILD ME STATS");

        return binding.getRoot();
    }
}
