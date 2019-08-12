/*
 * Copyright (C) 2003-2019 eXo Platform SAS.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.exoplatform.social.core.jpa.storage.dao;

import java.util.List;

import org.exoplatform.commons.api.persistence.GenericDAO;
import org.exoplatform.social.core.jpa.storage.entity.UserSpaceBindingEntity;

public interface UserSpaceBindingDAO extends GenericDAO<UserSpaceBindingEntity, Long> {

  /**
   * Get user bindings for a specific role(member or manager) in space
   *
   * @param spaceId Id of the space
   * @param userName Member in the space
   * @return A list of group+membership bindings
   */
  List<UserSpaceBindingEntity> findUserBindingsBySpace(Long spaceId, String userName);

  /**
   * Get user bindings of a user for a membership (group+role) in space
   *
   * @param group the group
   * @param groupRole the role in group
   * @param userName Member in the space
   * @return A list of group+membership bindings
   */
  List<UserSpaceBindingEntity> findUserBindingsByGroup(String group, String groupRole, String userName);

  /**
   * Get all user bindings for a membership (group+role)
   *
   * @param group the group
   * @param groupRole the role in group
   * @return A list of group+membership bindings
   */
  List<UserSpaceBindingEntity> findUserAllBindingsByGroupMembership(String group, String groupRole);

  /**
   * Get user bindings of a user
   *
   * @param userName the user
   * @return A list of group+membership bindings
   */
  List<UserSpaceBindingEntity> findUserAllBindingsByUser(String userName);

  /**
   * Delete all the bindings of the user
   *
   * @param userName Member in the space
   */
  void deleteAllUserBindings(String userName);

  /**
   * Check if member has binding for this space
   *
   * @param spaceId The space Id.
   * @param userName The space Id.
   */
  boolean hasUserBindings(Long spaceId, String userName);

}
