package org.thshsh.vaadin;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;

public class GridUtils {
	
	@SuppressWarnings("unchecked")
	public static <T> void reorderColumns(Grid<T> grid, Object... objects ) {
		List<Column<T>> columns = new ArrayList<>(grid.getColumns());
		Column<T> column = null;
		Integer index = null;
		for(int i=0;i<objects.length;i++) {
			if(i%2==0) column = (Column<T>) objects[i];
			else {
				index = (Integer) objects[i];
				if(columns.indexOf(column)!=index) {
					columns.remove(column);
					columns.add(index, column);
				}
			}
		}
		grid.setColumnOrder(columns);
	}

}
