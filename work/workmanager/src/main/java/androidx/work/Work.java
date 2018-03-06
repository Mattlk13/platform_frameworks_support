/*
 * Copyright 2018 The Android Open Source Project
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

package androidx.work;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.work.impl.WorkImpl;

/**
 * A class to execute a logical unit of non-repeating work.
 */

public abstract class Work implements BaseWork {

    /**
     * Creates an array of {@link Work} with defaults from an array of {@link Worker} class names.
     *
     * @param workerClasses An array of {@link Worker} class names
     * @return A list of {@link Work} constructed by using defaults in the {@link Builder}
     */
    @SafeVarargs public static @NonNull List<Work> from(
            @NonNull Class<? extends Worker>... workerClasses) {
        return from(Arrays.asList(workerClasses));
    }

    /**
     * Creates a list of {@link Work} with defaults from an array of {@link Worker} class names.
     *
     * @param workerClasses A list of {@link Worker} class names
     * @return A list of {@link Work} constructed by using defaults in the {@link Builder}
     */
    public static @NonNull List<Work> from(
            @NonNull List<Class<? extends Worker>> workerClasses) {
        List<Work> workList = new ArrayList<>(workerClasses.size());
        for (Class<? extends Worker> workerClass : workerClasses) {
            workList.add(new Work.Builder(workerClass).build());
        }
        return workList;
    }

    /**
     * Builder for {@link Work} class.
     */
    public static class Builder implements WorkBuilder<Work, Builder> {

        private WorkImpl.Builder mInternalBuilder;

        /**
         * Creates a {@link Work} that runs once.
         *
         * @param workerClass The {@link Worker} class to run with this job
         */
        public Builder(@NonNull Class<? extends Worker> workerClass) {
            mInternalBuilder = new WorkImpl.Builder(workerClass);
        }

        /**
         * Specify whether {@link Work} should run with an initial delay. Default is 0ms.
         *
         * @param duration initial delay before running WorkSpec in {@code timeUnit} units
         * @param timeUnit The {@link TimeUnit} for {@code duration}
         * @return The current {@link Builder}
         */
        @Override
        public Builder withInitialDelay(long duration, @NonNull TimeUnit timeUnit) {
            mInternalBuilder.withInitialDelay(duration, timeUnit);
            return this;
        }

        /**
         * Specify an {@link InputMerger}.  The default is {@link OverwritingInputMerger}.
         *
         * @param inputMerger The class name of the {@link InputMerger} to use for this {@link Work}
         * @return The current {@link Builder}
         */
        @Override
        public Builder withInputMerger(@NonNull Class<? extends InputMerger> inputMerger) {
            mInternalBuilder.withInputMerger(inputMerger);
            return this;
        }

        @Override
        public Builder withBackoffCriteria(
                @NonNull BackoffPolicy backoffPolicy,
                long backoffDelay,
                @NonNull TimeUnit timeUnit) {
            mInternalBuilder.withBackoffCriteria(backoffPolicy, backoffDelay, timeUnit);
            return this;
        }

        @Override
        public Builder withConstraints(@NonNull Constraints constraints) {
            mInternalBuilder.withConstraints(constraints);
            return this;
        }

        @Override
        public Builder withArguments(@NonNull Arguments arguments) {
            mInternalBuilder.withArguments(arguments);
            return this;
        }

        @Override
        public Builder addTag(@NonNull String tag) {
            mInternalBuilder.addTag(tag);
            return this;
        }

        @Override
        public Builder keepResultsForAtLeast(long duration, @NonNull TimeUnit timeUnit) {
            mInternalBuilder.keepResultsForAtLeast(duration, timeUnit);
            return this;
        }

        @Override
        public Work build() {
            return mInternalBuilder.build();
        }

        @VisibleForTesting
        @Override
        public Builder withInitialState(@NonNull State state) {
            mInternalBuilder.withInitialState(state);
            return this;
        }

        @VisibleForTesting
        @Override
        public Builder withInitialRunAttemptCount(int runAttemptCount) {
            mInternalBuilder.withInitialRunAttemptCount(runAttemptCount);
            return this;
        }

        @VisibleForTesting
        @Override
        public Builder withPeriodStartTime(long periodStartTime, @NonNull TimeUnit timeUnit) {
            mInternalBuilder.withPeriodStartTime(periodStartTime, timeUnit);
            return this;
        }
    }
}
