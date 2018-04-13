package com.dialogapp.dialog.ui.common;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;

import com.dialogapp.dialog.TestUtil;
import com.dialogapp.dialog.model.Item;
import com.dialogapp.dialog.model.MicroBlogResponse;
import com.dialogapp.dialog.repository.PostsRepository;
import com.dialogapp.dialog.ui.base.BaseListViewModel;
import com.dialogapp.dialog.util.Resource;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class ListViewModelTest {
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private ListViewModel viewModel;
    private PostsRepository postsRepository;

    @Before
    public void setUp() {
        postsRepository = mock(PostsRepository.class);
        viewModel = new ListViewModel(postsRepository);
    }

    @Test
    public void testLoading() {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        viewModel.getPosts().observeForever(mock(Observer.class));

        viewModel.setView(BaseListViewModel.TIMELINE, null);
        verify(postsRepository).loadTimeline();

        viewModel.setView(BaseListViewModel.MENTIONS, null);
        verify(postsRepository).loadMentions();

        viewModel.setView(BaseListViewModel.FAVORITES, null);
        verify(postsRepository).loadMentions();

        viewModel.setView(BaseListViewModel.DISCOVER, null);
        verify(postsRepository).loadDiscover(captor.capture());
        assertThat(captor.getValue(), is(nullValue()));

        viewModel.setView(BaseListViewModel.DISCOVER, "abc");
        verify(postsRepository, times(2)).loadDiscover(captor.capture());
        assertThat(captor.getValue(), is("abc"));

        viewModel.setView(BaseListViewModel.CONVERSATION, "123");
        verify(postsRepository).loadConversation(captor.capture());
        assertThat(captor.getValue(), is("123"));
    }

    @Test
    public void testRefresh() {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        viewModel.getPosts().observeForever(mock(Observer.class));
        verifyNoMoreInteractions(postsRepository);

        viewModel.setView(BaseListViewModel.TIMELINE, null);
        viewModel.refresh();
        verify(postsRepository, times(2)).loadTimeline();

        viewModel.setView(BaseListViewModel.MENTIONS, null);
        viewModel.refresh();
        verify(postsRepository, times(2)).loadMentions();

        viewModel.setView(BaseListViewModel.FAVORITES, null);
        viewModel.refresh();
        verify(postsRepository, times(2)).loadFavorites();

        viewModel.setView(BaseListViewModel.DISCOVER, null);
        viewModel.refresh();
        verify(postsRepository, times(2)).loadDiscover(captor.capture());
        assertThat(captor.getValue(), is(nullValue()));

        viewModel.setView(BaseListViewModel.DISCOVER, "abc");
        viewModel.refresh();
        verify(postsRepository, times(4)).loadDiscover(captor.capture());
        assertThat(captor.getValue(), is("abc"));

        viewModel.setView(BaseListViewModel.CONVERSATION, "123");
        viewModel.refresh();
        verify(postsRepository, times(2)).loadConversation(captor.capture());
        assertThat(captor.getValue(), is("123"));
    }

    @Test
    public void sendResultToUI() throws IOException {
        MutableLiveData<Resource<List<Item>>> data = new MutableLiveData<>();
        MicroBlogResponse response = TestUtil.readFromJson(getClass().getClassLoader(), "response.json");
        Resource<List<Item>> listResource = Resource.success(response.items);
        when(postsRepository.loadFavorites()).thenReturn(data);
        when(postsRepository.loadMentions()).thenReturn(data);
        when(postsRepository.loadTimeline()).thenReturn(data);
        when(postsRepository.loadDiscover(null)).thenReturn(data);
        when(postsRepository.loadConversation("123")).thenReturn(data);

        Observer<Resource<List<Item>>> observer = mock(Observer.class);
        viewModel.getPosts().observeForever(observer);
        viewModel.setView(BaseListViewModel.TIMELINE, null);
        verify(observer, never()).onChanged(any(Resource.class));

        data.setValue(listResource);
        verify(observer).onChanged(listResource);
        reset(observer);

        data.setValue(listResource);
        viewModel.setView(BaseListViewModel.MENTIONS, null);
        verify(observer).onChanged(listResource);
        reset(observer);

        data.setValue(listResource);
        viewModel.setView(BaseListViewModel.FAVORITES, null);
        verify(observer).onChanged(listResource);
        reset(observer);

        data.setValue(listResource);
        viewModel.setView(BaseListViewModel.DISCOVER, null);
        verify(observer).onChanged(listResource);
        reset(observer);

        data.setValue(listResource);
        viewModel.setView(BaseListViewModel.CONVERSATION, null);
        verify(observer).onChanged(listResource);
    }
}
