package com.eurodyn.uns.service.daemons.userupdater;

import com.eurodyn.uns.dao.DAOException;
import com.eurodyn.uns.dao.DAOFactory;
import com.eurodyn.uns.dao.IUserDao;
import com.eurodyn.uns.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Map;

/**
 * User updating service for LDAP servers.
 * @author George Sofianos
 */
public class UserUpdaterServiceLdap implements UserUpdaterService {

  private static final Logger LOGGER = LoggerFactory.getLogger(UserUpdaterServiceLdap.class);

  private IUserDao userSourceDao;
  private IUserDao userDestinationDao;

  public UserUpdaterServiceLdap(IUserDao userSourceDao, IUserDao userDestinationDao) {
    this.userSourceDao = userSourceDao;
    this.userDestinationDao = userDestinationDao;
  }

  public UserUpdaterServiceLdap() {
    DAOFactory ldapFactory = DAOFactory.getDAOFactory(DAOFactory.LDAP);
    this.userSourceDao = ldapFactory.getUserDao();
    DAOFactory hibernateFactory = DAOFactory.getDAOFactory(DAOFactory.HIBERNATE);
    this.userDestinationDao = hibernateFactory.getUserDao();
  }

  /**
   * Fetches all users and updates their details in database
   * @throws DAOException
   */
  public void synchronizeUsers() throws DAOException {
    List<User> ldapUsers = this.userSourceDao.findAllUsers();
    for (User ldapUser : ldapUsers) {
      this.synchronize(ldapUser);
    }
  }

  /**
   * Updates user details
   * @param ldapUser user fetched from LDAP Server
   * @throws DAOException
   */
  private void synchronize(User ldapUser) throws DAOException {
    String username = ldapUser.getExternalId();
    User user = this.userDestinationDao.findUser(username);

    if (user == null) {
      return;
    }

    if (UserUtil.equalUsers(user, ldapUser)) {
      //logger.info("User " + user.getFullName() + " has not been updated");
      return;
    }

    user.setFullName(ldapUser.getFullName());
    user.setDisabledFlag(ldapUser.getDisabledFlag());
    Map deliveryAddresses = user.getDeliveryAddresses();

    deliveryAddresses.put(new Integer(1), ldapUser.getDeliveryAddresses().get(1));
    user.setDeliveryAddresses(ldapUser.getDeliveryAddresses());
    this.userDestinationDao.updateUser(user);
    LOGGER.info("User " + user.getFullName() + " updated");
  }
}
