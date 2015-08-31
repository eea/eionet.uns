package com.eurodyn.uns.service.daemons.userupdater;

import com.eurodyn.uns.model.DeliveryAddress;
import com.eurodyn.uns.model.User;

import java.util.Collection;


/**
 *
 * @author George Sofianos
 */
public final class UserUtil {

  private UserUtil() {
    // never called
  }

  /**
   * Compares User in DB with user fetched from LDAP Server
   * @param dbUser User saved in database
   * @param ldapUser User fetched from LDAP Server
   * @return true if both critical user attributes are equal
   */
  public static boolean equalUsers(User dbUser, User ldapUser) {
    boolean result = true;
    if (!dbUser.getFullName().equals(ldapUser.getFullName())) result = false;
    if (!dbUser.getDisabledFlag().equals(ldapUser.getDisabledFlag())) result = false;
    Collection<DeliveryAddress> dbAddresses = dbUser.getDeliveryAddresses().values();
    Collection<DeliveryAddress> ldapAddresses = ldapUser.getDeliveryAddresses().values();
    String dbMail = "";
    String ldapMail = "";

    for (DeliveryAddress address : dbAddresses) {
      if (address.getDeliveryType().getId() == 1) {
        dbMail = address.getAddress();
      }
    }
    for (DeliveryAddress address : ldapAddresses) {
      if (address.getDeliveryType().getId() == 1) {
        ldapMail = address.getAddress();
      }
    }
    if (!dbMail.equals(ldapMail)) result = false;
    return result;
  }

}
