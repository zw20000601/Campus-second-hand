<template>
  <n-config-provider :theme-overrides="themeOverrides" :locale="zhCN" :date-locale="dateZhCN">
    <n-message-provider>
      <n-loading-bar-provider>
        <n-dialog-provider>
          <RouterView />
        </n-dialog-provider>
      </n-loading-bar-provider>
    </n-message-provider>
  </n-config-provider>
</template>

<script setup lang="ts">
import {
  NConfigProvider, NMessageProvider, NLoadingBarProvider, NDialogProvider,
  zhCN, dateZhCN,
} from 'naive-ui'

const themeOverrides = {
  common: {
    primaryColor: '#0CBFB5',
    primaryColorHover: '#0AADA3',
    primaryColorPressed: '#089B91',
    primaryColorSuppl: '#0CBFB5',
  },
}
</script>
