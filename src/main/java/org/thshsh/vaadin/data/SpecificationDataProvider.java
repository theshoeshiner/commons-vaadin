package org.thshsh.vaadin.data;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.vaadin.flow.data.provider.QuerySortOrder;

/**
 * Data provider that uses JPA Specifications as a filter and passes them to a JpaSpecificationExecutor instance.
 *
 * @param <T>
 */
public class SpecificationDataProvider<T> extends CustomCallbackDataProvider<T,Specification<T>>{
    
    private static final long serialVersionUID = -4502566155677348939L;

    public static final Logger LOGGER = LoggerFactory.getLogger(SpecificationDataProvider.class); 
    
    public SpecificationDataProvider(JpaSpecificationExecutor<T> repository,List<QuerySortOrder> defaultSort) {
    	super(repository::findAll,repository::count,Specification.where(null),defaultSort);
    }

	public static <V> Specification<V> combineFilters(Specification<V> query,Specification<V> config){
	     if(query == null) return config;
	    else if(config == null) return query;
	    else return query.and(config);
	}

}
