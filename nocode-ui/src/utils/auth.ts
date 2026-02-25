import Cookies from 'js-cookie'

const TokenKey = 'Admin-Token'

export const getToken = (): string | undefined => {
  return Cookies.get(TokenKey)
}

export const setToken = (token: string): string | undefined => {
  return Cookies.set(TokenKey, token)
}

export const removeToken = (): void => {
  Cookies.remove(TokenKey)
}
