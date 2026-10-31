import { createApp } from "vue";
import { createPinia } from "pinia";
import ElementPlus from "element-plus";
import zhCn from "element-plus/es/locale/lang/zh-cn";
import * as ElementPlusIconsVue from "@element-plus/icons-vue";
import "element-plus/dist/index.css";
import App from "./App.vue";
import router from "./router";
import "@/assets/styles/index.scss";
import { permission } from "@/directives/permission";
import HasPermission from "@/components/HasPermission.vue";

const app = createApp(App);

// 全局注册Element Plus图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component);
}

// 注册权限指令
app.directive("permission", permission);

// 注册权限组件
app.component("HasPermission", HasPermission);

app.use(createPinia());
app.use(ElementPlus, { locale: zhCn });
app.use(router);

app.mount("#app");
