package org.reservation.web.model;

import javax.persistence.Entity;
import java.io.Serializable;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;
import javax.persistence.Version;
import org.reservation.web.model.client;
import javax.persistence.OneToOne;
import org.reservation.web.model.chambre;
import java.util.Date;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.CascadeType;

@Entity
public class reservation implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", updatable = false, nullable = false)
	private Long id;
	@Version
	@Column(name = "version")
	private int version;

	@OneToOne(cascade = CascadeType.ALL)
	private client client;

	@OneToOne(cascade = CascadeType.ALL)
	private chambre chambre;

	@Column
	@Temporal(TemporalType.DATE)
	private Date date_reservation;

	@Column
	@Temporal(TemporalType.DATE)
	private Date date_arrivee;

	@Column
	private int nombre_nuits;

	@Column
	private String etat;

	public Long getId() {
		return this.id;
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
		if (!(obj instanceof reservation)) {
			return false;
		}
		reservation other = (reservation) obj;
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

	public client getClient() {
		return client;
	}

	public void setClient(client client) {
		this.client = client;
	}

	public chambre getChambre() {
		return chambre;
	}

	public void setChambre(chambre chambre) {
		this.chambre = chambre;
	}

	public Date getDate_reservation() {
		return date_reservation;
	}

	public void setDate_reservation(Date date_reservation) {
		this.date_reservation = date_reservation;
	}

	public Date getDate_arrivee() {
		return date_arrivee;
	}

	public void setDate_arrivee(Date date_arrivee) {
		this.date_arrivee = date_arrivee;
	}

	public int getNombre_nuits() {
		return nombre_nuits;
	}

	public void setNombre_nuits(int nombre_nuits) {
		this.nombre_nuits = nombre_nuits;
	}

	public String getEtat() {
		return etat;
	}

	public void setEtat(String etat) {
		this.etat = etat;
	}

	@Override
	public String toString() {
		String result = getClass().getSimpleName() + " ";
		result += "nombre_nuits: " + nombre_nuits;
		if (etat != null && !etat.trim().isEmpty())
			result += ", etat: " + etat;
		return result;
	}

	public void newClient() {
		this.client = new client();
	}

	public void newChambre() {
		this.chambre = new chambre();
	}
}