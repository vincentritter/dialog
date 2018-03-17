package com.dialogapp.dialog.repository;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.dialogapp.dialog.AppExecutors;
import com.dialogapp.dialog.api.ApiResponse;
import com.dialogapp.dialog.api.MicroblogService;
import com.dialogapp.dialog.db.PostsDao;
import com.dialogapp.dialog.model.Item;
import com.dialogapp.dialog.util.Resource;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import timber.log.Timber;

@Singleton
public class PostsRepository {

    private final long THRESHOLDMILLIS = 900000; // 15 mins

    private final AppExecutors appExecutors;
    private final MicroblogService microblogService;
    private final PostsDao postsDao;

    private String timelineTopPostId;
    private String mentionsTopPostId;

    private long lastTimelineRequestTimestamp;
    private long lastMentionsRequestTimestamp;

    @Inject
    public PostsRepository(AppExecutors appExecutors, PostsDao postsDao, MicroblogService microblogService) {
        this.appExecutors = appExecutors;
        this.postsDao = postsDao;
        this.microblogService = microblogService;
    }

    public LiveData<Resource<List<Item>>> loadTimeline(boolean refresh) {
        return new NetworkBoundResource<List<Item>, List<Item>>(appExecutors) {
            @Override
            protected boolean shouldFetch(@Nullable List<Item> dbData) {
                if (dbData != null && !dbData.isEmpty()) {
                    timelineTopPostId = Long.toString(dbData.get(0).id);
                }

                return refresh || shouldRefresh(lastTimelineRequestTimestamp) || dbData == null || dbData.isEmpty();
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<Item>>> createCall() {
                Timber.i("Timeline top ID: %s", timelineTopPostId);
                return microblogService.getTimeLine(timelineTopPostId);
            }

            @Override
            protected void saveCallResult(@NonNull List<Item> items) {
                lastTimelineRequestTimestamp = System.currentTimeMillis();
                for (Item item : items) {
                    item.setEndpoint(Endpoints.TIMELINE);
                }

                postsDao.insertPosts(items);
            }

            @NonNull
            @Override
            protected LiveData<List<Item>> loadFromDb() {
                return postsDao.loadEndpoint(Endpoints.TIMELINE);
            }
        }.asLiveData();
    }

    public LiveData<Resource<List<Item>>> loadMentions(boolean refresh) {
        return new NetworkBoundResource<List<Item>, List<Item>>(appExecutors) {
            @Override
            protected boolean shouldFetch(@Nullable List<Item> dbData) {
                if (dbData != null && !dbData.isEmpty()) {
                    mentionsTopPostId = Long.toString(dbData.get(0).id);
                }

                return refresh || shouldRefresh(lastMentionsRequestTimestamp) || dbData == null || dbData.isEmpty();
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<Item>>> createCall() {
                Timber.i("Mentions top ID: %s", mentionsTopPostId);
                return microblogService.getMentions(mentionsTopPostId);
            }

            @Override
            protected void saveCallResult(@NonNull List<Item> items) {
                lastMentionsRequestTimestamp = System.currentTimeMillis();
                for (Item item : items) {
                    item.setEndpoint(Endpoints.MENTIONS);
                }

                postsDao.insertPosts(items);
            }

            @NonNull
            @Override
            protected LiveData<List<Item>> loadFromDb() {
                return postsDao.loadEndpoint(Endpoints.MENTIONS);
            }
        }.asLiveData();
    }

    public LiveData<Resource<List<Item>>> loadFavorites(boolean refresh) {
        return new NetworkBoundResource<List<Item>, List<Item>>(appExecutors) {
            @Override
            protected boolean shouldFetch(@Nullable List<Item> dbData) {
                return dbData == null || dbData.isEmpty();
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<Item>>> createCall() {
                return microblogService.getFavorites();
            }

            @Override
            protected void saveCallResult(@NonNull List<Item> items) {
                for (Item item : items) {
                    item.setEndpoint(Endpoints.FAVORITES);
                }

                if (refresh)
                    postsDao.deletePosts(Endpoints.FAVORITES);
                postsDao.insertPosts(items);
            }

            @NonNull
            @Override
            protected LiveData<List<Item>> loadFromDb() {
                return postsDao.loadEndpoint(Endpoints.FAVORITES);
            }
        }.asLiveData();
    }

    public LiveData<Resource<List<Item>>> loadPostsByUsername(String username) {
        return new NetworkBoundResource<List<Item>, List<Item>>(appExecutors) {
            @Override
            protected boolean shouldFetch(@Nullable List<Item> dbData) {
                return dbData == null || dbData.isEmpty();
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<Item>>> createCall() {
                return microblogService.getPostsByUsername(username);
            }

            @Override
            protected void saveCallResult(@NonNull List<Item> items) {
                for (Item item : items) {
                    item.setEndpoint(username);
                }

                postsDao.deletePosts(username);
                postsDao.insertPosts(items);
            }

            @NonNull
            @Override
            protected LiveData<List<Item>> loadFromDb() {
                return postsDao.loadEndpoint(username);
            }
        }.asLiveData();
    }

    private boolean shouldRefresh(long lastRequestTimestamp) {
        return (System.currentTimeMillis() - lastRequestTimestamp) >= THRESHOLDMILLIS;
    }
}
