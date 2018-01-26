package com.dialogapp.dialog.di;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.dialogapp.dialog.ui.loginscreen.LoginViewModel;
import com.dialogapp.dialog.ui.mainscreen.mentions.MentionsViewModel;
import com.dialogapp.dialog.ui.mainscreen.timeline.TimelineViewModel;
import com.dialogapp.dialog.util.MicroblogViewModelFactory;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
abstract class ViewModelModule {
    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(MicroblogViewModelFactory factory);

    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel.class)
    abstract ViewModel bindLoginViewModel(LoginViewModel loginViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(TimelineViewModel.class)
    abstract ViewModel bindTimelineViewModel(TimelineViewModel timelineViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(MentionsViewModel.class)
    abstract ViewModel bindMentionsViewModel(MentionsViewModel mentionsViewModel);
}
