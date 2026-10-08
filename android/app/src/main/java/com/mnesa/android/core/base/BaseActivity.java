package com.mnesa.android.core.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewbinding.ViewBinding;

/**
 * Base Activity managing ViewBinding lifecycle.
 *
 * @param <VB> ViewBinding type
 */
public abstract class BaseActivity<VB extends ViewBinding> extends AppCompatActivity {

    protected VB binding;

    protected abstract VB inflateBinding(LayoutInflater inflater);

    protected abstract void initViews();

    protected abstract void observeViewModel();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = inflateBinding(getLayoutInflater());
        setContentView(binding.getRoot());

        initViews();
        observeViewModel();
    }
}
