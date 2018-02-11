package com.dialogapp.dialog.ui.mainscreen.timeline;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;

import com.dialogapp.dialog.model.Item;
import com.dialogapp.dialog.repository.PostsRepository;
import com.dialogapp.dialog.util.AbsentLiveData;
import com.dialogapp.dialog.util.Resource;

import java.util.List;

import javax.inject.Inject;

public class TimelineViewModel extends ViewModel {

    private final MutableLiveData<Boolean> refresh;

    private final LiveData<Resource<List<Item>>> timelinePosts;

    @Inject
    public TimelineViewModel(PostsRepository postsRepository) {
        refresh = new MutableLiveData<>();
        timelinePosts = Transformations.switchMap(refresh, input -> {
            if (input)
                return postsRepository.loadTimeline(input);
            else
                return AbsentLiveData.create();
        });
    }

    public LiveData<Resource<List<Item>>> getTimelinePosts() {
        return timelinePosts;
    }

    public void refresh() {
        refresh.setValue(true);
    }
}
