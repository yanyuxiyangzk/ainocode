/**
 * 判断是否为外部链接
 */
export const isExternal = (path: string): boolean => {
  return /^(https?:|mailto:|tel:)/.test(path)
}

/**
 * 判断是否为 http/https 链接
 */
export const isHttp = (url: string): boolean => {
  return url.indexOf('http://') !== -1 || url.indexOf('https://') !== -1
}

/**
 * 校验用户名
 */
export const validUsername = (str: string): boolean => {
  const validMap = ['admin', 'editor']
  return validMap.indexOf(str.trim()) >= 0
}

/**
 * 校验 URL
 */
export const validURL = (url: string): boolean => {
  const reg = /^(https?|ftp):\/\/([a-zA-Z0-9.-]+(:[a-zA-Z0-9.&%$-]+)*@)*((25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9][0-9]?)(\.(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9][0-9]?)){3}|([a-zA-Z0-9-]+\.)*[a-zA-Z0-9-]+\.(com|edu|gov|int|mil|net|org|biz|arpa|info|name|pro|aero|coop|museum|[a-zA-Z]{2}))(:[0-9]+)*(\/($|[a-zA-Z0-9.,?'\\+&%$#=~_-]+))*$/
  return reg.test(url)
}

/**
 * 校验小写字母
 */
export const validLowerCase = (str: string): boolean => {
  const reg = /^[a-z]+$/
  return reg.test(str)
}

/**
 * 校验大写字母
 */
export const validUpperCase = (str: string): boolean => {
  const reg = /^[A-Z]+$/
  return reg.test(str)
}

/**
 * 校验邮箱
 */
export const validEmail = (email: string): boolean => {
  const reg = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/
  return reg.test(email)
}

/**
 * 校验手机号
 */
export const validPhone = (phone: string): boolean => {
  const reg = /^1[3-9]\d{9}$/
  return reg.test(phone)
}
