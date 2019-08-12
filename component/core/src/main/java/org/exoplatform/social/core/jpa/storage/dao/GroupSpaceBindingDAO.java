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
import org.exoplatform.social.core.jpa.storage.entity.GroupSpaceBindingEntity;

public interface GroupSpaceBindingDAO extends GenericDAO<GroupSpaceBindingEntity, Long> {

  /**
   * Get groups binding for a specific role(member or manager) in space
   *
   * @param spaceId Id of the space
   * @param role role in the space
   * @return A list of group+membership bindings
   */
  List<GroupSpaceBindingEntity> findGroupSpaceBindingsBySpace(Long spaceId, String role);

  /**
  * Get groups binding for a specific role(member or manager) in space
  *
  * @param group group
  * @param role role in the group
  * @return A list of group+membership bindings
  */
  List<GroupSpaceBindingEntity> findGroupSpaceBindingsByGroup(String group, String role);
}
