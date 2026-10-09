package com.mnesa.backend.modules.opportunity.exception;

import com.mnesa.backend.common.exception.MnesaException;
import com.mnesa.backend.modules.opportunity.domain.OpportunityStatus;
import org.springframework.http.HttpStatus;

public class InvalidLifecycleTransitionException extends MnesaException {

    public InvalidLifecycleTransitionException(OpportunityStatus from, OpportunityStatus to) {
        super(String.format("Invalid opportunity lifecycle transition from %s to %s", from, to),
                HttpStatus.BAD_REQUEST,
                "INVALID_LIFECYCLE_TRANSITION");
    }

    public InvalidLifecycleTransitionException(String message) {
        super(message, HttpStatus.BAD_REQUEST, "INVALID_LIFECYCLE_TRANSITION");
    }
}
