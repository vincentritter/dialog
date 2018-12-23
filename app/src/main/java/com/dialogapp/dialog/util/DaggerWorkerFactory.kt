package com.dialogapp.dialog.util

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.dialogapp.dialog.api.MicroblogService
import com.dialogapp.dialog.db.InMemoryDb
import com.dialogapp.dialog.db.MicroBlogDb
import com.dialogapp.dialog.workers.FavoriteWorker

class DaggerWorkerFactory(private val microblogService: MicroblogService,
                          private val diskDb: MicroBlogDb,
                          private val inMemDb: InMemoryDb) : WorkerFactory() {

    override fun createWorker(appContext: Context, workerClassName: String,
                              workerParameters: WorkerParameters): ListenableWorker? {

        val workerKlass = Class.forName(workerClassName).asSubclass(CoroutineWorker::class.java)
        val constructor = workerKlass.getDeclaredConstructor(Context::class.java, WorkerParameters::class.java)
        val instance = constructor.newInstance(appContext, workerParameters)

        when (instance) {
            is FavoriteWorker -> {
                instance.microblogService = microblogService
                instance.diskDb = diskDb
                instance.inMemDb = inMemDb
            }
        }

        return instance
    }
}