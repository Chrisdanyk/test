/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.reservation.web.service;

import javax.persistence.EntityManager;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.reservation.web.model.user;

/**
 *
 * @author Ben Kafirongo
 */
public class UserService extends AbstractService<user> {

    public UserService() {
        super(user.class);
    }

    @Override
    protected Predicate[] getSearchPredicates(Root<user> root, user example) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public user findByUsernamePwd(String username, String pwd) {
        EntityManager em = super.getEntityManager();
        em.createQuery("SELECT user u from u where u.");
        return null;
    }

}
