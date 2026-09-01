package com.example.japanese.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * Builds a {@link Pageable} from the raw ?page&size&sort query params used
 * by every list endpoint (requirements section 26), with sane defaults and
 * an upper bound so a client can't request an unbounded page size.
 */
public final class PageableFactory {

    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int MAX_PAGE_SIZE = 100;

    private PageableFactory() {
    }

    /**
     * @param sort accepts "field,asc" or "field,desc"; defaults to "id,asc" when absent/blank.
     */
    public static Pageable build(Integer page, Integer size, String sort) {
        int resolvedPage = (page == null || page < 0) ? 0 : page;
        int resolvedSize = (size == null || size <= 0) ? DEFAULT_PAGE_SIZE : Math.min(size, MAX_PAGE_SIZE);
        return PageRequest.of(resolvedPage, resolvedSize, resolveSort(sort));
    }

    private static Sort resolveSort(String sort) {
        if (sort == null || sort.isBlank()) {
            return Sort.by(Sort.Direction.ASC, "id");
        }
        String[] parts = sort.split(",");
        String property = parts[0].trim();
        Sort.Direction direction = (parts.length > 1 && "desc".equalsIgnoreCase(parts[1].trim()))
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
        return Sort.by(direction, property);
    }
}
