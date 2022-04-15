package dst.ass1.jpa.dao.impl;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.List;

public abstract class AbstractDAO<T> {

    private Class<T> classT;

    static EntityManager entityManager;

    public final void setClassT(Class<T> classToSet) {
        this.classT = classToSet;
    }

    public static void setEntityManager(EntityManager entityManager) {
        AbstractDAO.entityManager = entityManager;
    }

    //entityManager.find() doesn't work with interfaces     =>      .createQuery
    public T findById(Long id) {
        try {
            return entityManager.createQuery("select c from " + classT.getName() + " c where c.id = " + id.toString(), classT).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<T> findAll() {
        try {
            return entityManager.createQuery("from " + classT.getName(), classT).getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }
}
