package org.reservation.web.view;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
import javax.servlet.http.Part;

import org.reservation.web.model.categorie;

/**
 * Backing bean for categorie entities.
 * <p/>
 * This class provides CRUD functionality for all categorie entities. It focuses
 * purely on Java EE 6 standards (e.g. <tt>&#64;ConversationScoped</tt> for
 * state management, <tt>PersistenceContext</tt> for persistence,
 * <tt>CriteriaBuilder</tt> for searches) rather than introducing a CRUD
 * framework or custom base class.
 */
@Named
@Stateful
@ConversationScoped
public class categorieBean implements Serializable {

    private static final long serialVersionUID = 1L;

    /*
	 * Support creating and retrieving categorie entities
     */
    private Long id;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    private categorie categorie;

    public categorie getcategorie() {
        return this.categorie;
    }

    public void setcategorie(categorie categorie) {
        this.categorie = categorie;
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
            this.categorie = this.example;
        } else {
            this.categorie = findById(getId());
        }
    }

    public categorie findById(Long id) {

        return this.entityManager.find(categorie.class, id);
    }

    /*
	 * Support updating and deleting categorie entities
     */
    public String update() {
        this.conversation.end();

        try {
            if (this.id == null) {
                this.entityManager.persist(this.categorie);
                return "search?faces-redirect=true";
            } else {
                this.entityManager.merge(this.categorie);
                return "view?faces-redirect=true&id=" + this.categorie.getId();
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
            categorie deletableEntity = findById(getId());

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
	 * Support searching categorie entities with pagination
     */
    private int page;
    private long count;
    private List<categorie> pageItems;

    private categorie example = new categorie();

    public int getPage() {
        return this.page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return 10;
    }

    public categorie getExample() {
        return this.example;
    }

    public void setExample(categorie example) {
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
        Root<categorie> root = countCriteria.from(categorie.class);
        countCriteria = countCriteria.select(builder.count(root)).where(
                getSearchPredicates(root));
        this.count = this.entityManager.createQuery(countCriteria)
                .getSingleResult();

        // Populate this.pageItems
        CriteriaQuery<categorie> criteria = builder
                .createQuery(categorie.class);
        root = criteria.from(categorie.class);
        TypedQuery<categorie> query = this.entityManager.createQuery(criteria
                .select(root).where(getSearchPredicates(root)));
        query.setFirstResult(this.page * getPageSize()).setMaxResults(
                getPageSize());
        this.pageItems = query.getResultList();
    }

    private Predicate[] getSearchPredicates(Root<categorie> root) {

        CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
        List<Predicate> predicatesList = new ArrayList<Predicate>();

        String designation = this.example.getDesignation();
        if (designation != null && !"".equals(designation)) {
            predicatesList.add(builder.like(
                    builder.lower(root.<String>get("designation")),
                    '%' + designation.toLowerCase() + '%'));
        }

        return predicatesList.toArray(new Predicate[predicatesList.size()]);
    }

    public List<categorie> getPageItems() {
        return this.pageItems;
    }

    public long getCount() {
        return this.count;
    }

    /*
	 * Support listing and POSTing back categorie entities (e.g. from inside an
	 * HtmlSelectOneMenu)
     */
    public List<categorie> getAll() {

        CriteriaQuery<categorie> criteria = this.entityManager
                .getCriteriaBuilder().createQuery(categorie.class);
        return this.entityManager.createQuery(
                criteria.select(criteria.from(categorie.class)))
                .getResultList();
    }

    @Resource
    private SessionContext sessionContext;

    public Converter getConverter() {

        final categorieBean ejbProxy = this.sessionContext
                .getBusinessObject(categorieBean.class);

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

                return String.valueOf(((categorie) value).getId());
            }
        };
    }

    /*
	 * Support adding children to bidirectional, one-to-many tables
     */
    private categorie add = new categorie();

    public categorie getAdd() {
        return this.add;
    }

    public categorie getAdded() {
        categorie added = this.add;
        this.add = new categorie();
        return added;
    }

    private Part file1;

    public Part getFile1() {
        return file1;
    }

    public void setFile1(Part file1) {
        this.file1 = file1;
    }

    public void upload() throws IOException {
        InputStream inputStream = file1.getInputStream();
        FileOutputStream fileOutputStream = new FileOutputStream("D:\\benji.png");
        byte[] buffer = new byte[4096];
        int bytesRead = 0;
        while (true) {
            bytesRead = inputStream.read(buffer);
            if (bytesRead > 0) {
                fileOutputStream.write(buffer, 0, bytesRead);
            } else {
                break;
            }
        }
        fileOutputStream.close();
        inputStream.close();

    }

    private static String getFilename(Part part) {
        for (String cd : part.getHeader("content-disposition").split(";")) {
            if (cd.trim().startsWith("filename")) {
                String filename = cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
                return filename.substring(filename.lastIndexOf('/') + 1).substring(filename.lastIndexOf('\\') + 1); // MSIE fix.  
            }
        }
        return null;
    }

}
