package com.ceilfors.jenkins.plugins.jiratrigger.integration

import com.atlassian.jira.rest.client.api.domain.Issue
import com.ceilfors.jenkins.plugins.jiratrigger.JiraTriggerExecutor
import com.ceilfors.jenkins.plugins.jiratrigger.JiraTriggerListener
import hudson.model.AbstractProject
import jenkins.model.Jenkins

import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * @author ceilfors
 */
class JenkinsBlockingQueue {

    private final BlockingQueue<Collection> scheduledJobsQueue = new ArrayBlockingQueue<>(1)
    private final CountDownLatch countDownLatch = new CountDownLatch(1)
    private long timeout = 5

    JenkinsBlockingQueue(Jenkins jenkins) {
        def jiraTriggerExecutor = jenkins.injector.getInstance(JiraTriggerExecutor)
        jiraTriggerExecutor.addJiraTriggerListener(new JiraTriggerListener() {

            @Override
            void buildScheduled(Issue issue, Collection<? extends AbstractProject> jobs) {
                scheduledJobsQueue.offer(jobs, timeout, TimeUnit.SECONDS)
                countDownLatch.countDown()
            }

            @Override
            void buildNotScheduled(Issue issue) {
                scheduledJobsQueue.offer([], timeout, TimeUnit.SECONDS)
                countDownLatch.countDown()
            }
        })
    }

    void setTimeout(long timeout) {
        this.timeout = timeout
    }

    Collection<? extends AbstractProject> getScheduledJobs() {
        scheduledJobsQueue.poll(timeout, TimeUnit.SECONDS)
    }

    boolean isAnyJobScheduled() {
        countDownLatch.await(timeout, TimeUnit.SECONDS)
        Collection scheduledJobs = scheduledJobsQueue.peek()
        scheduledJobs != null && !scheduledJobs.isEmpty()
    }
}
