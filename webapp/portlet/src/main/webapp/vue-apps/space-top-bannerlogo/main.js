import './initComponents.js';
const appId = 'SpaceTopBannerLogo';
if (extensionRegistry) {
  const components = extensionRegistry.loadComponents('ExoPopover');
  if (components && components.length > 0) {
    components.forEach(cmp => {
      Vue.component(cmp.componentName, cmp.componentOptions);
    });
  }
}
const vuetify = Vue.prototype.vuetifyOptions;

const lang = eXo && eXo.env.portal.language || 'en';
const url = `${eXo.env.portal.context}/${eXo.env.portal.rest}/i18n/bundle/locale.portlet.Portlets-${lang}.json`;

let popover;
export function init(params) {
  exoi18n.loadLanguageAsync(lang, url).then(i18n => {
    // init Vue app when locale resources are ready
    popover = Vue.createApp({
      data: function() {
        return {
          spaceId: params.id,
          isFavorite: params.isFavorite,
          muted: params.muted === 'true',
          isMember: params.isMember,
          logoPath: params.logoPath,
          portalPath: params.portalPath,
          imageClass: params.imageClass,
          logoTitle: params.logoTitle,
          titleClass: params.titleClass,
          membersNumber: params.membersNumber,
          spaceDescription: params.spaceDescription,
          managers: params.managers,
          homePath: params.homePath,
        };
      },
      template: `<exo-space-logo-banner
                    id="SpaceTopBannerLogo"
                    :space-id="spaceId"
                    :is-favorite="isFavorite"
                    :muted="muted"
                    :is-member="isMember"
                    :logo-path="logoPath" 
                    :portal-path="portalPath"
                    :logo-title="logoTitle" 
                    :members-number="membersNumber"
                    :managers="managers"
                    :home-path="homePath"
                    :space-description="spaceDescription" />`,
      i18n,
      vuetify,
    }, `#${appId}`, 'social-portlet');
  });
}
export function destroy() {
  if (popover) {
    popover.$destroy();
  }
}
