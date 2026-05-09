<template>
  <div class="page" v-loading="isAuthorize">
    <div v-show="!isAuthorize">
      <div class="logo">
        <router-link :to="{ name: 'index' }">
          <div class="icon"></div>
        </router-link>
      </div>
      
      <!-- 登录表单 -->
      <login 
        v-if="!showRegister && !showChangePassword" 
        :code="code" 
        :error_description="error_description" 
        :gitRepo="gitRepo" 
        :gitRepoOwner="gitRepoOwner"
        @switch-to-register="showRegister = true"
      />
      
      <!-- 注册表单 -->
      <register 
        v-if="showRegister" 
        @close="showRegister = false" 
        @switch-to-login="showRegister = false"
        @success="handleRegisterSuccess"
      />
      
      <!-- 修改密码表单 -->
      <change-password
        v-if="showChangePassword"
        @close="showChangePassword = false"
        @success="handleChangePasswordSuccess"
      />
      
      <bottom-nav/>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref } from 'vue';
import BottomNav from '@/views/nav/bottom.vue';
import Login from '@/views/common/login.vue';
import Register from '@/views/login/register.vue';
import ChangePassword from '@/views/login/change-password.vue';
import { useRoute } from 'vue-router';
import { AUTHORIZE_INDEX, LOGIN_INDEX } from '@/router/path-def';

export default defineComponent({
  components: { BottomNav, Login, Register, ChangePassword },
  props: {
    gitRepo: String,
    gitRepoOwner: String,
    code: String,
    error_description: String,
  },
  setup() {
    const route = useRoute();
    const isAuthorize = ref<boolean>(route.path === AUTHORIZE_INDEX);
    const showRegister = ref<boolean>(false);
    const showChangePassword = ref<boolean>(false);

    const handleRegisterSuccess = () => {
      showRegister.value = false;
    };

    const handleChangePasswordSuccess = () => {
      showChangePassword.value = false;
    };

    return {
      isAuthorize,
      showRegister,
      showChangePassword,
      handleRegisterSuccess,
      handleChangePasswordSuccess,
    };
  },
});
</script>

<style scoped lang="less">
.page {
  width: 100vw;
  height: 100vh;
  padding-top: 15vh;

  .logo {
    margin: 0 auto 15px auto;
    width: 350px;
    display: flex;
    align-items: center;
    justify-content: center;

    .icon {
      width: 150px;
      height: 34px;
      background-image: url('@/assets/svgs/logo/main.svg');
      background-repeat: no-repeat;
      background-size: contain;
      background-position: center center;
    }
  }

  ::v-deep(.login) {
    border-radius: 4px;
    padding: 30px;
    width: 350px;
    min-height: 344px;
  }
}
</style>