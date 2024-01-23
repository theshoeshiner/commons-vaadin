package org.thshsh.vaadin.data;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.vaadin.flow.data.provider.AbstractBackEndDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.QuerySortOrder;

import lombok.Getter;
import lombok.Setter;

/**
 * This class is equivalent to 
 * <code>
 * CallbackDataProvider<T, F> dataProvider = DataProvider.fromFilteringCallbacks(
                q -> findFunction.apply(q.getFilter().orElse(emptyFilter), null).stream(),
                q -> Math.toIntExact(countFunction.apply(q.getFilter().orElse(emptyFilter))));
 * </code>
 *
 * @param <ENTITY>
 * @param <FILTER>
 */
@Getter
@Setter
public class CustomCallbackDataProvider<ENTITY,FILTER> extends AbstractBackEndDataProvider<ENTITY,FILTER> {

	private static final long serialVersionUID = 317766754368674664L;
	
	protected BiFunction<FILTER, Pageable, Page<ENTITY>> findFunction;
	protected Function<FILTER, ? extends Number> countFunction;
	protected FILTER emptyFilter;
	
	public CustomCallbackDataProvider() {}
	
	public CustomCallbackDataProvider(BiFunction<FILTER, Pageable, Page<ENTITY>> findFunction, Function<FILTER, ? extends Number> countFunction, FILTER emptyFilter, List<QuerySortOrder> defaultSort) {
		super();
		this.findFunction = findFunction;
		this.countFunction = countFunction;
		this.emptyFilter = emptyFilter;
		this.setSortOrders(defaultSort);
	}

	@Override
	protected Stream<ENTITY> fetchFromBackEnd(Query<ENTITY, FILTER> query) {
		 return findFunction.apply(query.getFilter().orElse(emptyFilter), ChunkRequest.of(query)).stream();
	}

	@Override
	protected int sizeInBackEnd(Query<ENTITY, FILTER> query) {
		return countFunction.apply(query.getFilter().orElse(emptyFilter)).intValue();
	}
	
}
