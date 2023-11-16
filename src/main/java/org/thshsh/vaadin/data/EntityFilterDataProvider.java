package org.thshsh.vaadin.data;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.query.EscapeCharacter;
import org.springframework.data.repository.query.QueryByExampleExecutor;

import com.vaadin.flow.data.provider.QuerySortOrder;

/**
 * Class that wraps JpaSpecificationExecutor or QueryByExampleExecutor repositories,
 * which can both use entity instances as the filter object.
 *
 * @param <T>
 */
@Deprecated
public class EntityFilterDataProvider<T> extends CustomCallbackDataProvider<T, T> {

	private static final Logger LOGGER = LoggerFactory.getLogger(EntityFilterDataProvider.class);

	private static final long serialVersionUID = 2210995089871839624L;

	protected ExampleMatcher matcher = ExampleMatcher
			.matchingAny()
			.withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
			.withIgnoreCase()
			.withIgnoreNullValues();

	public EntityFilterDataProvider(JpaSpecificationExecutor<T> repository, List<QuerySortOrder> defaultSort) {
		this.findFunction = (f, p) -> {
			return repository.findAll(buildSpecification(f), p);
		};
		this.countFunction = (f) -> {
			return repository.count(buildSpecification(f));
		};
		if (defaultSort != null)
			this.setSortOrders(defaultSort);
	}

	public EntityFilterDataProvider(QueryByExampleExecutor<T> repository, List<QuerySortOrder> defaultSort) {
		this.findFunction = (f, p) -> {
			return repository.findAll(buildExample(f), p);
		};
		this.countFunction = (f) -> {
			return repository.count(buildExample(f));
		};

		if (defaultSort != null)
			this.setSortOrders(defaultSort);
	}

	protected Specification<T> buildSpecification(T probe) {
		LOGGER.debug("buildSpecification: {}", probe);
		if (probe == null) {
			return Specification.where(null);
		} else {
			ExampleSpecification<T> exSpecification = new ExampleSpecification<T>(buildExample(probe), EscapeCharacter.DEFAULT);
			return exSpecification;
		}
	}

	protected Example<T> buildExample(T probe) {
		return Example.of(probe, matcher);
	}
}
