package org.thshsh.vaadin;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

public interface ExampleFilterRepository<T,ID> extends PagingAndSortingRepository<T, ID> , QueryByExampleExecutor<T>, JpaSpecificationExecutor<T> {
 
	
}
