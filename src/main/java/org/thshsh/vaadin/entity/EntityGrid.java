package org.thshsh.vaadin.entity;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.Repository;
import org.thshsh.vaadin.data.CustomCallbackDataProvider;
import org.thshsh.vaadin.data.ExampleSpecification;
import org.thshsh.vaadin.data.SpecificationDataProvider;
import org.thshsh.vaadin.data.StringSearchDataProvider;
import org.thshsh.vaadin.data.StringSearchRepository;
import org.thshsh.vaadin.entity.ConfirmDialog.ButtonConfig;
import org.thshsh.vaadin.entity.ConfirmDialog.ConfirmDialogRunnable;
import org.vaadin.addons.thshsh.hovercolumn.HoverColumn;

import com.vaadin.componentfactory.ToggleButton;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentUtil;
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
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.grid.GridMultiSelectionModel;
import com.vaadin.flow.component.grid.GridMultiSelectionModel.SelectAllCheckboxVisibility;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.IconFactory;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.shared.Tooltip.TooltipPosition;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.provider.QuerySortOrderBuilder;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.router.RouteConfiguration;

import elemental.json.JsonValue;
import lombok.SneakyThrows;

/**
 * This is a component that can list entities in a table with a button column for operations
 * It needs a "Provider" implementation to be passed to it to to delegate parts of the component
 *
 * TODO need to refactor this so that the filter is passed into the query
 * this makes this class simpler but means that each provider needs to now respect the query filter and apply it on the fly
 * whereas right now the filter is applied at the time the user enters it into the textfield
 *
 * @param <T>
 * @param <ID>
 */
@SuppressWarnings("serial")
@CssImport(value="./entity-grid.css")
@CssImport(value = "./entity-grid-vaadin-grid.css",themeFor = "vaadin-grid")
@CssImport(value = "./entity-grid-vcf-toggle-button.css",themeFor = "vcf-toggle-button")
public abstract class EntityGrid<T, ID extends Serializable> extends VerticalLayout {
 

	public static class Styles {

		public static final String GRID_BORDERLESS = "borderless";
		public static final String BUTTON_COLUMN = "button-column";
		public static final String GRID_BUTTONS_COLUMN = "grid-buttons-column";
		public static final String GRID_BUTTONS = "grid-buttons";
		public static final String GRID_ENTITY_GRID = "entity-grid";
		public static final String GRID_BUTTON_INVISIBLE = "invisible";
		
	}
	
	public static final Logger LOGGER = LoggerFactory.getLogger(EntityGrid.class);

	public enum FilterMode {
		String, Example, None;
	}
	
	@Autowired
	protected ApplicationContext appCtx;

	protected VaadinIcon deleteIcon = VaadinIcon.TRASH;
	protected VaadinIcon editIcon = VaadinIcon.PENCIL;
	protected String editText = "Edit";
	protected String deleteText = "Delete";
	protected EntityOperation<T> deleteOperation;
	protected EntityOperation<T> editOperation;
	protected List<EntityOperation<T>> operations = new ArrayList<>();
	protected Repository<T, ID> repository;
	
	/**
	 * Root data provider is for allowing the implementations to customize the combineFilters method, which may not exist
	 * on the baseDataProvider once it has been wrapped in filtering callbacks. This field wont be necessary if we come up 
	 * with some other way to pass the combineFilters logic
	 */
	protected DataProvider<T, ?> rootDataProvider;
	/**
	 * Base data provider allows setting a filter that is used for all queries, and which the filtered data provider will 
	 * automatically inherit
	 */
	protected DataProvider<T, ?> baseDataProvider;
	/**
	 * The filtered data provider is what is actually used to populate the grid and applies any filters the user provides 
	 * via the filter field.
	 */
	protected DataProvider<T, ?> filteredDataProvider;

	protected T filterEntity;
	protected Class<? extends Component> entityView;
	protected Grid<T> grid;
	protected Boolean defaultSortAsc = true;
	protected List<String> defaultSortOrderProperties = null;
	protected List<QuerySortOrder> defaultSortOrders;
	protected Boolean appendButtonColumn = false;
	protected Boolean showEditButton = true;
	protected Boolean showDeleteButton = true;
	protected Boolean showCreateButton = true;
	protected Boolean showHeader = true;
	protected Boolean showHeaderButtons = true;
	protected Boolean showCount = true;
	protected Boolean showFilter = true;
	protected Boolean columnsResizable = null;
	protected Span count;
	protected TextField filter;
	protected Column<T> buttonColumn;
	protected String createText = "New";
	protected HorizontalLayout header;
	protected HorizontalLayout headerOperationButtonsLayout;
	protected List<Button> headerOperationButtons = new ArrayList<>();
	protected Span countAndAdvanced;
	protected SelectionMode selectionMode;

	protected Button addButton;
	protected ToggleButton advancedButton;
	protected Collection<Column<T>> advancedColumns = new ArrayList<>();

	protected FilterMode filterMode;
	protected ExampleMatcher matcher = ExampleMatcher
            .matchingAny()
            .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
            .withIgnoreCase()
            .withIgnoreNullValues();

	protected Boolean caseSensitive = false;
	protected Boolean emptyFilter = true;
	
	protected EntityDescriptor<T, ID> descriptor;
	
	protected String gridButtonsColumnClasses = Styles.GRID_BUTTONS_COLUMN+" "+HoverColumn.HOVER_COLUMN_CLASS;

	protected String buttonColumnTemplate;
	
	public EntityGrid(Class<? extends Component> ev,FilterMode fm, String sortProp) {
		this.entityView = ev;
		this.filterMode = fm;
		this.defaultSortOrderProperties = sortProp != null ? List.of(sortProp) : null;
	}
	
	/**
	 * This method can be called by child classes to automatically generate the descriptor (not recommended) rather than autowire it.
	 * @param entityClass
	 */
	protected void generateEntityDescriptor(Class<T> entityClass) {
		this.descriptor = new EntityDescriptor<T, ID>(entityClass);
	}

	@SuppressWarnings({ "deprecation" })
	@PostConstruct
	@SneakyThrows(value = IOException.class)
	public void postConstruct() {

		buttonColumnTemplate = IOUtils.toString(appCtx.getResource("classpath:META-INF/resources/frontend/button-column-cell.lit.html").getInputStream(),StandardCharsets.UTF_8);

		if(defaultSortOrders == null) defaultSortOrders = createDefaultSortOrders();
		
	    add(new HoverColumn());
	    
		this.setWidthFull();
		LOGGER.debug("postConstruct");

		this.addClassName(Styles.GRID_ENTITY_GRID);

		createOperations();
		
		rootDataProvider = createRootDataProvider();
		
		baseDataProvider = createBaseDataProvider();
		
		LOGGER.debug("baseDataProvider: {}",baseDataProvider);
		
		filteredDataProvider = createFilteredDataProvider();

		
		LOGGER.debug("filteredDataProvider: {}",filteredDataProvider.getClass());

		if (showHeader) {
			header = new HorizontalLayout();
			header.setSpacing(true);
			header.setWidth("100%");
			header.setAlignItems(Alignment.CENTER);
			this.add(header);
			
			if(showHeaderButtons) {
				headerOperationButtonsLayout = new HorizontalLayout();
				//headerButtons.setVisible(false);
				header.add(headerOperationButtonsLayout);
				for(EntityOperation<T> operation : operations) {
					if(!operation.isSingular() && !operation.isHide()) {
						//TODO enabled logic needs to consider if items are selected
						Button button = createOperationButton(operation);
						button.setIcon(operation.createIcon(null));
						button.addClickListener(click -> executeOperation(operation, grid.getSelectedItems()));
						headerOperationButtonsLayout.add(button);
						headerOperationButtons.add(button);
					}
				}
				updateHeaderOperationButtons();
			}
			
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
		grid.setDataProvider(filteredDataProvider);
		grid.addThemeVariants(
				//GridVariant.LUMO_NO_ROW_BORDERS,
				GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_ROW_STRIPES);
		grid.addClassName(Styles.GRID_BORDERLESS);
		grid.setHeight("100%");
		grid.setWidthFull();


		setSelectionMode(SelectionMode.MULTI);
		grid.addSelectionListener(select -> {
			updateHeaderOperationButtons();
		});

		setupColumns(grid);
		
		if(columnsResizable!=null) {
			grid.getColumns().forEach(col -> {
				col.setResizable(columnsResizable);
			});
		}
		
		this.advancedColumns.forEach(col -> {
			col.setVisible(false);
		});

		if (appendButtonColumn) {
			addStandardButtonsColumn();
		}


		Shortcuts.addShortcutListener(grid, () -> {
			grid.getSelectedItems().stream().findFirst().ifPresent(e -> {
				shortcutDetails(e);
			});
		}, Key.KEY_I, KeyModifier.CONTROL);

		this.add(grid);

		updateCount();

	}
	
	protected void createOperations() {
		
		deleteOperation = EntityOperation.<T>builder()
				.icon(deleteIcon)
				.name(deleteText)
				.display(showDeleteButton)
				.operation(this::delete)
				.confirm(true)
				.build();
		
		editOperation = EntityOperation.<T>builder()
				.icon(editIcon)
				.name(editText)
				.display(showEditButton)
				.singularOperation(this::edit)
				.build();
		
		this.operations.add(deleteOperation);
		this.operations.add(editOperation);
	}
	
	protected Button createOperationButton(EntityOperation<T> operation) {
		Button button = createButton(operation.getName());
		ComponentUtil.setData(button, EntityOperation.class, operation);
		return button;
	}
	
	protected void updateHeaderOperationButtons() {
		if(selectionMode == SelectionMode.MULTI) {
			boolean selection = grid.getSelectedItems().size()>0;
			headerOperationButtons.forEach(button -> {
				EntityOperation<?> operation = ComponentUtil.getData(button, EntityOperation.class);
				button.setVisible(selection || operation.isAll());
			});
		}
		else {
			//hide operations if selection mode is single since they will be present in row?
			//TODO should udpate this in case we dont want to show a button row 
			//singular operations shouldnt be present in this list to begin with
			headerOperationButtons.forEach(button -> {
				button.setVisible(false);
			});
			
		}
	}

	public void refresh() {
		filteredDataProvider.refreshAll();
		updateCount();
	}
	
	public void refresh(T entity) {
		filteredDataProvider.refreshItem(entity);
	}
	
	public Column<T> addButtonsColumn() {
		grid.addClassName(Styles.BUTTON_COLUMN);

		List<List<String>> operationsData = new ArrayList<>();
		
		for(EntityOperation<T> operation : operations) {
			List<String> data = new ArrayList<>();
			data.add(operation.getName());
			IconFactory iconFact = operation.getIcon(null);
			if(iconFact instanceof VaadinIcon) {
				data.add("vaadin");
				data.add(((VaadinIcon)operation.getIcon(null)).name().toLowerCase(Locale.ENGLISH).replace('_', '-'));				
			}
			else if(iconFact instanceof EntityIconFactory) {
				data.add(((EntityIconFactory)iconFact).collectionName());
				data.add(((EntityIconFactory)operation.getIcon(null)).name().toLowerCase(Locale.ENGLISH).replace('_', '-'));
			}
			else {
				throw new IllegalArgumentException("IconFactory must be VaadinIcon or EntityIconFactory");
			}
			operationsData.add(data);
		}
		
		
		buttonColumn = grid.addColumn(
				LitRenderer.<T>of(buttonColumnTemplate)
				.withFunction("click", (t,args) -> {
					JsonValue value = args.get(0);
					LOGGER.info("clicked: {}",value.asString());
					for(EntityOperation<T> op : operations) {
						if(op.getName().equals(value.asString())) {
							executeOperation(op, List.of(t));
							break;
						}
					}
					
				})
				//.withProperty("operations", t -> operationData)
				.withProperty("operations", t -> {
					List<List<String>> data = new ArrayList<>();
					
					for(int i=0;i<operations.size();i++) {
						EntityOperation<T> operation = operations.get(i);
						if(operation.isDisplay(t)) {
							List<String> operationData = operationsData.get(i);
							List<String> newopData = new ArrayList<>(operationData);
							String hide = ""+operation.isHide(t);
							String enable = ""+!operation.isEnabled(t);
							newopData.add(hide);
							newopData.add(enable);
							data.add(newopData);
						}
					}
					
					return data;
				})
				
		)
		.setWidth("150px")
		.setFlexGrow(0)
		.setClassNameGenerator(this::getButtonsColumnClasses)
		.setAutoWidth(true);
		
		
		return buttonColumn;
		
	}

	public Column<T> addStandardButtonsColumn() {
		return addButtonsColumn();
	}
	
	public String getButtonsColumnClasses(T e) {
		return gridButtonsColumnClasses;
	}
	

	protected void executeOperation(EntityOperation<T> operation, Collection<T> e) {
		if(operation.getCheckFunction()!=null) {
			ValidationResult result = operation.getCheckFunction().apply(e);
			if(result.isError()) {
				createCheckResponseDialog(result.getErrorMessage()).open();
				return;
			}
		}
		if(operation.isConfirm()) {
			String nameString;
			if(e.size()==1) {
				T t = e.iterator().next();
				nameString = descriptor.getEntityTypeName();
				String entityName = descriptor.getEntityName(t);
				if(entityName  != null) nameString += " \"" + entityName +"\"";
			}
			else if(e.size() == 0) {
				nameString = "All "+descriptor.getEntityTypeNamePlural();
			}
			else {
				nameString = e.size()+" "+descriptor.getEntityTypeNamePlural();
			}
			
			ConfirmDialog cd = ConfirmDialogs.yesNoDialog(operation.getName()+ " "+Objects.requireNonNullElse(operation.getPreposition(), StringUtils.EMPTY)+" "+nameString+" ?", (ConfirmDialogRunnable) (d,b) -> {
				operation.operation.accept(e);
			});
			cd.open();
		}
		else {
			operation.operation.accept(e);
		}
	}
	

	protected Button createButton(String tooltip) {
		Button button = new Button();
		button.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
		button.setTooltipText(tooltip).setPosition(TooltipPosition.TOP);
		return button;
	}
	
	protected ConfirmDialog createCheckResponseDialog(String message) {
		return ConfirmDialogs
		.messageDialog(null, message);
	}
	
	@SuppressWarnings("unchecked")
	public void shortcutDetails(T e) {
		DetailsDialog<T,ID> dd = appCtx.getBean(DetailsDialog.class,e,this);
		dd.open();
	}

	
	public void delete(Collection<T> e,ConfirmDialog d,ButtonConfig bc) {
		delete(e);
	}

	public void delete(Collection<T> e) {
	    if(repository instanceof CrudRepository) {
    		((CrudRepository<T,ID>)repository).deleteAllById(e.stream().map(descriptor::getEntityId).collect(Collectors.toList()));
    		refresh();
	    }
	    else throw new UnsupportedOperationException("Cannot delete entities unless repository is CrudRepository");
	}

	public void edit(T entity) {
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
	

	public DataProvider<T, ?> getFilteredDataProvider() {
		return filteredDataProvider;
	}
	
	@SuppressWarnings("unchecked")
    protected DataProvider<T, ?> createRootDataProvider() {
		
		LOGGER.debug("createBaseDataProvider: {}",filterMode);
	    
	    switch (filterMode) {
            case Example: {
     
                if(repository instanceof JpaSpecificationExecutor) {
                    SpecificationDataProvider<T> dataProvider = new SpecificationDataProvider<>((JpaSpecificationExecutor<T>)repository,defaultSortOrders);
                    return dataProvider;
                }
                
                //TODO need to attempt handle QueryByExampleExecutor
				/*if(repository instanceof QueryByExampleExecutor) {
				    SpecificationDataProvider<T> dataProvider = new SpecificationDataProvider<>((JpaSpecificationExecutor<T>)repository,defaultSortOrders);
				    return dataProvider;
				}*/
                
                break;
            }
            case String: {
                if(repository instanceof StringSearchRepository) {           
                    StringSearchRepository<T,ID> ssr = (StringSearchRepository<T, ID>) repository;
                    StringSearchDataProvider<T> dataProvider = new StringSearchDataProvider<T>(ssr, defaultSortOrders);
                    return dataProvider;
                }
            }
            case None: {
            	//repo at least needs to have paging and sorting to work with this api
            	//prefer the JpaSpecificationExecutor if available
            	if(repository instanceof JpaSpecificationExecutor) {
                    SpecificationDataProvider<T> dataProvider = new SpecificationDataProvider<>((JpaSpecificationExecutor<T>)repository,defaultSortOrders);
                    return dataProvider;
                }
            	else if(repository instanceof PagingAndSortingRepository) {
                    CustomCallbackDataProvider<T, Void> dataProvider = new CustomCallbackDataProvider<T, Void>(
                    		(f,p) -> ((PagingAndSortingRepository<T, ID>)repository).findAll(p), 
                    		f -> ((PagingAndSortingRepository<T, ID>)repository).count(), 
                    		null, 
                    		defaultSortOrders);
                    return dataProvider;
                }
            }
            default: 

	    }
    	    
	    throw new IllegalStateException("Could not create Data Provider");
		
	}

    protected DataProvider<T, ?> createBaseDataProvider() {
		LOGGER.debug("createBaseDataProvider: {}",filterMode);
	    return rootDataProvider;
	}

	
	@SuppressWarnings("unchecked")
	protected DataProvider<T, ?> createFilteredDataProvider() {
	    
		LOGGER.debug("createFilteredDataProvider: {}",filterMode);

    	switch (filterMode) {
    		case Example: {
	
    			if(rootDataProvider instanceof SpecificationDataProvider) {
    				//if its a SpecificationDataProvider then we use the combineFilters method to allow the data provider to customize the combination
    				
    				SpecificationDataProvider<T> root = (SpecificationDataProvider<T>) rootDataProvider;
    				
    				filterEntity = createFilterEntity();

    				DataProvider<T,Specification<T>> ex = (DataProvider<T, Specification<T>>) baseDataProvider;

     		        //allow a configurable example filter that can combine with the base specification
     		        ConfigurableFilterDataProvider<T,Specification<T>,ExampleSpecification<T>> config = ex.withConfigurableFilter(root::combineFilters);
     		        
     		        //allow converting from entity to ExampleSpecification
					 DataProvider<T,T> en = config.withConvertedFilter(filter -> {
					 	return ExampleSpecification.of(filter, matcher);
					 });
     		        //allow configurable entity filter 
     		        ConfigurableFilterDataProvider<T,Void,T> ec = en.withConfigurableFilter();
     		        
     		        //set filter entity
     		        ec.setFilter(filterEntity);
     		      
     		    
         			return ec;
    			}
    			else {
    				//here we assume that the data provider filter type is the entity type
    				//it would be best to have some way to check that this is correct
    				filterEntity = createFilterEntity();
    				DataProvider<T, T> dataProvider = (DataProvider<T, T>) baseDataProvider;
    				
    				ConfigurableFilterDataProvider<T,Void,T> filteredDataProvider = dataProvider.withConfigurableFilter();
    				filteredDataProvider.setFilter(filterEntity);
    				
    				return filteredDataProvider;
    			}
    		}
    		case String: {
    			if(baseDataProvider instanceof StringSearchDataProvider) {
    				StringSearchDataProvider<T> dp = (StringSearchDataProvider<T>) baseDataProvider;
    				//use the combined filter method in the DP if possible
    				ConfigurableFilterDataProvider<T,String,String> fdp = dp.withConfigurableFilter(dp::combineStringFilters);
    				 return fdp;
    			}
    			else {
    				ConfigurableFilterDataProvider<T,Void,String> fdp = ((DataProvider<T, String>)baseDataProvider).withConfigurableFilter();
    				return fdp;
    			}
    		}
    		case None: {
    		    return baseDataProvider;
    		}
    		default: 

		}
		throw new IllegalStateException("Could not create filtered DataProvider for filterMode: "+filterMode+" and baseDataProvider: "+baseDataProvider);

	}
	
	

	protected List<QuerySortOrder> createDefaultSortOrders() {
		QuerySortOrderBuilder builder = new QuerySortOrderBuilder();
		if(defaultSortOrderProperties != null) {
			defaultSortOrderProperties.forEach(prop -> {
				if (defaultSortAsc) builder.thenAsc(prop);
				else builder.thenDesc(prop);
			});
		}
		return builder.build();
	}

	public T createFilterEntity() {
		try {
			return descriptor.getEntityClass().getConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			throw new IllegalArgumentException("Could not instantiate class for filter: " + descriptor.getEntityClass());
		}
	}

	public void updateCount() {
		if (showHeader && showCount) {
			LOGGER.debug("updateCount");
			long full = (long) baseDataProvider.size(new Query<>());
			long shown = emptyFilter?full:(long) filteredDataProvider.size(new Query<>());
			count.setText("Showing " + shown + " of " + full);
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
				//for this filter we dont need to do anything as the spec will be updated by the data provider
				break;
			case String:
			    ((ConfigurableFilterDataProvider<T, String, String>)filteredDataProvider).setFilter(text);
				break;
			case None:
				break;
			default:
				break;
		}

		filteredDataProvider.refreshAll();
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

	public Repository<T, ID> getRepository(){
		return repository;
	}

	public abstract void setupColumns(Grid<T> grid);
	
	public void setupAdvancedColumns(Grid<T> grid, Collection<Column<T>> coll) {}

	public void setFilter(String text) {}
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

	public void setRepository(Repository<T, ID> repository) {
		this.repository = repository;
	}
	
	public void setSelectionMode(SelectionMode sm) {
		this.selectionMode = sm;
		grid.setSelectionMode(sm);
		if(selectionMode == SelectionMode.MULTI) {
			//show the checkall box - clicking it may be slow for large data sets
			((GridMultiSelectionModel<T>)grid.getSelectionModel()).setSelectAllCheckboxVisibility(SelectAllCheckboxVisibility.VISIBLE);
		}
		updateHeaderOperationButtons();
	}
	
}
