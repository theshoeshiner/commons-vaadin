package org.thshsh.vaadin;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.query.EscapeCharacter;
import org.springframework.util.Assert;

public class ExampleSpecification<T> implements Specification<T> {
	
	public static final Logger LOGGER = LoggerFactory.getLogger(ExampleSpecification.class);

	private static final long serialVersionUID = 1L;

	private final Example<T> example;
	private final EscapeCharacter escapeCharacter;

	/**
	 * Creates new {@link ExampleSpecification}.
	 *
	 * @param example
	 * @param escapeCharacter
	 */
	ExampleSpecification(Example<T> example, EscapeCharacter escapeCharacter) {

		Assert.notNull(example, "Example must not be null!");
		Assert.notNull(escapeCharacter, "EscapeCharacter must not be null!");

		this.example = example;
		this.escapeCharacter = escapeCharacter;
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.jpa.domain.Specification#toPredicate(javax.persistence.criteria.Root, javax.persistence.criteria.CriteriaQuery, javax.persistence.criteria.CriteriaBuilder)
	 */
	@Override
	public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
		LOGGER.debug("toPredicate");
		Predicate predicate = ExamplePredicateBuilder.getPredicate(root, cb, example, escapeCharacter);
		/*LOGGER.debug("predicate: {}",predicate);
		predicate.getExpressions().forEach(exp -> {
			LOGGER.debug("expression: {}",exp);
			LOGGER.debug("expression: {}",exp.getJavaType());
			if(exp.isCompoundSelection()) {
				exp.getCompoundSelectionItems().forEach(sel -> {
					LOGGER.debug("selection: {}",sel);
				});
			}
			
		});*/
		return predicate;
	}

	public Example<T> getExample() {
		return example;
	}

	public EscapeCharacter getEscapeCharacter() {
		return escapeCharacter;
	}

	
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("[example=");
		builder.append(example);
		builder.append(", escapeCharacter=");
		builder.append(escapeCharacter);
		builder.append("]");
		return builder.toString();
	}
	
	
}
