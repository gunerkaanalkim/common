package org.kaanalkim.common.repository.base;

import org.kaanalkim.common.exception.GenericSpecificationException;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.List;

import static org.kaanalkim.common.repository.base.Operators.*;

public class GenericSpecification<T> {
    private static final String DELIMITER = ".";
    private static final String DELIMITER_REGEX = "\\.";
    private static final int MAX_DEPTH = 2;

    public Specification<T> filter(SearchFilterRequest searchFilterRequest) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = searchFilterRequest.filters
                    .stream()
                    .map(searchFilter -> {
                        try {
                            return findMethodByOperator(root, criteriaBuilder, searchFilter);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .toList();

            Predicate predicate = criteriaBuilder.and(predicates.toArray(new Predicate[0]));

            if (OR.getOperator().equalsIgnoreCase(searchFilterRequest.getOperator())) {
                predicate = criteriaBuilder.or(predicates.toArray(new Predicate[0]));
            }

            return predicate;
        };
    }

    private static <T> Predicate findMethodByOperator(Root<T> root, CriteriaBuilder criteriaBuilder, SearchFilter searchFilter) throws Exception {
        String by = searchFilter.getBy();

        Path<String> createdBy = getStringPath(root, by);

        return getPredicate(criteriaBuilder, searchFilter, createdBy);
    }

    private static <T> Path<String> getStringPath(Root<T> root, String by) {
        Path<String> createdBy;

        if (by.contains(DELIMITER)) {
            String[] splitBy = by.split(DELIMITER_REGEX);

            if (splitBy.length > MAX_DEPTH) {
                throw new GenericSpecificationException("Only one deep object will be allowed.");
            }

            String relation = splitBy[0];
            String nestedBy = splitBy[1];

            Join<Object, Object> joinedRoot = root.join(relation);

            createdBy = joinedRoot.get(nestedBy);
        } else {
            createdBy = root.get(by);
        }

        return createdBy;
    }


    private static Predicate getPredicate(CriteriaBuilder criteriaBuilder, SearchFilter searchFilter, Path<String> createdBy) {
        Predicate predicate;

        if (NOT_EQUAL.getOperator().equalsIgnoreCase(searchFilter.getOperator())) {
            predicate = criteriaBuilder.notEqual(createdBy, searchFilter.getValue());
        } else if (GREATER_THAN_OR_EQUAL.getOperator().equalsIgnoreCase(searchFilter.getOperator())) {
            predicate = criteriaBuilder.greaterThanOrEqualTo(createdBy, searchFilter.getValue());
        } else if (GREATER_THAN.getOperator().equalsIgnoreCase(searchFilter.getOperator())) {
            predicate = criteriaBuilder.greaterThan(createdBy, searchFilter.getValue());
        } else if (LESS_THAN_OR_EQUAL.getOperator().equalsIgnoreCase(searchFilter.getOperator())) {
            predicate = criteriaBuilder.lessThanOrEqualTo(createdBy, searchFilter.getValue());
        } else if (LESS_THAN.getOperator().equalsIgnoreCase(searchFilter.getOperator())) {
            predicate = criteriaBuilder.lessThan(createdBy, searchFilter.getValue());
        } else if (LIKE.getOperator().equalsIgnoreCase(searchFilter.getOperator())) {
            predicate = criteriaBuilder.like(createdBy, String.format("%%%s%%", searchFilter.getValue()));
        } else {
            predicate = criteriaBuilder.equal(createdBy, searchFilter.getValue());
        }

        return predicate;
    }
}
