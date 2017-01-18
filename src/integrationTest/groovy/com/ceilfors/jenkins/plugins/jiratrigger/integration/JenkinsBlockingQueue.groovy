package com.ceilfors.jenkins.plugins.jiratrigger.integration

import com.atlassian.jira.rest.client.api.domain.Issue
import com.ceilfors.jenkins.plugins.jiratrigger.JiraTriggerExecutor
import com.ceilfors.jenkins.plugins.jiratrigger.JiraTriggerListener
import hudson.model.AbstractProject
import hudson.model.Queue
import jenkins.model.Jenkins

import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * @author ceilfors
 */
class JenkinsBlockingQueue {

    private final BlockingQueue scheduledItemBlockingQueue = new ArrayBlockingQueue<>(1)
    private final CountDownLatch countDownLatch = new CountDownLatch(1)
    private long timeout = 5

    /** Can't extend from Queue.Item due to package access methods. */
    private class NullItem {
    }

    JenkinsBlockingQueue(Jenkins jenkins) {
        def jiraTriggerExecutor = jenkins.injector.getInstance(JiraTriggerExecutor)
        jiraTriggerExecutor.addJiraTriggerListener(new JiraTriggerListener() {

            @Override
            void buildScheduled(Issue issue, Collection<? extends AbstractProject> projects) {
                scheduledItemBlockingQueue.offer(jenkins.queue.items.last(), timeout, TimeUnit.SECONDS)
                countDownLatch.countDown()
            }

            @Override
            void buildNotScheduled(Issue issue) {
                scheduledItemBlockingQueue.offer(new NullItem(), timeout, TimeUnit.SECONDS)
                countDownLatch.countDown()
            }
        })
    }

    void setTimeout(long timeout) {
        this.timeout = timeout
    }

    Queue.Item getScheduledItem() {
        def scheduledItem = scheduledItemBlockingQueue.poll(timeout, TimeUnit.SECONDS)
        scheduledItem instanceof NullItem ? null : scheduledItem as Queue.Item
    }

    boolean isItemScheduled() {
        countDownLatch.await(timeout, TimeUnit.SECONDS)
        def scheduledItem = scheduledItemBlockingQueue.peek()
        scheduledItem instanceof NullItem ? false : scheduledItem != null
    }
}
