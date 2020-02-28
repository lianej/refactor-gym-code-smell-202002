package cc.xpbootcamp.code_smell_kit.$15_speculative_generality;


public class VehicleChannel {

    private Vehicle vehicle;

    private ExternalChannel salesChannel;

    private ExternalChannelStatus currentStatus;

    private String failureMessage;
}
