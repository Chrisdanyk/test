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

import org.reservation.web.model.chambre;
import org.reservation.web.model.categorie;

/**
 * Backing bean for chambre entities.
 * <p/>
 * This class provides CRUD functionality for all chambre entities. It focuses
 * purely on Java EE 6 standards (e.g. <tt>&#64;ConversationScoped</tt> for
 * state management, <tt>PersistenceContext</tt> for persistence,
 * <tt>CriteriaBuilder</tt> for searches) rather than introducing a CRUD
 * framework or custom base class.
 */
@Named
@Stateful
@ConversationScoped
public class chambreBean implements Serializable {
    
    private static final long serialVersionUID = 1L;

    /*
	 * Support creating and retrieving chambre entities
     */
    private Long id;
    
    public Long getId() {
        return this.id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    private chambre chambre;
    
    public chambre getchambre() {
        return this.chambre;
    }
    
    public void setchambre(chambre chambre) {
        this.chambre = chambre;
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
            this.chambre = this.example;
        } else {
            this.chambre = findById(getId());
        }
    }
    
    public chambre findById(Long id) {
        
        return this.entityManager.find(chambre.class, id);
    }

    /*
	 * Support updating and deleting chambre entities
     */
    public String update() {
        this.conversation.end();
        
        try {
            if (this.id == null) {
                this.chambre.setReservee("NON");
                this.entityManager.persist(this.chambre);
                System.out.println("benjamin");
                return "search?faces-redirect=true";
            } else {
                this.entityManager.merge(this.chambre);
                return "view?faces-redirect=true&id=" + this.chambre.getId();
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
            chambre deletableEntity = findById(getId());
            
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
	 * Support searching chambre entities with pagination
     */
    private int page;
    private long count;
    private List<chambre> pageItems;
    
    private chambre example = new chambre();
    
    public int getPage() {
        return this.page;
    }
    
    public void setPage(int page) {
        this.page = page;
    }
    
    public int getPageSize() {
        return 10;
    }
    
    public chambre getExample() {
        return this.example;
    }
    
    public void setExample(chambre example) {
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
        Root<chambre> root = countCriteria.from(chambre.class);
        countCriteria = countCriteria.select(builder.count(root)).where(
                getSearchPredicates(root));
        this.count = this.entityManager.createQuery(countCriteria)
                .getSingleResult();

        // Populate this.pageItems
        CriteriaQuery<chambre> criteria = builder.createQuery(chambre.class);
        root = criteria.from(chambre.class);
        TypedQuery<chambre> query = this.entityManager.createQuery(criteria
                .select(root).where(getSearchPredicates(root)));
        query.setFirstResult(this.page * getPageSize()).setMaxResults(
                getPageSize());
        this.pageItems = query.getResultList();
    }
    
    private Predicate[] getSearchPredicates(Root<chambre> root) {
        
        CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
        List<Predicate> predicatesList = new ArrayList<Predicate>();
        
        categorie categorie = this.example.getCategorie();
        if (categorie != null) {
            predicatesList.add(builder.equal(root.get("categorie"), categorie));
        }
        String reservee = this.example.getReservee();
        if (reservee != null && !"".equals(reservee)) {
            predicatesList.add(builder.like(
                    builder.lower(root.<String>get("reservee")),
                    '%' + reservee.toLowerCase() + '%'));
        }
        
        return predicatesList.toArray(new Predicate[predicatesList.size()]);
    }
    
    public List<chambre> getPageItems() {
        return this.pageItems;
    }
    
    public long getCount() {
        return this.count;
    }

    /*
	 * Support listing and POSTing back chambre entities (e.g. from inside an
	 * HtmlSelectOneMenu)
     */
    public List<chambre> getAll() {
        
        CriteriaQuery<chambre> criteria = this.entityManager
                .getCriteriaBuilder().createQuery(chambre.class);
        return this.entityManager.createQuery(
                criteria.select(criteria.from(chambre.class))).getResultList();
    }
    
    @Resource
    private SessionContext sessionContext;
    
    public Converter getConverter() {
        
        final chambreBean ejbProxy = this.sessionContext
                .getBusinessObject(chambreBean.class);
        
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
                
                return String.valueOf(((chambre) value).getId());
            }
        };
    }

    /*
	 * Support adding children to bidirectional, one-to-many tables
     */
    private chambre add = new chambre();
    
    public chambre getAdd() {
        return this.add;
    }
    
    public chambre getAdded() {
        chambre added = this.add;
        this.add = new chambre();
        return added;
    }
}
