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

import org.reservation.web.model.client;

/**
 * Backing bean for client entities.
 * <p/>
 * This class provides CRUD functionality for all client entities. It focuses
 * purely on Java EE 6 standards (e.g. <tt>&#64;ConversationScoped</tt> for
 * state management, <tt>PersistenceContext</tt> for persistence,
 * <tt>CriteriaBuilder</tt> for searches) rather than introducing a CRUD
 * framework or custom base class.
 */

@Named
@Stateful
@ConversationScoped
public class clientBean implements Serializable {

	private static final long serialVersionUID = 1L;

	/*
	 * Support creating and retrieving client entities
	 */

	private Long id;

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	private client client;

	public client getclient() {
		return this.client;
	}

	public void setclient(client client) {
		this.client = client;
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
			this.client = this.example;
		} else {
			this.client = findById(getId());
		}
	}

	public client findById(Long id) {

		return this.entityManager.find(client.class, id);
	}

	/*
	 * Support updating and deleting client entities
	 */

	public String update() {
		this.conversation.end();

		try {
			if (this.id == null) {
				this.entityManager.persist(this.client);
				return "search?faces-redirect=true";
			} else {
				this.entityManager.merge(this.client);
				return "view?faces-redirect=true&id=" + this.client.getId();
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
			client deletableEntity = findById(getId());

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
	 * Support searching client entities with pagination
	 */

	private int page;
	private long count;
	private List<client> pageItems;

	private client example = new client();

	public int getPage() {
		return this.page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getPageSize() {
		return 10;
	}

	public client getExample() {
		return this.example;
	}

	public void setExample(client example) {
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
		Root<client> root = countCriteria.from(client.class);
		countCriteria = countCriteria.select(builder.count(root)).where(
				getSearchPredicates(root));
		this.count = this.entityManager.createQuery(countCriteria)
				.getSingleResult();

		// Populate this.pageItems

		CriteriaQuery<client> criteria = builder.createQuery(client.class);
		root = criteria.from(client.class);
		TypedQuery<client> query = this.entityManager.createQuery(criteria
				.select(root).where(getSearchPredicates(root)));
		query.setFirstResult(this.page * getPageSize()).setMaxResults(
				getPageSize());
		this.pageItems = query.getResultList();
	}

	private Predicate[] getSearchPredicates(Root<client> root) {

		CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
		List<Predicate> predicatesList = new ArrayList<Predicate>();

		String nom = this.example.getNom();
		if (nom != null && !"".equals(nom)) {
			predicatesList.add(builder.like(
					builder.lower(root.<String> get("nom")),
					'%' + nom.toLowerCase() + '%'));
		}
		String prenom = this.example.getPrenom();
		if (prenom != null && !"".equals(prenom)) {
			predicatesList.add(builder.like(
					builder.lower(root.<String> get("prenom")),
					'%' + prenom.toLowerCase() + '%'));
		}
		String email = this.example.getEmail();
		if (email != null && !"".equals(email)) {
			predicatesList.add(builder.like(
					builder.lower(root.<String> get("email")),
					'%' + email.toLowerCase() + '%'));
		}
		String telephone = this.example.getTelephone();
		if (telephone != null && !"".equals(telephone)) {
			predicatesList.add(builder.like(
					builder.lower(root.<String> get("telephone")),
					'%' + telephone.toLowerCase() + '%'));
		}
		String profession = this.example.getProfession();
		if (profession != null && !"".equals(profession)) {
			predicatesList.add(builder.like(
					builder.lower(root.<String> get("profession")),
					'%' + profession.toLowerCase() + '%'));
		}

		return predicatesList.toArray(new Predicate[predicatesList.size()]);
	}

	public List<client> getPageItems() {
		return this.pageItems;
	}

	public long getCount() {
		return this.count;
	}

	/*
	 * Support listing and POSTing back client entities (e.g. from inside an
	 * HtmlSelectOneMenu)
	 */

	public List<client> getAll() {

		CriteriaQuery<client> criteria = this.entityManager
				.getCriteriaBuilder().createQuery(client.class);
		return this.entityManager.createQuery(
				criteria.select(criteria.from(client.class))).getResultList();
	}

	@Resource
	private SessionContext sessionContext;

	public Converter getConverter() {

		final clientBean ejbProxy = this.sessionContext
				.getBusinessObject(clientBean.class);

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

				return String.valueOf(((client) value).getId());
			}
		};
	}

	/*
	 * Support adding children to bidirectional, one-to-many tables
	 */

	private client add = new client();

	public client getAdd() {
		return this.add;
	}

	public client getAdded() {
		client added = this.add;
		this.add = new client();
		return added;
	}
}
