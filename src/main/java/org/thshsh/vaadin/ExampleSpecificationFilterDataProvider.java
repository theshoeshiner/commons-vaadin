package org.thshsh.vaadin;

import java.util.List;
import java.util.stream.Stream;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.query.EscapeCharacter;

import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.QuerySortOrder;

/**
 * Extension of SpecificationFilterDataProvider that allows creating a specification via an example entity
 *
 * @param <T>
 */
@SuppressWarnings("serial")
public class ExampleSpecificationFilterDataProvider<T> extends SpecificationFilterDataProvider<T> {

	protected ExampleMatcher matcher = ExampleMatcher
			.matchingAny()
			.withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
			.withIgnoreCase()
			.withIgnoreNullValues();
	
	//the example entity used to build the specification below
	protected T filterExample;
	//This gets applied to all queries that do not have embedded filters
	protected Specification<T> filterSpecification;
	
	public ExampleSpecificationFilterDataProvider(Class<T> classs, JpaSpecificationExecutor<T> r, List<QuerySortOrder> defaultSort) {
		super(r, defaultSort);
	}
	

	protected Example<T> buildExample(T probe) {
		return Example.of(probe, matcher);
	}

	 public void setBaseExample(T example) {
		 if(example == null) setBaseSpecification(null);
		 else setBaseSpecification( new ExampleSpecification<T>(buildExample(example), EscapeCharacter.DEFAULT)); 
	 }
	 
	 public void setFilterExample(T example) {
		 this.filterExample = example;
		 rebuildFilterSpecification();
	 }

	 public T getFilterExample() {
		return filterExample;
	}


	public Specification<T> getFilterSpecification() {
		return filterSpecification;
	}


	protected void rebuildFilterSpecification() {
		 if(filterExample == null) filterSpecification = null;
		 else {
			 filterSpecification = new ExampleSpecification<T>(buildExample(filterExample), EscapeCharacter.DEFAULT);
		 }
	 }
	 
	 @Override
	public Stream<T> fetch(Query<T, Specification<T>> query) {
		 Query<T, Specification<T>> q =  new Query<>(query.getOffset(), query.getLimit(),
		            query.getSortOrders(), query.getInMemorySorting(),
		            query.getFilter().orElse(filterSpecification));
		 
		return super.fetch(q);
	}


	@Override
	public int size(Query<T, Specification<T>> query) {
		 Query<T, Specification<T>> q =  new Query<>(query.getOffset(), query.getLimit(),
		            query.getSortOrders(), query.getInMemorySorting(),
		            query.getFilter().orElse(filterSpecification));
		return super.size(q);
	}
	
	public int sizeUnfiltered(Query<T, Specification<T>> query) {
		 Query<T, Specification<T>> q =  new Query<>(query.getOffset(), query.getLimit(),
		            query.getSortOrders(), query.getInMemorySorting(),
		            null);
		return super.size(q);
	}


}
