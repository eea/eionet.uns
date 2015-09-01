package com.eurodyn.uns.service.daemons.userupdater;

import com.eurodyn.uns.dao.DAOException;

/**
 *
 * @author George Sofianos
 */
public interface UserUpdaterService {

  void synchronizeUsers() throws DAOException;
}
