package com.mnesa.backend.modules.intake.service;

import com.mnesa.backend.modules.intake.domain.IntakeJob;
import com.mnesa.backend.modules.intake.domain.IntakeJobStatus;
import com.mnesa.backend.modules.intake.repository.IntakeJobRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Component
public class IntakeJobScheduler {

    private static final Logger log = LoggerFactory.getLogger(IntakeJobScheduler.class);
    private static final Duration STALE_JOB_THRESHOLD = Duration.ofMinutes(5);

    private final IntakeJobProcessor processor;
    private final IntakeJobRepository intakeJobRepository;

    public IntakeJobScheduler(IntakeJobProcessor processor, IntakeJobRepository intakeJobRepository) {
        this.processor = processor;
        this.intakeJobRepository = intakeJobRepository;
    }

    /**
     * Polls and processes pending intake jobs asynchronously every 3 seconds.
     */
    @Scheduled(fixedDelay = 3000)
    public void processPendingJobs() {
        int processedCount = 0;
        while (processedCount < 5) {
            boolean processed = processor.processNextPendingJob();
            if (!processed) {
                break;
            }
            processedCount++;
        }
    }

    /**
     * Recovers jobs that became stuck in PROCESSING state due to unexpected worker termination.
     */
    @Scheduled(fixedDelay = 60000)
    public void recoverStaleProcessingJobs() {
        Instant cutoff = Instant.now().minus(STALE_JOB_THRESHOLD);
        List<IntakeJob> staleJobs = intakeJobRepository.findByStatusAndUpdatedAtBefore(IntakeJobStatus.PROCESSING, cutoff);

        for (IntakeJob job : staleJobs) {
            log.warn("Detected stale intake job [{}] in PROCESSING status since [{}]. Resetting to PENDING.",
                    job.getId(), job.getUpdatedAt());
            if (job.getAttemptCount() >= 3) {
                job.setStatus(IntakeJobStatus.FAILED);
                job.setErrorMessage("Processing timed out and exceeded maximum retries");
            } else {
                job.setStatus(IntakeJobStatus.PENDING);
            }
            intakeJobRepository.save(job);
        }
    }
}
