package org.thshsh.vaadin.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.query.EscapeCharacter;

public class ExampleSpecificationBuilder<T> {

    
    private static final Logger LOGGER = LoggerFactory.getLogger(ExampleSpecificationBuilder.class);

    
    protected ExampleMatcher matcher = ExampleMatcher
            .matchingAny()
            .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
            .withIgnoreCase()
            .withIgnoreNullValues();
    
    public Specification<T> combineFilters(Specification<T> query,ExampleSpecification<T> config){
        LOGGER.debug("combined filter entities query/config: {} + {}",query,config);
         if(query == null) return config;
        else if(config == null) return query;
        else return query.and(config);
    }
    
    public Specification<T> combineFilters(Specification<T> query,Specification<T> config){
        LOGGER.debug("combined filter entities query/config: {} + {}",query,config);
         if(query == null) return config;
        else if(config == null) return query;
        else return query.and(config);
    }
    
    public ExampleSpecification<T> buildFilter(T probe) {
        LOGGER.debug("buildFilter: {}",probe);
        Example<T> ex =  Example.of(probe, matcher);
        ExampleSpecification<T> exSpecification = new ExampleSpecification<T>(ex, EscapeCharacter.DEFAULT);
        return exSpecification;

    }
    
}
