/*
 * Copyright 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package androidx.work.testing

import android.content.Context
import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import androidx.work.ListenableWorker.Result
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.testing.workers.TestListenableWorker
import androidx.work.testing.workers.TestWorker
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.hasItems
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsInAnyOrder
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executor
import java.util.concurrent.Executors

@RunWith(AndroidJUnit4::class)
@SmallTest
class TestWorkerBuilderTest {

    private lateinit var context: Context
    private lateinit var executor: Executor

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        executor = SynchronousExecutor()
    }

    @Test
    @Throws(InterruptedException::class, ExecutionException::class)
    fun testListenableWorkerBuilder_buildsWorker() {
        val request = OneTimeWorkRequestBuilder<TestWorker>().build()
        val worker = TestListenableWorkerBuilder.from(context, request).build()
        val result = worker.startWork().get()
        assertThat(result, `is`(Result.success()))
    }

    @Test
    fun testWorkerBuilder_buildsWorker() {
        val request = OneTimeWorkRequestBuilder<TestWorker>().build()
        val worker = TestWorkerBuilder.from(context, request, executor).build()
        val result = worker.doWork()
        assertThat(result, `is`(Result.success()))
    }

    @Test(expected = IllegalArgumentException::class)
    fun testWorkerBuilder_invalidWorker() {
        val request = OneTimeWorkRequestBuilder<TestListenableWorker>().build()
        TestWorkerBuilder.from(context, request, executor).build()
    }

    @Test
    fun testBuilder() {
        val request = OneTimeWorkRequestBuilder<TestWorker>()
            .addTag("test")
            .build()

        val contentUris = arrayOf(Uri.parse("android.test://1"))
        val authorities = arrayOf("android.test")

        val worker = TestListenableWorkerBuilder.from(context, request)
            .setRunAttemptCount(2)
            .setTriggeredContentAuthorities(authorities.toList())
            .setTriggeredContentUris(contentUris.toList())
            .build()

        assertThat(worker.tags, hasItems("test"))
        assertThat(worker.id, `is`(request.id))
        assertThat(worker.runAttemptCount, `is`(2))
        assertThat(worker.triggeredContentAuthorities, containsInAnyOrder(*authorities))
        assertThat(worker.triggeredContentUris, containsInAnyOrder(*contentUris))
    }

    @Test
    fun testWorkerBuilder_usesSingleThreadedExecutor() {
        val request = OneTimeWorkRequestBuilder<TestWorker>().build()
        val singleThreadedExecutor = Executors.newSingleThreadExecutor()
        val worker = TestWorkerBuilder.from(context, request, singleThreadedExecutor).build()
        val result = worker.doWork()
        assertThat(result, `is`(Result.success()))
    }
}