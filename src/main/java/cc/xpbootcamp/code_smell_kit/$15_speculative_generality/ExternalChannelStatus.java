package cc.xpbootcamp.code_smell_kit.$15_speculative_generality;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ExternalChannelStatus {

    IS_AVAILABLE(true, "已上架"),
    IS_UNAVAILABLE(false, "未上架"),
    PROCESSING_AVAILABLE(true, "上架中"),
    PROCESSING_UNAVAILABLE(true, "下架中"),
    FAIL_OF_AVAILABLE(false, "上架失败"),
    FAIL_OF_UNAVAILABLE(true, "下架失败");

    private boolean available;

    private String description;

}
