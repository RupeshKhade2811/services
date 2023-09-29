package com.massil.dto;


import com.massil.ExceptionHandle.Response;
import lombok.*;

/**
 * This class is a DTO of VideoAndImageResponse
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class VideoAndImageResponse extends Response {
    private byte[] videoBytes;
    private byte[] imageBytes;


}
