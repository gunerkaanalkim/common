package org.kaanalkim.common.service.base;

import org.kaanalkim.common.exception.ObjectNotFoundById;
import org.kaanalkim.common.model.base.AbstractEntity;
import org.kaanalkim.common.model.enums.ErrorCode;
import org.kaanalkim.common.repository.base.BaseRepository;
import org.kaanalkim.common.repository.base.GenericSpecification;
import org.kaanalkim.common.repository.base.SearchFilterRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.beans.FeatureDescriptor;
import java.util.List;
import java.util.stream.Stream;

public abstract class AbstractCrudService<T extends AbstractEntity> extends AbstractService {
    protected abstract <K extends BaseRepository<T, Long>> K getRepository();

    public T save(T t) {
        T entity = getRepository().save(t);
        logger.info("{} : {} saved successfully! {}", className, getName(t.getClass()), t);
        return entity;
    }

    public List<T> saveAll(List<T> all) {
        final List<T> tList = getRepository().saveAll(all);
        logger.info("{}, list saved successfully! Size: {}", className, tList.size());
        return all;
    }

    public T update(T updatedEntity) {
        final T persistedEntity = getRepository()
                .findById(updatedEntity.getId())
                .orElseThrow(
                        () -> new ObjectNotFoundById(super.getErrorMessage(ErrorCode.NOT_FOUND, updatedEntity.getId().toString()))
                );

        BeanUtils.copyProperties(updatedEntity, persistedEntity, this.getNullPropertyNames(updatedEntity));

        getRepository().save(persistedEntity);
        logger.info("{} : {} updated successfully! {}", className, super.getName(updatedEntity.getClass()), updatedEntity);

        return persistedEntity;
    }

    public T get(Long id) {
        validateId(id);

        final T t = getRepository().findById(id).orElseThrow(() -> new ObjectNotFoundById(super.getErrorMessage(ErrorCode.NOT_FOUND, id.toString())));

        logger.info("{} : {} object is found by id: {}, object is: {}", className, super.getName(t.getClass()), id, t);

        return t;
    }

    public Page<T> getAll(Pageable pageable) {
        final Page<T> all = getRepository().findAll(pageable);
        logger.info("{} : {} object found.", className, all.getContent().size());
        return all;
    }

    public List<T> getAllWithoutPage() {
        List<T> all = getRepository().findAll();
        logger.info("{} : {} object found.", className, all.size());

        return all;
    }

    public T delete(Long id) {
        validateId(id);

        final T t = getRepository().findById(id).orElseThrow(() -> new ObjectNotFoundById(super.getErrorMessage(ErrorCode.NOT_FOUND, id.toString())));

        getRepository().deleteById(id);
        logger.info("{} : Object deleted by id {} successfully!", className, id);

        return t;
    }

    public List<T> deleteAll(List<T> all) {
        all.forEach(ent -> validateId(ent.getId()));

        getRepository().deleteAll(all);
        logger.info("{} : All objects deleted successfully!", className);

        return all;
    }

    public Pageable getPaging(Integer pageNo, Integer pageSize, String column, String order) {
        int calculatedPageNumber = pageNo - 1;

        if (calculatedPageNumber <= 0) {
            calculatedPageNumber = 0;
        }

        return getPager(calculatedPageNumber, pageSize, column, order);
    }

    private String[] getNullPropertyNames(T t) {
        final BeanWrapper wrappedSource = new BeanWrapperImpl(t);
        return Stream.of(wrappedSource.getPropertyDescriptors())
                .map(FeatureDescriptor::getName)
                .filter(propertyName -> wrappedSource.getPropertyValue(propertyName) == null)
                .toArray(String[]::new);
    }

    public Page<T> filter(SearchFilterRequest searchFilterRequest, Pageable pageable) {
        GenericSpecification<T> genericSpecification = new GenericSpecification<>();
        Specification<T> specification = genericSpecification.filter(searchFilterRequest);

        return getRepository().findAll(specification, pageable);
    }
}