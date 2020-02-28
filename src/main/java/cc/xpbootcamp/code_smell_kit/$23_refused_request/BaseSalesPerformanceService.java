package cc.xpbootcamp.code_smell_kit.$23_refused_request;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class BaseSalesPerformanceService<T> {

    // 模板方法
    public List<SalesPerformanceDTO> convertResultsMappingToDTOs(List<T> queryList, String filterValue, Map<String, SalesPerformanceDTO> performanceMapping, MyQueryParam param) {
        List<SalesPerformanceDTO> dtoList = new ArrayList<>();
        addAllToDTOs(dtoList, filterValue, performanceMapping, param);
        // 省略其他逻辑
        return dtoList;
    }

    // hooks, 默认实现
    protected void addAllToDTOs(List<SalesPerformanceDTO> dtoList, String option, Map<String, SalesPerformanceDTO> performanceMapping, MyQueryParam param) {
        if ("ALL".equals(option)) {
            dtoList.add(performanceMapping.get("ALL"));
        }
    }

    // 省略其他方法
}
