package com.suyh2901.common;

import lombok.Data;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.convert.QueryByExamplePredicateBuilder;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * JPA范围查询工具类
 *
 * @param <T>
 */
public class JpaRangeQuery<T> implements Specification<T> {
    private final Example<T> example;
    private List<IAttributeRange<Long>> rangeList;

    public JpaRangeQuery(Example<T> example, List<IAttributeRange<Long>> rangeList) {
        this.example = example;
        this.rangeList = rangeList;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
        List<String> ignorePaths = new ArrayList<>();
        for (IAttributeRange<?> rangeElement : rangeList) {
            if (rangeElement.isValid()) {
                ignorePaths.add(rangeElement.getAttribute());
            }
        }

        example.getMatcher().withIgnorePaths(ignorePaths.toArray(new String[0]));
        Predicate predicateExample = QueryByExamplePredicateBuilder.getPredicate(root, cb, example);

        Predicate result = predicateExample;
        for (IAttributeRange<Long> rangeElement : rangeList) {
            if (rangeElement.getLowerBound() != null) {
                if (rangeElement.isContainLower()) {
                    Long lowerBound = rangeElement.getLowerBound();
                    Path<Long> objectPath = root.get(rangeElement.getAttribute());
                    Predicate predicate = cb.greaterThanOrEqualTo(objectPath, lowerBound);
                    result = cb.and(predicateExample, predicate);
                } else {
                    Predicate predicate = (cb.greaterThan(
                            root.get(rangeElement.getAttribute()), rangeElement.getLowerBound()));
                    result = cb.and(predicateExample, predicate);
                }
            }
            if (rangeElement.getUpperBound() != null) {
                if (rangeElement.isContainUpper()) {
                    Predicate predicate = (cb.lessThanOrEqualTo(
                            root.get(rangeElement.getAttribute()), rangeElement.getLowerBound()));
                    result = cb.and(predicateExample, predicate);
                } else {
                    Predicate predicate = (cb.lessThan(
                            root.get(rangeElement.getAttribute()), rangeElement.getLowerBound()));
                    result = cb.and(predicateExample, predicate);
                }
            }
        }
        return result;
    }

    public interface IAttributeRange<T> {
        String getAttribute();

        T getLowerBound();

        T getUpperBound();

        boolean isContainLower();

        boolean isContainUpper();

        boolean isValid();
    }

    @Data
    public static class AttributeRange<T> implements IAttributeRange<T>, Serializable {
        static final long serialVersionUID = 42L;

        private String attribute;   // 属性名称字符串
        private T lowerBound;   // 下边界
        private boolean containLower;   // 是否包含下边界
        private T upperBound;   // 上边界
        private boolean containUpper; // 是否包含上边界

        @Override
        public boolean isValid() {
            return lowerBound != null || upperBound != null;
        }
    }
}
