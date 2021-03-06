/*
 * Copyright 2020 StreamThoughts.
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.streamthoughts.kafka.clients.consumer

import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.OffsetAndMetadata
import org.apache.kafka.common.TopicPartition
import java.io.Closeable
import java.time.Duration

interface ConsumerTask: Closeable {

    enum class State {
        /**
         * The [ConsumerTask] is created.
         */
        CREATED,
        /**
         * The [ConsumerTask] is starting.
         */
        STARTING,
        /**
         * The [ConsumerTask]
         */
        RUNNING,
        /**
         * The [ConsumerTask] is paused for all assigned partitions.
         */
        PAUSED,
        /**
         * The [ConsumerTask] is rebalancing and new partitions are being assigned.
         */
        PARTITIONS_ASSIGNED,
        /**
         * The [ConsumerTask] is rebalancing and partitions are being revoked.
         */
        PARTITIONS_REVOKED,
        /**
         * The [ConsumerTask] is being closed.
         */
        PENDING_SHUTDOWN,
        /**
         * The [ConsumerTask] is closed.
         */
        SHUTDOWN
    }

    suspend fun run()

    /**
     * Pauses consumption for the current assignments.
     * @see org.apache.kafka.clients.consumer.Consumer.pause
     */
    fun pause()

    /**
     * Resumes consumption for the current assignments.
     * @see org.apache.kafka.clients.consumer.Consumer.pause
     */
    fun resume()

    /**
     * Shutdowns the [ConsumerTask] and wait for completion.
     * @see org.apache.kafka.clients.consumer.Consumer.close
     */
    override fun close()

    /**
     * Shutdowns the [ConsumerTask] and wait for completion until the given [timeout].
     * @see org.apache.kafka.clients.consumer.Consumer.close
     */
    fun close(timeout: Duration)

    /**
     * @return the [State] of this [ConsumerTask].
     */
    fun state(): State

    /**
     * Executes the given [action] with the underlying [Consumer].
     */
    fun <T> execute(action: (consumer: Consumer<ByteArray, ByteArray>) -> T): T

    /**
     * Commits asynchronously the positions of the internal [Consumer] for the given [offsets].
     * If passed [offsets] is {@code null} then commit the [Consumer] positions for its current partition assignments.
     *
     * @see [Consumer.commitAsync]
     */
    fun commitAsync(offsets: Map<TopicPartition, OffsetAndMetadata>? = null)

    /**
     * Commits synchronously the positions of the internal [Consumer] for the given offsets.
     * If passed [offsets] is {@code null} then commit the [Consumer] positions for its current partition assignments.
     *
     * @see [Consumer.commitAsync]
     */
    fun commitSync(offsets: Map<TopicPartition, OffsetAndMetadata>? = null)
}