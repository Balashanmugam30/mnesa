package com.mnesa.android.data.repository;

import com.mnesa.android.data.local.dao.SyncQueueDao;
import com.mnesa.android.data.local.entity.SyncQueueEntity;
import com.mnesa.android.domain.model.SyncOperation;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class SyncRepositoryTest {

    @Mock
    private SyncQueueDao syncQueueDao;

    private SyncRepositoryImpl syncRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        syncRepository = new SyncRepositoryImpl(syncQueueDao);
    }

    @Test
    public void testEnqueueOperation() {
        when(syncQueueDao.enqueue(any(SyncQueueEntity.class))).thenReturn(Completable.complete());

        SyncOperation op = new SyncOperation("op-1", "user-1", "OPPORTUNITY", "opp-123", "CREATE", "{}", 1000L, 0, "PENDING");
        syncRepository.enqueueOperation(op).test().assertComplete();

        verify(syncQueueDao).enqueue(any(SyncQueueEntity.class));
    }

    @Test
    public void testGetPendingOperations() {
        List<SyncQueueEntity> entities = new ArrayList<>();
        entities.add(new SyncQueueEntity("op-1", "user-1", "OPPORTUNITY", "opp-123", "CREATE", "{}", 1000L, 0, null, null, "PENDING", null));

        when(syncQueueDao.getPendingForUser("user-1")).thenReturn(Flowable.just(entities));

        List<SyncOperation> ops = syncRepository.getPendingOperations("user-1").blockingFirst();
        assertEquals(1, ops.size());
        assertEquals("op-1", ops.get(0).getOperationId());
        assertEquals("CREATE", ops.get(0).getOperationType());
    }

    @Test
    public void testMarkSuccessDeletesOperation() {
        when(syncQueueDao.delete("op-1")).thenReturn(Completable.complete());

        syncRepository.markSuccess("op-1").test().assertComplete();
        verify(syncQueueDao).delete("op-1");
    }

    @Test
    public void testPendingCount() {
        when(syncQueueDao.countPendingTotal()).thenReturn(Single.just(3));

        assertEquals(Integer.valueOf(3), syncRepository.getPendingCount().blockingGet());
    }
}
