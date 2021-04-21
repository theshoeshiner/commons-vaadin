package org.thshsh.vaadin;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.Repository;

public interface StringSearchRepository<T, ID> extends Repository<T,ID>, CrudRepository<T, ID>, PagingAndSortingRepository<T, ID> {

	public default Page<T> findByStringSearch(String s,Pageable p) {
		throw new UnsupportedOperationException();
	}
	
	public default Long countByStringSearch(String s) {
		throw new UnsupportedOperationException();
	}
	
}
