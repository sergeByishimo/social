/*
 * This file is part of the Meeds project (https://meeds.io/).
 * Copyright (C) 2020 - 2022 Meeds Association contact@meeds.io
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */

package org.exoplatform.social.core.storage.api;

import java.util.List;

import org.exoplatform.social.core.profileproperty.model.ProfilePropertySetting;


public interface ProfileSettingStorage {

  /**
   * Gets a list of available profile settings.
   *
   * @return the list of profile settings.
   */

  public List<ProfilePropertySetting> getPropertySettings();

  /**
   * Gets a list of available synchronized settings
   *
   * @return {@link List} of {@link ProfilePropertySetting}
   */
  List<ProfilePropertySetting> getSynchronizedPropertySettings();

  ProfilePropertySetting findProfileSettingByName(String name);

  ProfilePropertySetting getProfileSettingById(Long id);

  /**
   * Saves a profilePropertySetting. If isNew is true, creates new profilePropertySetting. If not only updates profilePropertySetting
   * and saves it.
   *
   * @param profilePropertySetting
   * @param isNew
   */
  public ProfilePropertySetting saveProfilePropertySetting(ProfilePropertySetting profilePropertySetting, boolean isNew);

  /**
   * Deletes a profilePropertySetting by profilePropertySetting id.
   *
   * @param id
   */
  public void deleteProfilePropertySetting(Long id);
}
