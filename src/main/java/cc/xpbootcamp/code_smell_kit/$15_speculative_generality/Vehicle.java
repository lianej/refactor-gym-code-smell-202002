package cc.xpbootcamp.code_smell_kit.$15_speculative_generality;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Vehicle {

    private Long id;

    //车辆可销售渠道
    private List<VehicleChannel> channels;

    //省略其他属性

}
