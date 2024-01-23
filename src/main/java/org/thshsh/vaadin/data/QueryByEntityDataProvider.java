package org.thshsh.vaadin.data;

import java.util.List;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.repository.query.QueryByExampleExecutor;

import com.vaadin.flow.data.provider.QuerySortOrder;

/**
 * @deprecated we should always use the specificationdataprovider
 *
 * @param <T>
 */
@Deprecated
public class QueryByEntityDataProvider<T> extends CustomCallbackDataProvider<T, T> {

	private static final long serialVersionUID = 6490352760302988710L;

	protected ExampleMatcher matcher;

	public QueryByEntityDataProvider(QueryByExampleExecutor<T> repository, List<QuerySortOrder> defaultSort) {
		this(repository, defaultSort, ExampleMatcher
	            .matchingAny()
	            .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
	            .withIgnoreCase()
	            .withIgnoreNullValues());
	}
	
	public QueryByEntityDataProvider(QueryByExampleExecutor<T> repository, List<QuerySortOrder> defaultSort, ExampleMatcher matcher) {
		super((filter, pageable) -> {
			
			return repository.findAll(Example.of(filter, matcher), pageable);
		}, (filter) -> {
			return repository.count(Example.of(filter, matcher));
		}, null, defaultSort);
	}

	public static <T> T combineFilters(T query,T config){
	     if(query == null) return config;
	    else if(config == null) return query;
	    else return null;
	}
}
