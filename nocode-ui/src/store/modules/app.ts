import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useAppStore = defineStore('app', () => {
  const sidebar = ref({
    opened: localStorage.getItem('sidebarStatus') ? !!+localStorage.getItem('sidebarStatus')! : true,
    withoutAnimation: false,
    hide: false
  })
  const device = ref<'desktop' | 'mobile'>('desktop')
  const size = ref(localStorage.getItem('size') || 'default')

  const toggleSidebar = (withoutAnimation: boolean) => {
    if (sidebar.value.hide) {
      return false
    }
    sidebar.value.opened = !sidebar.value.opened
    sidebar.value.withoutAnimation = withoutAnimation
    if (sidebar.value.opened) {
      localStorage.setItem('sidebarStatus', '1')
    } else {
      localStorage.setItem('sidebarStatus', '0')
    }
  }

  const closeSidebar = ({ withoutAnimation }: { withoutAnimation: boolean }) => {
    localStorage.setItem('sidebarStatus', '0')
    sidebar.value.opened = false
    sidebar.value.withoutAnimation = withoutAnimation
  }

  const toggleDevice = (value: 'desktop' | 'mobile') => {
    device.value = value
  }

  const setSize = (value: string) => {
    size.value = value
    localStorage.setItem('size', value)
  }

  return {
    sidebar,
    device,
    size,
    toggleSidebar,
    closeSidebar,
    toggleDevice,
    setSize
  }
})
