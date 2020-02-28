package cc.xpbootcamp.code_smell_kit.$15_speculative_generality;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@AllArgsConstructor
@Getter
public enum ExternalChannel {
    SYSTEM_NAME("外部系统");

    private String channelName;

    public boolean isSameAs(String str) {
        return Objects.equals(str, this.name());
    }

}
