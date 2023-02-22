package org.thshsh.vaadin.entity;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.thshsh.vaadin.ChunkRequest;
import org.thshsh.vaadin.ExampleSpecificationFilterDataProvider;
import org.thshsh.vaadin.StringSearchDataProvider;
import org.thshsh.vaadin.entity.ConfirmDialog.ButtonConfig;
import org.thshsh.vaadin.entity.ConfirmDialog.ConfirmDialogRunnable;
import org.thshsh.vaadin.grid.ShowOnHoverColumn;

import com.google.common.primitives.Ints;
import com.vaadin.componentfactory.ToggleButton;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.Shortcuts;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.RouteConfiguration;

/**
 * This is a component that can list entities in a table with a button column for operations
 * It needs a "Provider" implementation to be passed to it to to delegate parts of the component
 *
 *
 * @param <T>
 * @param <ID>
 */
@SuppressWarnings("serial")
@CssImport(value="entity-grid.css")
@CssImport(value = "entity-grid-vaadin-grid.css",themeFor = "vaadin-grid")
@CssImport(value="show-on-hover-column.css")
@CssImport(value = "show-on-hover-column-vaadin-grid.css",themeFor = "vaadin-grid")
@CssImport(value = "entity-grid-vcf-toggle-button.css",themeFor = "vcf-toggle-button")
public abstract class EntityGrid<T, ID extends Serializable> extends VerticalLayout {
 
	public static final Logger LOGGER = LoggerFactory.getLogger(EntityGrid.class);

	public static enum FilterMode {
		String, Example, None;
	}
	
	public static final String CLASS = "entity-grid";

	@Autowired
	protected ApplicationContext appCtx;


	protected PagingAndSortingRepository<T, ID> repository;
	protected DataProvider<T, ?> dataProvider;

	protected T filterEntity;
	protected Class<? extends Component> entityView;
	protected Grid<T> grid;
	protected Boolean defaultSortAsc = true;
	protected String defaultSortOrderProperty = null;
	protected Boolean showButtonColumn = false;
	protected Boolean showEditButton = true;
	protected Boolean showDeleteButton = true;
	protected Boolean showCreateButton = true;
	protected Boolean showHeader = true;
	protected Boolean showCount = true;
	protected Boolean showFilter = true;
	protected Boolean columnsResizable = null;
	protected Span count;
	protected TextField filter;
	protected Column<?> buttonColumn;
	protected String createText = "New";
	protected HorizontalLayout header;
	protected Span countAndAdvanced;

	//holds a temporary reference to the edit button, which is replaced as we are iterating over the rows
	protected Button editButton;
	protected Button deleteButton;
	protected Button addButton;
	protected ToggleButton advancedButton;
	protected Collection<Column<T>> advancedColumns = new ArrayList<>();

	protected FilterMode filterMode;

	protected Boolean caseSensitive = false;
	protected Boolean emptyFilter = true;
	
	protected EntityDescriptor<T, ID> descriptor;

	
	public EntityGrid(Class<? extends Component> ev,FilterMode fm, String sortProp) {
		this.entityView = ev;
		this.filterMode = fm;
		this.defaultSortOrderProperty = sortProp;
	}
	


	@SuppressWarnings({ "unchecked", "deprecation" })
	@PostConstruct
	public void postConstruct() {

		this.setWidthFull();
		LOGGER.debug("postConstruct");

		this.addClassName(CLASS);


		dataProvider = createDataProvider();

		LOGGER.debug("dataProvider: {}",dataProvider);

		if (filterMode == FilterMode.Example) {
			filterEntity = createFilterEntity();
			((ExampleSpecificationFilterDataProvider<T>) dataProvider).setFilterExample(filterEntity);
		}

		if (showHeader) {
			header = new HorizontalLayout();
			header.setSpacing(true);
			header.setWidth("100%");
			header.setAlignItems(Alignment.CENTER);
			this.add(header);
			
			countAndAdvanced = new Span();
			countAndAdvanced.addClassName("header-left");

			count = new Span();
			count.addClassName("count");
			countAndAdvanced.add(count);
			
			
			advancedButton = new ToggleButton("Advanced", false);
			advancedButton.addClassName("advanced");
			advancedButton.setVisible(false);
			advancedButton.addValueChangeListener(change -> {
				showAdvanced(change.getValue());
			});
			countAndAdvanced.add(advancedButton);
			
			header.addAndExpand(countAndAdvanced);
			
			
			
			if(showFilter) {
				filter = new TextField();
				filter.setClearButtonVisible(true);
	
				filter.setPlaceholder("Filter");
				filter.addValueChangeListener(change -> changeFilter(change.getValue()));
				header.add(filter);
			}

			if (entityView != null && showCreateButton) {
				addButton = new Button(createText + " " + descriptor.getEntityTypeName(), VaadinIcon.PLUS.create());
				addButton.addClickListener(this::clickNew);
				addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
				header.add(addButton);
			}
		}

		grid = new Grid<T>(descriptor.getEntityClass(), false);
		grid.setDataProvider(dataProvider);
		grid.addThemeVariants(
				//GridVariant.LUMO_NO_ROW_BORDERS,
				GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_ROW_STRIPES);
		grid.addClassName("borderless");
		grid.setHeight("100%");
		grid.setWidthFull();

		//dataProvider = provider.createDataProvider();
		//filterEntity = provider.createFilterEntity();
		//dataProvider.setFilter(filterEntity);
		//grid.setDataProvider(dataProvider);

		setupColumns(grid);
		
		if(columnsResizable!=null) {
			grid.getColumns().forEach(col -> {
				col.setResizable(columnsResizable);
			});
		}
		
		this.advancedColumns.forEach(col -> {
			col.setVisible(false);
		});

		if (showButtonColumn) {

			grid.addClassName("button-column");
			buttonColumn = grid.addComponentColumn(e -> {

				HorizontalLayout buttons = new HorizontalLayout();
				buttons.addClassNames("grid-buttons","grid-buttons-layout");
				buttons.setPadding(true);
				buttons.setWidthFull();
				buttons.setJustifyContentMode(JustifyContentMode.END);

				addButtonColumn(buttons, e);

				return buttons;
			})
				.setFlexGrow(0)
				.setClassNameGenerator(val -> "grid-buttons-column "+ShowOnHoverColumn.SHOW_ON_HOVER_COLUMN_CLASS)
				.setWidth("250px");

		}


		Shortcuts.addShortcutListener(grid, () -> {
			grid.getSelectedItems().stream().findFirst().ifPresent(e -> {
				shortcutDetails(e);
			});
		}, Key.KEY_I, KeyModifier.CONTROL);

		this.add(grid);

		updateCount();

	}

	public void refresh() {
		dataProvider.refreshAll();
		updateCount();
	}
	
	public void refresh(T entity) {
		dataProvider.refreshItem(entity);
	}

	public void addButtonColumn(HorizontalLayout buttons, T e) {
		if (showEditButton) {
			editButton = new Button(VaadinIcon.PENCIL.create());
			editButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
			buttons.add(editButton);
			editButton.setTooltipText("Edit");
			editButton.addClickListener(click -> clickEdit(click, e));
		}
		if (showDeleteButton) {
			deleteButton = new Button(VaadinIcon.TRASH.create());
			deleteButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
			buttons.add(deleteButton);
			deleteButton.setTooltipText("Delete");
			deleteButton.addClickListener(click -> {
				clickDelete(e);
			});
		}

	}

	
	@SuppressWarnings("unchecked")
	public void shortcutDetails(T e) {
		DetailsDialog<T,ID> dd = appCtx.getBean(DetailsDialog.class,e,this);
		dd.open();
	}

	public void clickDelete(T e) {
		String nameString = descriptor.getEntityTypeName();
		String entityName = descriptor.getEntityName(e);
		if(entityName  != null) nameString += " \"" + entityName +"\"";
		ConfirmDialog cd = ConfirmDialogs.deleteDialog(nameString,(ConfirmDialogRunnable) (d,b) -> {
			delete(e,d,b);
		});
		cd.open();

	}
	
	public void delete(T e,ConfirmDialog d,ButtonConfig bc) {
		delete(e);
	}

	public void delete(T e) {
		repository.delete(e);
		refresh();
	}

	public void clickEdit(ClickEvent<Button> click, T entity) {
		if(entityView != null) {
			if (Dialog.class.isAssignableFrom(entityView)) {
				Dialog cd = createDialog(entity);
				cd.open();
				cd.addOpenedChangeListener(change -> {
					refresh();
				});
			} else {
				Class<? extends Component> hup = entityView;

				String route = RouteConfiguration.forSessionScope().getUrl(hup);

				QueryParameters queryParameters = new QueryParameters(Collections.singletonMap("id",
						Arrays.asList(descriptor.getEntityId(entity).toString())));

				UI.getCurrent().navigate(route, queryParameters);

			}
		}
	}
	
	
	public DataProvider<T, ?> getDataProvider() {
		return dataProvider;
	}

	@SuppressWarnings("unchecked")
	public DataProvider<T, ?> createDataProvider() {
		LOGGER.debug("createDataProvider: {}",filterMode);

		switch (filterMode) {
		case Example: {
			JpaSpecificationExecutor<T> r = (JpaSpecificationExecutor<T>) repository;
			ExampleSpecificationFilterDataProvider<T> dataProvider = new ExampleSpecificationFilterDataProvider<T>(descriptor.getEntityClass(),r,getDefaultSortOrder());
			return dataProvider;
		}
		case String: {
			//StringSearchRepository<T, ID> r = (StringSearchRepository<T, ID>) repository;
			StringSearchDataProvider<T, ID> dp = new StringSearchDataProvider<>(repository,getDefaultSortOrder());
			return dp;
		}
		case None: {
			CallbackDataProvider<T, Void> dataProvider = DataProvider.fromCallbacks(
					q -> repository.findAll(ChunkRequest.of(q, getDefaultSortOrder())).getContent().stream(),
					q -> Ints.checkedCast(repository.count()));

			return dataProvider;
		}
		default: throw new IllegalStateException();

		}

	}

	public List<QuerySortOrder> getDefaultSortOrder() {
		if(defaultSortOrderProperty == null) return null;
		if (defaultSortAsc)
			return QuerySortOrder.asc(defaultSortOrderProperty).build();
		else
			return QuerySortOrder.desc(defaultSortOrderProperty).build();
	}

	public T createFilterEntity() {
		try {
			return descriptor.getEntityClass().getConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			throw new IllegalArgumentException("Could not instantiate class " + descriptor.getEntityClass());
		}
	}

	public void updateCount() {
		if (showHeader && showCount) {
			long full = getCountAll();
			long shown = emptyFilter?full:getCountFiltered();
			count.setText("Showing " + shown + " of " + full);
		}
	}

	@SuppressWarnings("unchecked")
	public Long getCountFiltered() {		
		switch(filterMode) {
			case Example: return (long) ((ExampleSpecificationFilterDataProvider<T>)dataProvider).size(new Query<>());
			case None: return repository.count();
			case String: return ((StringSearchDataProvider<T, Serializable>)dataProvider).countAll();
			default: throw new IllegalStateException();
		}
		
	}
	
	/**
	 * This needs to return the full count
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Long getCountAll() {
		LOGGER.debug("countall: {}",emptyFilter);
		switch(filterMode) {
			case Example: return (long) ((ExampleSpecificationFilterDataProvider<T>)dataProvider).sizeUnfiltered(new Query<>());
			case None: return repository.count();
			case String: return ((StringSearchDataProvider<T, Serializable>)dataProvider).countAll();
			default: throw new IllegalStateException();
		}
	}

	@SuppressWarnings("unchecked")
	public void changeFilter(String text) {

		if (StringUtils.isBlank(text)) emptyFilter = true;
		else emptyFilter = false;

		if(!caseSensitive) text = StringUtils.lowerCase(text);
		switch (filterMode) {
			case Example:
				if (emptyFilter) clearFilter();
				else setFilter(text);
				break;
			case String:
				((StringSearchDataProvider<T, ID>) dataProvider).setFilter(text);
				break;
			case None:
				break;
			default:
				break;
		}

		dataProvider.refreshAll();
		updateCount();
	}

	@SuppressWarnings("unchecked")
	public void clickNew(ClickEvent<Button> click) {
		if (Dialog.class.isAssignableFrom(entityView)) {
			Dialog cd = createDialog(null);
			cd.open();
			cd.addOpenedChangeListener(change -> {
				if (cd instanceof EntityDialog) {
					EntityDialog<T,ID> ed = (EntityDialog<T,ID>) cd;
					if (ed.getEntityForm().getSaved())
						refresh();
				} else
					refresh();
			});
		} else {
			UI.getCurrent().navigate(entityView);
		}
	}

	public Dialog createDialog(T entity) {
		Dialog cd = (Dialog) appCtx.getBean(entityView,entity);
		return cd;
	}

	protected EntityGridRefreshThread refreshThread;

	public void refreshEvery(Long ms) {

		if(ms == null) {
			if(refreshThread != null) {
				refreshThread.setStopped();
				refreshThread = null;
			}
		}
		else {
			if(refreshThread == null) {
				refreshThread = new EntityGridRefreshThread(this, UI.getCurrent(), ms);
				UI.getCurrent().addBeforeLeaveListener(before -> {
					refreshThread.setStopped();
				});
				refreshThread.start();
			}
			else refreshThread.setWait(ms);
		}

	}
	
	
	public void showAdvanced(boolean show) {
		if(!show) {
			advancedColumns.forEach(col -> {
				grid.removeColumn(col);
			});
			advancedColumns.clear();
		}
		else {
			setupAdvancedColumns(grid,advancedColumns);
			if(columnsResizable!=null) {
				advancedColumns.forEach(col -> {
					col.setResizable(columnsResizable);
				});
			}
		}
		
	}

	public PagingAndSortingRepository<T, ID> getRepository(){
		return repository;
	}

	public abstract void setupColumns(Grid<T> grid);
	
	public void setupAdvancedColumns(Grid<T> grid, Collection<Column<T>> coll) {
		
	}


	public void setFilter(String text) {};
	public void clearFilter() {}


	public Grid<T> getGrid() {
		return grid;
	}


	public HorizontalLayout getHeader() {
		return header;
	}
	
	public EntityDescriptor<T, ID> getDescriptor() {
		return descriptor;
	}


	public void setDescriptor(EntityDescriptor<T, ID> descriptor) {
		this.descriptor = descriptor;
	}


	public void setRepository(PagingAndSortingRepository<T, ID> repository) {
		this.repository = repository;
	}
	
	
	
}
