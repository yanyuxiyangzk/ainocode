// i18n composable for form designer
import { ref, computed, inject, type InjectionKey, type Ref } from 'vue';
import { zhCN, enUS, locales, localeNames, type Locale } from '@/lang';

// i18n context key
export const I18N_CONTEXT_KEY: InjectionKey<ReturnType<typeof createI18nContext>> = Symbol('i18n');

export interface I18nContext {
  locale: Ref<Locale>;
  t: (path: string, params?: Record<string, string | number>) => string;
  setLocale: (locale: Locale) => void;
  availableLocales: Locale[];
  localeNames: Record<Locale, string>;
}

function createI18nContext() {
  // Default to browser locale or zh-CN
  const browserLocale = navigator.language.toLowerCase();
  const defaultLocale: Locale = browserLocale.startsWith('en') ? 'en-US' : 'zh-CN';

  const locale = ref<Locale>(defaultLocale);

  const currentTranslations = computed(() => locales[locale.value]);

  function t(path: string, params?: Record<string, string | number>): string {
    const keys = path.split('.');
    let value: unknown = currentTranslations.value;

    for (const key of keys) {
      if (value && typeof value === 'object' && key in value) {
        value = (value as Record<string, unknown>)[key];
      } else {
        console.warn(`[i18n] Translation not found: ${path}`);
        return path;
      }
    }

    if (typeof value !== 'string') {
      console.warn(`[i18n] Translation is not a string: ${path}`);
      return path;
    }

    // Replace parameters like {0}, {name} with provided values
    if (params) {
      return value.replace(/\{(\w+)\}/g, (match, key) => {
        return params[key]?.toString() ?? match;
      });
    }

    return value;
  }

  function setLocale(newLocale: Locale) {
    if (locales[newLocale]) {
      locale.value = newLocale;
      // Persist to localStorage
      localStorage.setItem('form-designer-locale', newLocale);
    } else {
      console.warn(`[i18n] Unknown locale: ${newLocale}`);
    }
  }

  function initLocale() {
    const savedLocale = localStorage.getItem('form-designer-locale') as Locale | null;
    if (savedLocale && locales[savedLocale]) {
      locale.value = savedLocale;
    }
  }

  return {
    locale,
    t,
    setLocale,
    initLocale,
    availableLocales: Object.keys(locales) as Locale[],
    localeNames,
  };
}

export function provideI18n() {
  const context = createI18nContext();
  // Initialize locale from localStorage
  context.initLocale();
  return context;
}

export function useI18n(): I18nContext {
  const context = inject(I18N_CONTEXT_KEY);
  if (!context) {
    // Return a default context if not provided (for standalone usage)
    return createI18nContext();
  }
  return context;
}

// Helper composable for components that need reactive translations
export function useLocale() {
  const { locale, setLocale, availableLocales, localeNames } = useI18n();

  const isZhCN = computed(() => locale.value === 'zh-CN');
  const isEnUS = computed(() => locale.value === 'en-US');

  function toggleLocale() {
    setLocale(locale.value === 'zh-CN' ? 'en-US' : 'zh-CN');
  }

  return {
    locale,
    setLocale,
    isZhCN,
    isEnUS,
    toggleLocale,
    availableLocales,
    localeNames,
  };
}

// Helper to get widget label
export function useWidgetLabel() {
  const { t } = useI18n();

  function getWidgetLabel(widgetName: string): string {
    return t(`widgets.${widgetName}` as any);
  }

  function getContainerLabel(widgetName: string, propName: string): string {
    return t(`containers.${widgetName}.${propName}` as any);
  }

  return {
    getWidgetLabel,
    getContainerLabel,
  };
}

// Helper for form property labels
export function useFormPropsLabel() {
  const { t } = useI18n();

  function getLayoutLabel(layout: string): string {
    return t(`formProps.layouts.${layout}` as any);
  }

  function getLabelAlignLabel(align: string): string {
    return t(`formProps.labelAligns.${align}` as any);
  }

  function getSizeLabel(size: string): string {
    return t(`formProps.sizes.${size}` as any);
  }

  return {
    getLayoutLabel,
    getLabelAlignLabel,
    getSizeLabel,
  };
}
