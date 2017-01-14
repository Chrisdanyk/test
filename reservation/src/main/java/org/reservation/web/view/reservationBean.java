package org.reservation.web.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
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
import org.reservation.web.model.chambre;
import org.reservation.web.model.client;

import org.reservation.web.model.reservation;

/**
 * Backing bean for reservation entities.
 * <p/>
 * This class provides CRUD functionality for all reservation entities. It
 * focuses purely on Java EE 6 standards (e.g. <tt>&#64;ConversationScoped</tt>
 * for state management, <tt>PersistenceContext</tt> for persistence,
 * <tt>CriteriaBuilder</tt> for searches) rather than introducing a CRUD
 * framework or custom base class.
 */
@Named
@Stateful
@ConversationScoped
public class reservationBean implements Serializable {

    private static final long serialVersionUID = 1L;

    /*
	 * Support creating and retrieving reservation entities
     */
    private Long id;

    private client client;
    private chambre chambre;

    public chambre getChambre() {
        if (chambre == null) {
            chambre = new chambre();
        }
        return chambre;
    }

    public void setChambre(chambre chambre) {
        this.chambre = chambre;
    }

    public client getClient() {
        if (client == null) {
            client = new client();
        }
        return client;
    }

    public void setClient(client client) {
        this.client = client;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    private reservation reservation;

    public reservation getreservation() {
        return this.reservation;
    }

    public void setreservation(reservation reservation) {
        this.reservation = reservation;
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
            this.reservation = this.example;
        } else {
            this.reservation = findById(getId());
        }
    }

    public reservation findById(Long id) {

        return this.entityManager.find(reservation.class, id);
    }

    /*
	 * Support updating and deleting reservation entities
     */
    public String update() {
        this.conversation.end();

        try {
            if (this.id == null) {

                this.reservation.setClient(this.getClient());
                this.getChambre().setReservee("OUI");
                this.reservation.setChambre(this.getChambre());
                this.reservation.setDate_reservation(new Date());
                this.reservation.setEtat("EN COURS");
                this.entityManager.persist(this.reservation);
                return "search?faces-redirect=true";
            } else {
                this.entityManager.merge(this.reservation);
                return "view?faces-redirect=true&id="
                        + this.reservation.getId();
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
            reservation deletableEntity = findById(getId());

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
	 * Support searching reservation entities with pagination
     */
    private int page;
    private long count;
    private List<reservation> pageItems;

    private reservation example = new reservation();

    public int getPage() {
        return this.page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return 10;
    }

    public reservation getExample() {
        return this.example;
    }

    public void setExample(reservation example) {
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
        Root<reservation> root = countCriteria.from(reservation.class);
        countCriteria = countCriteria.select(builder.count(root)).where(
                getSearchPredicates(root));
        this.count = this.entityManager.createQuery(countCriteria)
                .getSingleResult();

        // Populate this.pageItems
        CriteriaQuery<reservation> criteria = builder
                .createQuery(reservation.class);
        root = criteria.from(reservation.class);
        TypedQuery<reservation> query = this.entityManager.createQuery(criteria
                .select(root).where(getSearchPredicates(root)));
        query.setFirstResult(this.page * getPageSize()).setMaxResults(
                getPageSize());
        this.pageItems = query.getResultList();
    }

    private Predicate[] getSearchPredicates(Root<reservation> root) {

        CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
        List<Predicate> predicatesList = new ArrayList<Predicate>();

        int nombre_nuits = this.example.getNombre_nuits();
        if (nombre_nuits != 0) {
            predicatesList.add(builder.equal(root.get("nombre_nuits"),
                    nombre_nuits));
        }
        String etat = this.example.getEtat();
        if (etat != null && !"".equals(etat)) {
            predicatesList.add(builder.like(
                    builder.lower(root.<String>get("etat")),
                    '%' + etat.toLowerCase() + '%'));
        }

        return predicatesList.toArray(new Predicate[predicatesList.size()]);
    }

    public List<reservation> getPageItems() {
        return this.pageItems;
    }

    public long getCount() {
        return this.count;
    }

    /*
	 * Support listing and POSTing back reservation entities (e.g. from inside
	 * an HtmlSelectOneMenu)
     */
    public List<reservation> getAll() {

        CriteriaQuery<reservation> criteria = this.entityManager
                .getCriteriaBuilder().createQuery(reservation.class);
        return this.entityManager.createQuery(
                criteria.select(criteria.from(reservation.class)))
                .getResultList();
    }

    @Resource
    private SessionContext sessionContext;

    public Converter getConverter() {

        final reservationBean ejbProxy = this.sessionContext
                .getBusinessObject(reservationBean.class);

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

                return String.valueOf(((reservation) value).getId());
            }
        };
    }

    /*
	 * Support adding children to bidirectional, one-to-many tables
     */
    private reservation add = new reservation();

    public reservation getAdd() {
        return this.add;
    }

    public reservation getAdded() {
        reservation added = this.add;
        this.add = new reservation();
        return added;
    }
}
