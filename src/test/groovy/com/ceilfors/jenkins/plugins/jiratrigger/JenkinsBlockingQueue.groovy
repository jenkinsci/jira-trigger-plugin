package com.ceilfors.jenkins.plugins.jiratrigger
import com.atlassian.jira.rest.client.api.domain.ChangelogGroup
import com.atlassian.jira.rest.client.api.domain.Comment
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

    private BlockingQueue scheduledItemBlockingQueue = new ArrayBlockingQueue<>(1)
    private CountDownLatch countDownLatch = new CountDownLatch(1)
    private long timeout = 5

    /** Can't extend from Queue.Item due to package access methods. */
    private class NullItem {}

    public JenkinsBlockingQueue(Jenkins jenkins) {
        def jiraTriggerExecutor = jenkins.getInjector().getInstance(JiraTriggerExecutor)
        jiraTriggerExecutor.addJiraTriggerListener(new JiraTriggerListener() {
            @Override
            void buildScheduled(Comment comment, Collection<? extends AbstractProject> projects) {
                scheduledItemBlockingQueue.offer(jenkins.queue.getItems().last(), timeout, TimeUnit.SECONDS)
                countDownLatch.countDown()
            }

            @Override
            void buildScheduled(ChangelogGroup changelog, Collection<? extends AbstractProject> projects) {
                scheduledItemBlockingQueue.offer(jenkins.queue.getItems().last(), timeout, TimeUnit.SECONDS)
                countDownLatch.countDown()
            }

            @Override
            void buildNotScheduled(Comment comment) {
                scheduledItemBlockingQueue.offer(new NullItem(), timeout, TimeUnit.SECONDS)
                countDownLatch.countDown()
            }

            @Override
            void buildNotScheduled(ChangelogGroup changelogGroup) {
                scheduledItemBlockingQueue.offer(new NullItem(), timeout, TimeUnit.SECONDS)
                countDownLatch.countDown()
            }
        });
    }

    void setTimeout(long timeout) {
        this.timeout = timeout
    }

    Queue.Item getScheduledItem() {
        def scheduledItem = scheduledItemBlockingQueue.poll(timeout, TimeUnit.SECONDS)
        return scheduledItem instanceof NullItem ? null : scheduledItem as Queue.Item
    }

    boolean isItemScheduled() {
        countDownLatch.await(timeout, TimeUnit.SECONDS)
        def scheduledItem = scheduledItemBlockingQueue.peek()
        return scheduledItem instanceof NullItem ? null : scheduledItem as Queue.Item
    }
}
