package kz.natooa.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagedResponse<T>{
    private List<T> content;    // данные
    private PageInfo page;      // инфа о странице

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PageInfo {
        private int number;
        private int size;
        private long totalElements;
        private int totalPages;
    }
}
