package org.thshsh.vaadin.data;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.query.EscapeCharacter;

import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.DataProviderWrapper;
import com.vaadin.flow.data.provider.Query;

//public class ExampleDataProvider<T> extends ExampleSpecificationDataProvider2<T> {
public class ExampleDataProvider<T> extends DataProviderWrapper<T, T, ExampleSpecification<T>> {

    private static final long serialVersionUID = 3886853078195712567L;
    
    protected ExampleMatcher matcher = ExampleMatcher
            .matchingAny()
            .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
            .withIgnoreCase()
            .withIgnoreNullValues();
    
    T emptyExample;

    public ExampleDataProvider(DataProvider<T, ExampleSpecification<T>> dataProvider) {
        super(dataProvider);
        // TODO Auto-generated constructor stub
    }
    
    public ExampleSpecification<T> buildFilter(T probe) {
        Example<T> ex =  Example.of(probe, matcher);
        ExampleSpecification<T> exSpecification = new ExampleSpecification<T>(ex, EscapeCharacter.DEFAULT);
        return exSpecification;
    }
    
    public Specification<T> combineFilters(ExampleSpecification<T> query,ExampleSpecification<T> config){
        //LOGGER.info("combined filter entities query/config: {} + config",query,config);
        if(query == null) return config;
        else if(config == null) return query;
        //else if(combineCallback != null) return combineCallback.apply(query,config);
        else return config.and(query);
    }

    /* @Override
    protected ExampleSpecification<T> getFilter(Query<T, T> query) {
        // TODO Auto-generated method stub
        return null;
    }
    */
    
    @Override
    protected ExampleSpecification<T> getFilter(Query<T, T> query) {
        
        return query.getFilter().map(f -> buildFilter(f)).orElse(buildFilter(emptyExample));
        
    }
    

}
