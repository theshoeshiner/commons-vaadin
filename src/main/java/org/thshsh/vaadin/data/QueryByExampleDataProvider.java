package org.thshsh.vaadin.data;

import java.util.List;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.jpa.repository.query.EscapeCharacter;
import org.springframework.data.repository.query.QueryByExampleExecutor;

import com.vaadin.flow.data.provider.QuerySortOrder;

/**
 * @deprecated we should always use the jpa spec provider
 *
 * @param <T>
 */
@Deprecated
public class QueryByExampleDataProvider<T> extends CustomCallbackDataProvider<T,Example<T>> {

    private static final long serialVersionUID = 664367914244562897L;

    public QueryByExampleDataProvider(QueryByExampleExecutor<T> repository, List<QuerySortOrder> defaultSort) {
    	super(repository::findAll,repository::count,null,defaultSort);
    }
    
    public static <T> ExampleSpecification<T> buildFilter(T probe, ExampleMatcher matcher) {
        Example<T> ex =  Example.of(probe, matcher);
        ExampleSpecification<T> exSpecification = new ExampleSpecification<T>(ex, EscapeCharacter.DEFAULT);
        return exSpecification;

    }

}
