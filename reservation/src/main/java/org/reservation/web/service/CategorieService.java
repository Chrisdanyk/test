/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.reservation.web.service;

import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.reservation.web.model.categorie;

/**
 *
 * @author Ben Kafirongo
 */
@Service
public class CategorieService extends AbstractService<categorie> {

    public CategorieService() {
        super(categorie.class);
    }

    @Override
    protected Predicate[] getSearchPredicates(Root<categorie> root, categorie example) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
