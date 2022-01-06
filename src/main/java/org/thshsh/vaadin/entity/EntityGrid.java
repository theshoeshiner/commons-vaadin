package org.thshsh.vaadin.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.atteo.evo.inflector.English;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.thshsh.vaadin.ChunkRequest;
import org.thshsh.vaadin.ExampleFilterDataProvider;
import org.thshsh.vaadin.ExampleFilterRepository;
import org.thshsh.vaadin.StringSearchDataProvider;
import org.thshsh.vaadin.UIUtils;
import org.thshsh.vaadin.entity.ConfirmDialog.ButtonConfig;
import org.thshsh.vaadin.entity.ConfirmDialog.ConfirmDialogRunnable;

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
 * @author daniel.watson
 *
 * @param <T>
 * @param <ID>
 */
@SuppressWarnings("serial")
@CssImport("entity-grid.css")
@CssImport(value = "entity-grid-vaadin-grid.css",themeFor = "vaadin-grid")
@CssImport(value = "entity-grid-vcf-toggle-button.css",themeFor = "vcf-toggle-button")
public abstract class EntityGrid<T, ID extends Serializable> extends VerticalLayout {
 
	public static final Logger LOGGER = LoggerFactory.getLogger(EntityGrid.class);

	public static enum FilterMode {
		String, Example, None;
	}
	
	public static final String SHOW_ON_HOVER_COLUMN_CLASS = "show-on-hover";

	@Autowired
	protected ApplicationContext appCtx;

	//EntitiesListProvider<T, ID> listOperationProvider;

	protected PagingAndSortingRepository<T, ID> repository;
	protected DataProvider<T, ?> dataProvider;

	protected T filterEntity;
	protected Class<? extends Component> entityView;
	protected Class<T> entityClass;
	protected Grid<T> grid;
	protected Boolean defaultSortAsc = true;
	protected String defaultSortOrderProperty = null;
	protected String entityName;
	protected String entityNamePlural;
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

	public EntityGrid(Class<T> c, Class<? extends Component> ev,FilterMode fm, String sortProp) {
		this.entityClass = c;
		this.entityView = ev;
		this.filterMode = fm;
		this.defaultSortOrderProperty = sortProp;
	}
	


	@SuppressWarnings({ "unchecked", "deprecation" })
	@PostConstruct
	public void postConstruct() {

		this.setWidthFull();
		LOGGER.debug("postConstruct");

		this.repository = getRepository();
		this.addClassName("entities-view");

		if (entityName == null)
			entityName = entityClass.getSimpleName();
		if (entityNamePlural == null)
			entityNamePlural = English.plural(entityName);

		dataProvider = createDataProvider();

		LOGGER.debug("dataProvider: {}",dataProvider);

		if (filterMode == FilterMode.Example) {
			filterEntity = createFilterEntity();
			((ExampleFilterDataProvider<T, ID>) dataProvider).setFilter(filterEntity);
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
				addButton = new Button(createText + " " + entityName, VaadinIcon.PLUS.create());
				addButton.addClickListener(this::clickNew);
				addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
				header.add(addButton);
			}
		}

		grid = new Grid<T>(entityClass, false);
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
				buttons.addClassName("grid-buttons");
				buttons.setPadding(true);
				buttons.setWidthFull();
				buttons.setJustifyContentMode(JustifyContentMode.END);

				addButtonColumn(buttons, e);

				return buttons;
			}).setFlexGrow(0).setClassNameGenerator(val -> {
				return "grid-buttons-column "+SHOW_ON_HOVER_COLUMN_CLASS;
			}).setWidth("250px");

		}

		grid.addItemClickListener(click -> {
			LOGGER.debug("Clicked item: {}", click.getItem());
		});

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
			UIUtils.setTitle(editButton, "Edit");
			editButton.addClickListener(click -> clickEdit(click, e));
		}
		if (showDeleteButton) {
			deleteButton = new Button(VaadinIcon.TRASH.create());
			deleteButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
			buttons.add(deleteButton);
			UIUtils.setTitle(deleteButton, "Delete");
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
		String nameString = entityName;
		String entityName = getEntityName(e);
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
						Arrays.asList(getEntityId(entity).toString())));

				UI.getCurrent().navigate(route, queryParameters);

			}
		}
	}

	public DataProvider<T, ?> createDataProvider() {
		LOGGER.debug("createDataProvider: {}",filterMode);

		switch (filterMode) {
		case Example: {
			ExampleFilterRepository<T, ID> r = (ExampleFilterRepository<T, ID>) repository;
			ExampleFilterDataProvider<T, ID> dataProvider = new ExampleFilterDataProvider<T, ID>(r,
					ExampleMatcher.matchingAny().withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
							.withIgnoreCase().withIgnoreNullValues(),
					getDefaultSortOrder());
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
			return entityClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new IllegalArgumentException("Could not instantiate class " + entityClass);
		}
	}

	public void updateCount() {

		if (showHeader && showCount) {
			long full = getCountAll();
			long shown = emptyFilter?full:dataProvider.size(new Query<>());
			count.setText("Showing " + shown + " of " + full);
		}
	}

	@SuppressWarnings("unchecked")
	public Long getCountAll() {
		LOGGER.debug("countall: {}",emptyFilter);
		switch(filterMode) {
			case Example: return repository.count();
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
	
	

	/*
		@Override
		public void setupColumns(Grid<T> grid) {
			throw new NotImplementedException();
		}

		@Override
		public void setFilter(String text) {
			throw new NotImplementedException();
		}

		@Override
		public void clearFilter() {
			throw new NotImplementedException();
		}

		@Override
		public ExampleFilterRepository<T, ID> getRepository() {
			throw new NotImplementedException();
		}

		@Override
		public String getEntityName(T t) {
			throw new NotImplementedException();
		}

		@Override
		public void delete(T t) {
			throw new NotImplementedException();
		}*/

	public String getEntityName() {
		return entityName;
	}
	
	public String getEntityNamePlural() {
		return entityNamePlural;
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

	public abstract PagingAndSortingRepository<T, ID> getRepository();

	public abstract void setupColumns(Grid<T> grid);
	
	public void setupAdvancedColumns(Grid<T> grid, Collection<Column<T>> coll) {
		
	}

	public String getEntityName(T t) {
		return null;
	};
	
	public ID getEntityId(T entity) {
		return null;
	};

	public void setFilter(String text) {};
	public void clearFilter() {}



	public Grid<T> getGrid() {
		return grid;
	}



	public HorizontalLayout getHeader() {
		return header;
	};
	
	
}
