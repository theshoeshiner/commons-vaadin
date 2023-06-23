package org.thshsh.vaadin.data;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.repository.query.QueryByExampleExecutor;

import com.vaadin.flow.data.provider.AbstractBackEndDataProvider;
import com.vaadin.flow.data.provider.Query;

public class QueryByExampleDataProvider<T> extends AbstractBackEndDataProvider<T,T> {

    private static final long serialVersionUID = 664367914244562897L;

    protected ExampleMatcher matcher = ExampleMatcher
            .matchingAny()
            .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
            .withIgnoreCase()
            .withIgnoreNullValues();

    protected QueryByExampleExecutor<T> repository;
    protected Example<T> emptyExample;

    public QueryByExampleDataProvider(QueryByExampleExecutor<T> repository, Class<T> filterClass) {
        super();
        this.repository = repository;
        try {
            emptyExample = Example.of(filterClass.getDeclaredConstructor().newInstance());
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException| NoSuchMethodException | SecurityException e) {
            throw new IllegalArgumentException("Could not create empty example");
        }
    }

    protected Example<T> buildExample(Optional<T> probe) {
        if (probe.isEmpty()) return emptyExample;
         else return Example.of(probe.get(), matcher);
    }
    
    @Override
    public boolean isInMemory() {
        return false;
    }
    
    @Override
    protected Stream<T> fetchFromBackEnd(Query<T, T> query) {
        return repository.findAll(buildExample(query.getFilter()),ChunkRequest.of(query)).stream();
    }

    @Override
    protected int sizeInBackEnd(Query<T, T> query) {
        return Math.toIntExact(repository.count(buildExample(query.getFilter())));
    }

}
