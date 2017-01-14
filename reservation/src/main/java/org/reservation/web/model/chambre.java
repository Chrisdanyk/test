package org.reservation.web.model;

import javax.persistence.Entity;
import java.io.Serializable;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;
import javax.persistence.Version;
import org.reservation.web.model.categorie;
import javax.persistence.ManyToOne;

@Entity
public class chambre implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;
    @Version
    @Column(name = "version")
    private int version;

    @Column
    private int numero;

    @ManyToOne
    private categorie categorie;

    @Column
    private String reservee;

    @Column
    private String description;

    public Long getId() {
        return this.id;
    }

    public String getDescription() {
        return description;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public int getVersion() {
        return this.version;
    }

    public void setVersion(final int version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof chambre)) {
            return false;
        }
        chambre other = (chambre) obj;
        if (id != null) {
            if (!id.equals(other.id)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    public categorie getCategorie() {
        return this.categorie;
    }

    public void setCategorie(final categorie categorie) {
        this.categorie = categorie;
    }

    public String getReservee() {
        return reservee;
    }

    public void setReservee(String reservee) {
        this.reservee = reservee;
    }

    @Override
    public String toString() {
        String result = getClass().getSimpleName() + " ";
        if (reservee != null && !reservee.trim().isEmpty()) {
            result += "reservee: " + reservee;
        }
        return result;
    }
}
