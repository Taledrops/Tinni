package com.example.tinni.ui.home;

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
import com.example.tinni.databinding.FragmentHomeBinding;

import java.lang.reflect.Field;

/**
 * <h1>Home Fragment</h1>
 * Everything related to the home ui
 *
 * Variables:
 * FragmentHomeBinding binding: The binding to the layout file
 * HomeViewModel viewModel: The ViewModel to this page
 *
 * @author Nassim Amar
 * @version 1.0
 * @since   18.06.2020
 */

public class HomeFragment extends Fragment
{
    private FragmentHomeBinding binding = null;
    private HomeViewModel viewModel;

    /**
     * <h2>On Create View</h2>
     * Connecting the Fragment with the ViewModel and inflating its layout view
     */

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        binding.setLifecycleOwner(this);
        binding.setVm(viewModel);

        System.out.println("##### BUILD ME HOME");

        return binding.getRoot();
    }
}
