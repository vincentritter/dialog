package com.dialogapp.dialog.di.viewmodule;

import com.dialogapp.dialog.ui.common.ListFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
abstract class ListFragmentModule {
    @ContributesAndroidInjector
    abstract ListFragment contributesListFragment();
}