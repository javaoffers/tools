package com.javaoffers.brief.excel;

import lombok.Builder;
import lombok.Data;

/**
 * 媒体数据。图片或视频.
 */
@Data
@Builder
public class MediaData {
    private byte[] data;
    private int pictureType;
    private int format;
    private String suggestFileExtension;
    private String mimeType;
}