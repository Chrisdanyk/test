package org.reservation.web.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateful;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.reservation.web.model.user;

/**
 * Backing bean for user entities.
 * <p/>
 * This class provides CRUD functionality for all user entities. It focuses
 * purely on Java EE 6 standards (e.g. <tt>&#64;ConversationScoped</tt> for
 * state management, <tt>PersistenceContext</tt> for persistence,
 * <tt>CriteriaBuilder</tt> for searches) rather than introducing a CRUD
 * framework or custom base class.
 */

@Named
@Stateful
@ConversationScoped
public class userBean implements Serializable {

	private static final long serialVersionUID = 1L;

	/*
	 * Support creating and retrieving user entities
	 */

	private Long id;

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	private user user;

	public user getuser() {
		return this.user;
	}

	public void setuser(user user) {
		this.user = user;
	}

	@Inject
	private Conversation conversation;

	@PersistenceContext(unitName = "reservation-pu", type = PersistenceContextType.EXTENDED)
	private EntityManager entityManager;

	public String create() {

		this.conversation.begin();
		this.conversation.setTimeout(1800000L);
		return "create?faces-redirect=true";
	}

	public void retrieve() {

		if (FacesContext.getCurrentInstance().isPostback()) {
			return;
		}

		if (this.conversation.isTransient()) {
			this.conversation.begin();
			this.conversation.setTimeout(1800000L);
		}

		if (this.id == null) {
			this.user = this.example;
		} else {
			this.user = findById(getId());
		}
	}

	public user findById(Long id) {

		return this.entityManager.find(user.class, id);
	}

	/*
	 * Support updating and deleting user entities
	 */

	public String update() {
		this.conversation.end();

		try {
			if (this.id == null) {
				this.entityManager.persist(this.user);
				return "search?faces-redirect=true";
			} else {
				this.entityManager.merge(this.user);
				return "view?faces-redirect=true&id=" + this.user.getId();
			}
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(e.getMessage()));
			return null;
		}
	}

	public String delete() {
		this.conversation.end();

		try {
			user deletableEntity = findById(getId());

			this.entityManager.remove(deletableEntity);
			this.entityManager.flush();
			return "search?faces-redirect=true";
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(e.getMessage()));
			return null;
		}
	}

	/*
	 * Support searching user entities with pagination
	 */

	private int page;
	private long count;
	private List<user> pageItems;

	private user example = new user();

	public int getPage() {
		return this.page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getPageSize() {
		return 10;
	}

	public user getExample() {
		return this.example;
	}

	public void setExample(user example) {
		this.example = example;
	}

	public String search() {
		this.page = 0;
		return null;
	}

	public void paginate() {

		CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();

		// Populate this.count

		CriteriaQuery<Long> countCriteria = builder.createQuery(Long.class);
		Root<user> root = countCriteria.from(user.class);
		countCriteria = countCriteria.select(builder.count(root)).where(
				getSearchPredicates(root));
		this.count = this.entityManager.createQuery(countCriteria)
				.getSingleResult();

		// Populate this.pageItems

		CriteriaQuery<user> criteria = builder.createQuery(user.class);
		root = criteria.from(user.class);
		TypedQuery<user> query = this.entityManager.createQuery(criteria
				.select(root).where(getSearchPredicates(root)));
		query.setFirstResult(this.page * getPageSize()).setMaxResults(
				getPageSize());
		this.pageItems = query.getResultList();
	}

	private Predicate[] getSearchPredicates(Root<user> root) {

		CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
		List<Predicate> predicatesList = new ArrayList<Predicate>();

		String username = this.example.getUsername();
		if (username != null && !"".equals(username)) {
			predicatesList.add(builder.like(
					builder.lower(root.<String> get("username")),
					'%' + username.toLowerCase() + '%'));
		}
		String password = this.example.getPassword();
		if (password != null && !"".equals(password)) {
			predicatesList.add(builder.like(
					builder.lower(root.<String> get("password")),
					'%' + password.toLowerCase() + '%'));
		}
		String role = this.example.getRole();
		if (role != null && !"".equals(role)) {
			predicatesList.add(builder.like(
					builder.lower(root.<String> get("role")),
					'%' + role.toLowerCase() + '%'));
		}

		return predicatesList.toArray(new Predicate[predicatesList.size()]);
	}

	public List<user> getPageItems() {
		return this.pageItems;
	}

	public long getCount() {
		return this.count;
	}

	/*
	 * Support listing and POSTing back user entities (e.g. from inside an
	 * HtmlSelectOneMenu)
	 */

	public List<user> getAll() {

		CriteriaQuery<user> criteria = this.entityManager.getCriteriaBuilder()
				.createQuery(user.class);
		return this.entityManager.createQuery(
				criteria.select(criteria.from(user.class))).getResultList();
	}

	@Resource
	private SessionContext sessionContext;

	public Converter getConverter() {

		final userBean ejbProxy = this.sessionContext
				.getBusinessObject(userBean.class);

		return new Converter() {

			@Override
			public Object getAsObject(FacesContext context,
					UIComponent component, String value) {

				return ejbProxy.findById(Long.valueOf(value));
			}

			@Override
			public String getAsString(FacesContext context,
					UIComponent component, Object value) {

				if (value == null) {
					return "";
				}

				return String.valueOf(((user) value).getId());
			}
		};
	}

	/*
	 * Support adding children to bidirectional, one-to-many tables
	 */

	private user add = new user();

	public user getAdd() {
		return this.add;
	}

	public user getAdded() {
		user added = this.add;
		this.add = new user();
		return added;
	}
}
