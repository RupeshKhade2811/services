package com.massil.ExceptionHandle;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class AppraisalException is used for raising user defined exception
 */

@NoArgsConstructor
@Data
public class AppraisalException extends Exception{

    public AppraisalException(String message) {
        super(message);
    }

}
