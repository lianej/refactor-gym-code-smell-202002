package cc.xpbootcamp.code_smell_kit.$23_refused_request;

import java.util.List;
import java.util.Map;

public class LeadSourceSalesPerformanceService extends BaseSalesPerformanceService<LeadSourceInfo> {

    //LeadSourceSalesPerformanceService 不需要这个hooks, 于是override了一个空方法
    @Override
    protected void addAllToDTOs(List<SalesPerformanceDTO> dtoList, String option, Map<String, SalesPerformanceDTO> performanceMapping, MyQueryParam param) {
    }

}
class LeadSourceInfo{}