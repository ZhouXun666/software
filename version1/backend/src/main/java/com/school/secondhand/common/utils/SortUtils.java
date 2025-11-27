package com.school.secondhand.common.utils;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
public class SortUtils {
    public Sort getSort(int sortType) {
        switch (sortType) {
            case 1:
                return Sort.by(Sort.Direction.ASC, "price");
            case 2:
                return Sort.by(Sort.Direction.DESC, "price");
            case 3:
                return Sort.by(Sort.Direction.DESC, "createTime");
            case 0:
            default:
                // 默认排序：假设按创建时间降序（最新的在前）
                return Sort.by(Sort.Direction.ASC, "id");
        }
    }
}
