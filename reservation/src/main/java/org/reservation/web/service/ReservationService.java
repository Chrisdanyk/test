/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.reservation.web.service;

import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.reservation.web.model.reservation;

/**
 *
 * @author Ben Kafirongo
 */
public class ReservationService extends AbstractService<reservation> {

    public ReservationService() {
        super(reservation.class);
    }

    @Override
    protected Predicate[] getSearchPredicates(Root<reservation> root, reservation example) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
