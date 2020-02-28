package cc.xpbootcamp.code_smell_kit.$23_refused_request;


import java.util.List;
import java.util.Map;

public class CDOSalesPerformanceService extends BaseSalesPerformanceService<OTRAccountUser> {

    //CDOSalesPerformanceService 不希望使用父类的默认hooks, 于是自己重新实现了一份
    @Override
    protected void addAllToDTOs(List<SalesPerformanceDTO> dtoList, String option, Map<String, SalesPerformanceDTO> performanceMapping, MyQueryParam param) {
        dtoList.add(performanceMapping.get("ITEM_SUM"));
    }

}

class OTRAccountUser{}
