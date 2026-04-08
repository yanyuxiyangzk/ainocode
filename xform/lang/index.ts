// Language pack exports
import zhCN from './zh-CN';
import enUS from './en-US';

export type Locale = 'zh-CN' | 'en-US';

export const locales: Record<Locale, typeof zhCN> = {
  'zh-CN': zhCN,
  'en-US': enUS,
};

export const localeNames: Record<Locale, string> = {
  'zh-CN': '中文',
  'en-US': 'English',
};

export { zhCN, enUS };
