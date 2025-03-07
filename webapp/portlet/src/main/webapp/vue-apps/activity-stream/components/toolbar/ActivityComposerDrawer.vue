<!--
  This file is part of the Meeds project (https://meeds.io/).
  Copyright (C) 2022 Meeds Association
  contact@meeds.io
  This program is free software; you can redistribute it and/or
  modify it under the terms of the GNU Lesser General Public
  License as published by the Free Software Foundation; either
  version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
  Lesser General Public License for more details.
  You should have received a copy of the GNU Lesser General Public License
  along with this program; if not, write to the Free Software Foundation,
  Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
-->
<template>
  <exo-drawer
    id="activityComposerDrawer"
    ref="activityComposerDrawer"
    v-model="drawer"
    v-draggable="enabled"
    disable-pull-to-refresh
    right
    fixed>
    <template #title>
      {{ $t('activity.composer.title') }}
    </template>
    <template #content>
      <v-card flat>
        <div v-if="!activityId">
          <div v-if="audienceTypesDisplay" class="mt-1 px-4 pt-4">
            <div v-if="postVisibility">
              <span class="subtitle-1 text-color"> {{ $t('activity.composer.content.title') }} </span>
              <v-radio-group
                v-if="postToNetwork"
                v-model="audienceChoice"
                class="mt-0"
                mandatory>
                <v-radio value="yourNetwork">
                  <template #label>
                    <span class="text-color text-subtitle-2 ms-1"> {{ $t('activity.composer.content.yourNetwork') }}</span>
                  </template>
                </v-radio>
                <v-radio value="oneOfYourSpaces">
                  <template #label>
                    <span class="text-color text-subtitle-2 ms-1"> {{ $t('activity.composer.content.oneOfYourSpaces') }}</span>
                  </template>
                </v-radio>
              </v-radio-group>
              <exo-identity-suggester
                v-if="spaceSuggesterDisplay"
                ref="audienceComposerSuggester"
                v-model="audience"
                :labels="spaceSuggesterLabels"
                :include-users="false"
                :width="220"
                name="audienceComposerSuggester"
                class="user-suggester mt-n2"
                include-spaces
                only-redactor />
            </div>
            <v-list-item v-if="audienceAvatarDisplay" class="text-truncate px-0 mt-n1">
              <exo-space-avatar
                :space-id="spaceId"
                :size="30"
                extra-class="text-truncate"
                avatar />
              <exo-user-avatar
                :profile-id="username"
                :size="spaceId && 25 || 30"
                :extra-class="spaceId && 'ms-n4 mt-6' || ''"
                avatar />
              <v-list-item-content class="py-0 accountTitleLabel text-truncate">
                <v-list-item-title class="font-weight-bold d-flex body-2 mb-0">
                  <exo-space-avatar
                    :space-id="spaceId"
                    extra-class="text-truncate"
                    fullname
                    bold-title
                    link-style
                    username-class />
                </v-list-item-title>
                <v-list-item-subtitle class="d-flex flex-row flex-nowrap">
                  <exo-user-avatar
                    :profile-id="username"
                    extra-class="text-truncate ms-2 me-1"
                    fullname
                    link-style
                    small-font-size
                    username-class />
                </v-list-item-subtitle>
              </v-list-item-content>
              <v-list-item-action class="my-0">
                <v-tooltip bottom>
                  <template #activator="{ on, attrs }">
                    <v-btn
                      icon
                      v-bind="attrs"
                      v-on="on"
                      @click="resetAudienceChoice()">
                      <v-icon size="14">
                        fas fa-redo
                      </v-icon>
                    </v-btn>
                  </template>
                  <span>
                    {{ $t('activity.composer.audience.reset.tooltip') }}
                  </span>
                </v-tooltip>
              </v-list-item-action>
            </v-list-item>
          </div>
        </div>
        <v-card-text>
          <rich-editor
            v-if="drawer"
            ref="activityContent"
            v-model="message"
            :max-length="MESSAGE_MAX_LENGTH"
            :template-params="templateParams"
            :placeholder="composerPlaceholder"
            :object-type="metadataObjectType"
            :object-id="metadataObjectId"
            :max-file-size="$root.maxFileSize"
            :suggester-space-u-r-l="spaceURL"
            :activity-id="activityId"
            class="activityRichEditor"
            ck-editor-type="activityContent"
            context-name="activityComposer"
            use-extra-plugins
            use-draft-management
            autofocus
            @attachments-edited="attachmentsEdit" />
          <div v-if="containInvalidUsers" class="mt-4">{{ $t('activity.composer.invalidUsers.message') }}</div>
        </v-card-text>
        <v-card-actions class="d-flex px-4">
          <extension-registry-components
            :params="extensionParams"
            name="ActivityComposerAction"
            type="activity-composer-action" />
        </v-card-actions>
        <v-divider class="mx-4 my-5" />
        <extension-registry-components
          v-if="!activityId"
          name="ComposerAction"
          type="composer-action-item" />
        <extension-registry-components
          v-if="!activityId"
          :params="extensionParams"
          name="ActivityComposerFooterAction"
          type="activity-composer-footer-action" />
      </v-card>
    </template>
    <template slot="footer">
      <div class="d-flex">
        <v-spacer />
        <v-btn
          id="activityComposerPostButton"
          :disabled="postDisabled"
          :loading="loading"
          :aria-label="$t(`activity.composer.${composerAction}`)"
          type="button"
          class="primary btn no-box-shadow ms-auto"
          @click="postMessage()">
          {{ composerActionLabel }}
        </v-btn>
      </div>
    </template>
  </exo-drawer>
</template>

<script>
export default {
  data() {
    return {
      MESSAGE_MAX_LENGTH: 1300,
      MESSAGE_TIMEOUT: 5000,
      activityId: null,
      spaceId: null,
      spaceURL: eXo.env.portal.spaceUrl,
      message: '',
      files: null,
      templateParams: {},
      drawer: false,
      activityBodyEdited: false,
      activityAttachmentsEdited: false,
      originalBody: '',
      messageEdited: false,
      activityType: null,
      loading: false,
      attachments: null,
      activityToolbarAction: false,
      postToNetwork: eXo.env.portal.postToNetworkEnabled,
      audienceChoice: 'yourNetwork',
      audience: '',
      username: eXo.env.portal.userName,
      containInvalidUsers: false
    };
  },
  computed: {
    composerPlaceholder() {
      return this.$t('activity.composer.placeholder');
    },
    composerAction() {
      return this.activityId && 'update' || 'post';
    },
    composerActionLabel() {
      return this.$t(`activity.composer.${this.composerAction}`);
    },
    extensionParams() {
      return {
        activityId: this.activityId,
        spaceId: this.spaceId,
        files: this.files,
        templateParams: this.templateParams,
        message: this.message,
        maxMessageLength: this.MESSAGE_MAX_LENGTH,
        activityType: this.activityType
      };
    },
    messageLength(){
      return this.message && this.message.length && this.$utils.htmlToText(this.message).length || 0;
    },
    ckEditorInstance() {
      return this.drawer && this.$refs.activityContent || null;
    },
    postDisabled() {
      return (!this.messageLength && !this.activityBodyEdited && !this.activityAttachmentsEdited) || this.messageLength > this.MESSAGE_MAX_LENGTH || this.loading || (!!this.activityId && !this.activityBodyEdited && !this.attachments?.length) || (!this.attachments?.length && !this.messageLength && !this.activityBodyEdited) || (this.postInYourSpacesChoice && !this.audience) || (!this.postToNetwork && !eXo.env.portal.spaceId && !this.audience && !this.messageEdited);
    },
    metadataObjectId() {
      return this.templateParams?.metadataObjectId || this.activityId;
    },
    metadataObjectType() {
      return this.templateParams?.metadataObjectType || 'activity';
    },
    enabled() {
      return eXo.env.portal.editorAttachImageEnabled && this.metadataObjectType?.length && eXo.env.portal.attachmentObjectTypes?.indexOf(this.metadataObjectType) >= 0;
    },
    spaceSuggesterLabels() {
      return {
        placeholder: this.$t('activity.composer.audience.placeholder'),
        noDataLabel: this.$t('activity.composer.audience.noDataLabel'),
      };
    },
    audienceTypesDisplay() {
      return !eXo.env.portal.spaceId;
    },
    postInYourSpacesChoice() {
      return this.audienceChoice === 'oneOfYourSpaces';
    },
    postInYourNetwork() {
      return this.audienceChoice === 'yourNetwork';
    },
    spaceSuggesterDisplay() {
      return (this.postToNetwork && this.postInYourSpacesChoice && !this.audience) || !this.postToNetwork ;
    },
    audienceAvatarDisplay() {
      return this.audience && this.postInYourSpacesChoice;
    },
    postVisibility() {
      return  this.postInYourNetwork || (this.postInYourSpacesChoice && !this.audience);
    }
  },
  watch: {
    message(newVal, oldVal) {
      // Do not compute again this.activityBodyEdited if it's made true
      // once, else, this can lead to performances issue when editing
      // An activity
      if (this.activityId && !this.activityBodyEdited) {
        this.activityBodyEdited = this.$utils.htmlToText(newVal) !== this.$utils.htmlToText(oldVal);
        this.messageEdited = this.$utils.htmlToText(newVal) !== this.$utils.htmlToText(this.originalBody);
      }
    },
    drawer() {
      if (this.drawer) {
        document.dispatchEvent(new CustomEvent('activity-composer-opened'));
      } else {
        document.dispatchEvent(new CustomEvent('activity-composer-closed'));
      }
    },
    audience() {
      this.spaceId = this.audience?.spaceId || '';
      this.spaceURL = this.audience?.remoteId || '';
    },
    audienceChoice(newVal) {
      if (newVal === 'yourNetwork') {
        this.removeAudience();
      }
    },
    spaceURL() {
      this.resetRichEditorData();
    }
  },
  created() {
    document.addEventListener('activity-composer-drawer-open', this.open);
    document.addEventListener('activity-composer-edited', this.isActivityBodyEdited);
    document.addEventListener('activity-composer-closed', this.close);
  },
  methods: {
    isActivityBodyEdited(event) {
      this.activityBodyEdited = (this.messageEdited && this.messageLength) || event.detail !== 0 || (event.detail === 0 && this.messageLength);
    },
    attachmentsEdit(attachments) {
      this.attachments = attachments;
      this.activityAttachmentsEdited = true;
    },
    open(params) {
      params = params && params.detail;
      if (params) {
        this.message = params.activityBody;
        this.originalBody = params.activityBody;
        this.activityId = params.activityId;
        this.spaceId = params.spaceId;
        this.templateParams = params.activityParams || params.templateParams || {};
        this.files = params.files || [];
        this.activityType = params.activityType;
        this.attachments = this.templateParams?.metadatas?.attachments;
        this.activityToolbarAction = params.activityToolbarAction;
      } else {
        this.activityId = null;
        this.spaceId = null;
        this.message = '';
        this.templateParams = {};
        this.files = [];
        this.activityType = [];
      }
      this.$nextTick().then(() => {
        this.activityBodyEdited = false;
        this.messageEdited = false;
        this.$refs.activityComposerDrawer.open();
        document.dispatchEvent(new CustomEvent('message-composer-opened'));
      });
    },
    close() {
      if (this.ckEditorInstance) {
        this.ckEditorInstance.unload();
      }
      this.$nextTick().then(() => {
        this.$refs.activityComposerDrawer.close();
        this.$root.$emit('message-composer-closed');
      });
    },
    postMessage() {
      // Using a ref to the editor component and the getMessage method is mandatory to
      // be sure to get the most up to date value of the message
      let message = this.ckEditorInstance.getMessage();
      const tempdiv = $('<div class=\'temp\'/>').html(message || '');
      tempdiv.find('[data-atwho-at-value]')
        .each(function() {
          const value = $(this).attr('data-atwho-at-value');
          const pattern = new RegExp(`<span class="atwho-inserted" data-atwho-at-query="[^"]*" data-atwho-at-value="${value}"[^>]*>(.*?)</span> </span>`, 'g');
          message = message.replace(pattern, value ? `@${value}` : '');
        });
      this.templateParams.comment = message;
      this.templateParams.default_title = message;
      if (this.activityId) {
        let activityType = this.activityType;
        if (this.templateParams && this.templateParams.link && !this.activityType) {
          activityType = 'LINK_ACTIVITY';
        } else if (this.templateParams && this.templateParams.link === '-') {
          activityType = null;
        }
        this.loading = true;
        this.$activityService.updateActivity(this.activityId, message, activityType, this.files, this.templateParams)
          .then(this.postUpdateMessage)
          .then(() => this.ckEditorInstance && this.ckEditorInstance.saveAttachments())
          .then(() => {
            document.dispatchEvent(new CustomEvent('activity-updated', {detail: this.activityId}));
            this.cleareActivityMessage();
            this.close();
          })
          .catch(error => {
            // eslint-disable-next-line no-console
            console.error(`Error when updating the activity: ${error}`);
            // TODO Display error Message
          })
          .finally(() => this.loading = false);
      } else {
        let activityType = this.activityType;
        if (this.templateParams && this.templateParams.link && !this.activityType) {
          activityType = 'LINK_ACTIVITY';
        }
        if (this.activityType && this.activityType.length !== 0) {
          if (this.activityToolbarAction) {
            document.dispatchEvent(new CustomEvent('post-activity-toolbar-action', {detail: message}));
          } else {
            document.dispatchEvent(new CustomEvent('post-activity', {detail: message}));
          }
        } else {
          this.loading = true;
          this.$activityService.createActivity(message, activityType, this.files, this.spaceId, this.templateParams)
            .then(activity => {
              this.activityId = activity.id;
              this.templateParams = activity.templateParams;
              return this.$nextTick().then(() => activity);
            })
            .then(this.postSaveMessage)
            .then(() => this.ckEditorInstance && this.ckEditorInstance.saveAttachments())
            .then(() => {
              document.dispatchEvent(new CustomEvent('activity-created', {detail: this.activityId}));
              this.cleareActivityMessage();
              this.resetAudienceChoice();
              this.close();
            })
            .catch(error => {
              // eslint-disable-next-line no-console
              console.error(`Error when posting message: ${error}`);
              // TODO Display error Message
            })
            .finally(() => this.loading = false);
        }
      }
    },
    postSaveMessage(activity) {
      const postSaveOperations = extensionRegistry.loadExtensions('activity', 'saveAction');
      if (postSaveOperations?.length) {
        const promises = [];
        postSaveOperations.forEach(extension => {
          if (extension.postSave) {
            const result = extension.postSave(activity);
            if (result?.then) {
              promises.push(result);
            }
          }
        });
        return Promise.all(promises).then(() => activity);
      } else {
        return Promise.resolve(activity);
      }
    },
    postUpdateMessage(activity) {
      const postUpdateOperations = extensionRegistry.loadExtensions('activity', 'updateAction');
      if (postUpdateOperations?.length) {
        const promises = [];
        postUpdateOperations.forEach(extension => {
          if (extension.postUpdate) {
            const result = extension.postUpdate(activity);
            if (result?.then) {
              promises.push(result);
            }
          }
        });
        return Promise.all(promises).then(() => activity);
      } else {
        return Promise.resolve(activity);
      }
    },
    cleareActivityMessage() {
      if (localStorage.getItem('activity-message-activityComposer')) {
        localStorage.removeItem('activity-message-activityComposer');
      }
    },
    resetAudienceChoice() {
      this.audienceChoice = 'yourNetwork';
      this.audience = '';
    },
    removeAudience() {
      this.audience = '';
    },
    replaceValidSuggestedUser(message, profile, pattern) {
      return message.replace(new RegExp(pattern, 'g'), ExtendedDomPurify.purify(`
                      <span class="atwho-inserted" data-atwho-at-query="@${profile.username}" data-atwho-at-value="${profile.username}" contenteditable="false">
                        <span class="exo-mention">${profile.fullname}${profile.isExternal === 'true' ? ` (${  this.$t('UsersManagement.type.external')  })` : ''}<a href="#" class="remove"><i class="uiIconClose uiIconLightGray"></i></a></span>
                      </span>
                    `));
    },
    replaceInvalidSuggestedUser(message, profile, pattern) {
      return message.replace(new RegExp(pattern, 'g'), ExtendedDomPurify.purify(`
                      <span class="atwho-inserted" data-atwho-at-query="@${profile.username}" data-atwho-at-value="" contenteditable="false" title="${this.$t('activity.composer.invalidUser.message')}">
                        <span class="exo-mention">
                          <i aria-hidden="true" class="v-icon notranslate fa fa-exclamation-triangle theme--light orange--text error-color" style="font-size: 16px;">
                          </i>
                          <del>${profile.fullname}${profile.isExternal === 'true' ? ` (${  this.$t('UsersManagement.type.external')  })` : ''}</del>
                          <a href="#" class="remove">
                            <i class="uiIconClose uiIconLightGray"></i>
                          </a>
                        </span>
                      </span>
                    `));
    },
    resetRichEditorData() {
      let message = this.ckEditorInstance.getMessage();
      const mentionedUsers =  message.match(/@([A-Za-z0-9_'.+-]+)/g)?.map(a => a.replace('@', '')) || null;
      this.containInvalidUsers = false;
      if (mentionedUsers?.length) {
        // Clear suggester cache
        document.dispatchEvent(new CustomEvent('suggester-clear-people-cache'));
        Promise
          .all(mentionedUsers.map(username => {
            return this.$identityService.getIdentityByProviderIdAndRemoteId('organization', username)
              .then(identity => {
                if (!identity?.profile) {
                  return null;
                }
                if (this.audience?.spaceId) {
                  return this.$spaceService.isSpaceMember(this.audience.spaceId, username)
                    .then(data => {
                      if (data) {
                        const profile = identity.profile;
                        profile.isMember = data?.isMember === 'true';
                        return profile;
                      } else {
                        return null;
                      }
                    });
                } else {
                  const profile = identity.profile;
                  profile.isMember = true;
                  return profile;
                }
              });
          }))
          .then(userProfiles => userProfiles.filter(p => p))
          .then(userProfiles => {
            const containsExoMentionClass = message.search('exo-mention') >= 0;
            this.containInvalidUsers = !!userProfiles.find(profile => profile.isMember !== true);
            userProfiles.forEach(profile => {
              const pattern = containsExoMentionClass ? `<span class="atwho-inserted" data-atwho-at-query="@${profile.username}"[^>]*>(.*?)</span> </span>` : `@${profile.username}`;
              if (profile.isMember) {
                message = this.replaceValidSuggestedUser(message, profile, pattern);
              } else {
                message = this.replaceInvalidSuggestedUser(message, profile, pattern);
              }
            });
            this.ckEditorInstance?.initCKEditorData?.(message);
          });
      }
    },
  },
};
</script>
